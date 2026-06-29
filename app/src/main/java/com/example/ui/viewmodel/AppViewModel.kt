package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID

// Sealed class representing different screens in the app
sealed class Screen {
    object Splash : Screen()
    object Home : Screen()
    object AskAi : Screen()
    object Summaries : Screen()
    object Ministerial : Screen()
    object Exams : Screen()
    object Performance : Screen()
    object Subscriptions : Screen()
    object AdminDashboard : Screen()
}

// UI State for the Ask AI screen
data class AskAiUiState(
    val isAnalyzing: Boolean = false,
    val isSolving: Boolean = false,
    val error: String? = null,
    
    // Analysis phase results
    val analysisSubject: String? = null,
    val analysisUnit: String? = null,
    val analysisLesson: String? = null,
    val difficulty: String? = null,
    val confidence: Float = 0f,
    val isSourceFound: Boolean = false,

    // Solution phase results
    val solutionText: String? = null,
    val analysisText: String? = null,
    val sourceBook: String? = null,
    val sourceUnit: String? = null,
    val sourceLesson: String? = null,
    val sourcePage: Int? = null,
    val sourceText: String? = null,
    
    // Similar questions from db
    val similarQuestions: List<MinisterialQuestion> = emptyList()
)

// UI State for Exams
data class ExamUiState(
    val isExamActive: Boolean = false,
    val subject: String = "",
    val questions: List<MinisterialQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<Int, String> = emptyMap(), // Question Index -> "A"/"B"/"C"/"D"
    val timeLeftSeconds: Int = 300,
    val isFinished: Boolean = false,
    val score: Int = 0,
    val correctAnswersCount: Int = 0,
    val reviewRecommendations: List<String> = emptyList()
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = CurriculumRepository(db)

    // Navigation State
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Splash)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Screen navigation stack for back button support
    private val navigationStack = mutableListOf<Screen>()

    // Core Data flows from Room
    val books: StateFlow<List<Book>> = repository.allBooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lessons: StateFlow<List<Lesson>> = repository.allLessons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ministerialQuestions: StateFlow<List<MinisterialQuestion>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userStats: StateFlow<UserStats?> = repository.userStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val studyLogs: StateFlow<List<StudyLog>> = repository.allStudyLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // App state
    val isLoggedIn = MutableStateFlow(false)
    val userEmail = MutableStateFlow("")
    val isDarkMode = MutableStateFlow(true) // Default to beautiful dark mode!

    // Search queries
    val summarySearchQuery = MutableStateFlow("")
    val filteredLessons: StateFlow<List<Lesson>> = summarySearchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                repository.allLessons
            } else {
                repository.searchLessons(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Ask AI state
    private val _askAiState = MutableStateFlow(AskAiUiState())
    val askAiState: StateFlow<AskAiUiState> = _askAiState.asStateFlow()

    // Exam state
    private val _examState = MutableStateFlow(ExamUiState())
    val examState: StateFlow<ExamUiState> = _examState.asStateFlow()
    private var examTimerJob: Job? = null

    // Selected state for filters
    val selectedSubjectForMinisterial = MutableStateFlow("الفيزياء")
    val selectedUnitForMinisterial = MutableStateFlow("الكل")

    init {
        // Prepopulate the database with Yemeni curriculum on startup if it's empty
        viewModelScope.launch(Dispatchers.IO) {
            repository.prepopulateIfNeeded()
        }
    }

    // Navigation
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            navigationStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack() {
        if (navigationStack.isNotEmpty()) {
            _currentScreen.value = navigationStack.removeAt(navigationStack.size - 1)
        } else {
            _currentScreen.value = Screen.Home
        }
    }

    // Toggle theme
    fun toggleDarkMode() {
        isDarkMode.value = !isDarkMode.value
    }

    // Subscription & Cards
    val cardRedeemStatus = MutableStateFlow<String?>(null) // "SUCCESS", "INVALID", "REDEEMED_ALREADY"
    val lastRedeemedTier = MutableStateFlow<String?>(null)

    fun redeemSubscriptionCard(code: String) {
        viewModelScope.launch(Dispatchers.IO) {
            cardRedeemStatus.value = "LOADING"
            val redeemedCard = repository.redeemCard(code)
            if (redeemedCard != null) {
                cardRedeemStatus.value = "SUCCESS"
                lastRedeemedTier.value = redeemedCard.tier
                delay(2000)
                cardRedeemStatus.value = null
            } else {
                cardRedeemStatus.value = "INVALID"
                delay(2000)
                cardRedeemStatus.value = null
            }
        }
    }

    // Ask AI (🧠 اسأل الذكاء) with OCR simulation & exact curriculum grounding
    fun askAiQuestion(questionText: String, subject: String, attachedImage: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            _askAiState.value = AskAiUiState(isAnalyzing = true)
            
            // 1. OCR simulation & image analysis if image attached
            var finalQuestion = questionText.trim()
            if (attachedImage != null && finalQuestion.isEmpty()) {
                // Simulate highly polished OCR extracting text
                delay(1500) // Image cleaning, noise removal, cropping animation
                finalQuestion = when (subject) {
                    "الفيزياء" -> "يصل المقذوف إلى أقصى مدى أفقي ممكن له عندما تكون زاوية قذفه هـ مع الأفق مساوية لـ 45؟ أثبت ذلك."
                    "الرياضيات" -> "احسب قيمة المقدار ت^18."
                    "الكيمياء" -> "ما هي الخواص المغناطيسية لعنصر السكانديوم؟"
                    "القرآن الكريم" -> "اذكر وصايا لقمان الحكيم لابنه كما وردت في سورة لقمان."
                    else -> "ما هو تعريف مكثف السعة وكيف نوصله؟"
                }
            }

            if (finalQuestion.isEmpty()) {
                _askAiState.value = AskAiUiState(error = "الرجاء كتابة سؤال أو رفع صورة السؤال أولاً.")
                return@launch
            }

            // 2. Search local database to find official grounding text
            val lessonsInSubject = db.lessonDao().getLessonsBySubject(subject).firstOrNull() ?: emptyList()
            var matchedLesson: Lesson? = null
            
            // Simple keyword-based semantic matching
            val keywords = finalQuestion.lowercase().split(" ", "،", "؟", "أوجد", "احسب", "ما", "كيف")
                .filter { it.length > 2 }
            
            var maxMatches = 0
            for (lesson in lessonsInSubject) {
                var matches = 0
                for (keyword in keywords) {
                    if (lesson.title.lowercase().contains(keyword) || 
                        lesson.content.lowercase().contains(keyword) || 
                        lesson.summary.lowercase().contains(keyword)) {
                        matches++
                    }
                }
                if (matches > maxMatches) {
                    maxMatches = matches
                    matchedLesson = lesson
                }
            }

            // Also check previous exams questions for matches
            val similarQuestionsFromDb = db.ministerialQuestionDao().getQuestionsBySubject(subject).firstOrNull() ?: emptyList()
            val filteredSimilar = similarQuestionsFromDb.filter { q ->
                keywords.any { k -> q.questionText.lowercase().contains(k) }
            }.take(3)

            // If we have a matching curriculum lesson, we ground the answer on it.
            // If not, we check if we can match any other lessons.
            val sourceTextToUse: String
            val sourceBookTitle: String
            val sourceUnitTitle: String
            val sourceLessonTitle: String
            val sourcePageNum: Int
            val isSourceFound: Boolean

            if (matchedLesson != null && maxMatches >= 1) {
                sourceTextToUse = matchedLesson.content
                sourceBookTitle = "كتاب " + matchedLesson.subjectName
                sourceUnitTitle = matchedLesson.unitName
                sourceLessonTitle = matchedLesson.title
                sourcePageNum = matchedLesson.pageNumber
                isSourceFound = true
            } else {
                // No source matched in curriculum! This activates the strict fallback rule.
                sourceTextToUse = ""
                sourceBookTitle = ""
                sourceUnitTitle = ""
                sourceLessonTitle = ""
                sourcePageNum = 0
                isSourceFound = false
            }

            // Update state to displaying analysis
            _askAiState.value = _askAiState.value.copy(
                isAnalyzing = false,
                isSolving = true,
                analysisSubject = subject,
                analysisUnit = if (isSourceFound) sourceUnitTitle else "غير محدد",
                analysisLesson = if (isSourceFound) sourceLessonTitle else "غير متوفر في المنهج",
                difficulty = if (isSourceFound) "متوسط" else "غير معروف",
                confidence = if (isSourceFound) 0.98f else 0.10f,
                isSourceFound = isSourceFound
            )

            // 3. Construct exact system instructions to prevent guessing
            val systemInstruction = """
                أنت "مدرس اليمن الذكي"، مساعد تعليمي ذكي مخصص حصرياً لمنهج الثالث الثانوي العلمي اليمني.
                مهمتك الإجابة على سؤال الطالب بناءً على المصدر الرسمي المعتمد المرفق فقط.
                
                قواعد هامة جداً:
                1. لا تجب من ذاكرتك الخارجية ولا تخمن.
                2. اعتمد فقط وحصرياً على نص الكتاب الوزاري المرفق في هذا السياق.
                3. إذا كان السياق المرفق فارغاً، أو لا يحتوي على معلومات كافية لحل السؤال، أو كان السؤال خارج منهج ثالث ثانوي علمي يمني، يجب عليك رفض الإجابة تماماً.
                   أجب فقط بالنص التالي حرفياً بدون أي إضافات:
                   "عذراً، لم أتمكن من العثور على مصدر رسمي موثق لهذا السؤال في قاعدة بيانات المنهج اليمني المعتمد لثالث ثانوي علمي."
                4. إذا كان السؤال قابلاً للحل بناءً على السياق، قم بصياغة الإجابة باللغة العربية الفصحى بشكل مرتب كالتالي:
                   - **الحل**: شرح كامل ومفصل للحل خطوة بخطوة مع توضيح القوانين الرياضية أو الفيزيائية المستخدمة.
                   - **التحليل**: شرح المفاهيم العلمية المرتبطة بالسؤال وكيف تم الربط بينها للوصول للحل.
                   - **المصادر**: اذكر اسم الكتاب والوحدة والدرس والصفحة والنص الحقيقي المستخدم من السياق.
            """.trimIndent()

            val prompt = """
                سؤال الطالب: $finalQuestion
                
                المصدر المعتمد من الكتاب الوزاري المرفق:
                ${if (isSourceFound) "الكتاب: $sourceBookTitle\nالوحدة: $sourceUnitTitle\nالدرس: $sourceLessonTitle\nالصفحة: $sourcePageNum\nالنص الحقيقي: $sourceTextToUse" else "لا يوجد مصدر مطابق في قاعدة البيانات للمنهج الرسمي."}
            """.trimIndent()

            // 4. Call Gemini API
            val response = GeminiClient.generateContent(prompt, systemInstruction)
            
            // Log study session for stats / admin monitoring
            repository.logStudySession(
                query = finalQuestion,
                subject = subject,
                unit = if (isSourceFound) sourceUnitTitle else "خارج المنهج",
                lesson = if (isSourceFound) sourceLessonTitle else "خارج المنهج",
                confidence = if (isSourceFound) 0.98f else 0.10f,
                matchedText = sourceTextToUse,
                responseText = response
            )

            if (response == "ERROR_API_KEY_MISSING") {
                _askAiState.value = AskAiUiState(
                    error = "مفتاح الذكاء الاصطناعي (Gemini API Key) غير مضبوط! الرجاء إضافته عبر لوحة الأسرار (Secrets Panel) لتفعيل الذكاء الاصطناعي."
                )
            } else if (response.startsWith("ERROR_")) {
                _askAiState.value = AskAiUiState(
                    error = "حدث خطأ أثناء الاتصال بالخادم الذكي. يرجى التحقق من الاتصال بالإنترنت والمحاولة مجدداً."
                )
            } else {
                // Parse solution and analysis from response safely
                _askAiState.value = AskAiUiState(
                    isAnalyzing = false,
                    isSolving = false,
                    analysisSubject = subject,
                    analysisUnit = if (isSourceFound) sourceUnitTitle else "غير معروف",
                    analysisLesson = if (isSourceFound) sourceLessonTitle else "خارج المنهج المعتمد",
                    difficulty = if (isSourceFound) "متوسط" else "غير محدد",
                    confidence = if (isSourceFound) 0.98f else 0.0f,
                    isSourceFound = isSourceFound,
                    
                    solutionText = response,
                    sourceBook = if (isSourceFound) sourceBookTitle else null,
                    sourceUnit = if (isSourceFound) sourceUnitTitle else null,
                    sourceLesson = if (isSourceFound) sourceLessonTitle else null,
                    sourcePage = if (isSourceFound) sourcePageNum else null,
                    sourceText = if (isSourceFound) sourceTextToUse else null,
                    similarQuestions = filteredSimilar
                )
            }
        }
    }

    fun clearAskAiError() {
        _askAiState.value = _askAiState.value.copy(error = null)
    }

    // Exams (قسم الاختبارات الذكية)
    fun startSmartExam(subject: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val allSubjectQuestions = db.ministerialQuestionDao().getQuestionsBySubject(subject).firstOrNull() ?: emptyList()
            if (allSubjectQuestions.isEmpty()) {
                // In case no questions in DB, create some dummy ones on the fly for robust execution
                val fallbackQuestions = listOf(
                    MinisterialQuestion(
                        subject = subject,
                        unitName = "الوحدة الأولى",
                        lessonName = "الدرس الأول",
                        questionText = "سؤال تجريبي 1 في مادة $subject: ما هو أساس هذا المفهوم؟",
                        optionA = "الخيار الأول",
                        optionB = "الخيار الثاني المعتمد",
                        optionC = "الخيار الثالث",
                        optionD = "الخيار الرابع",
                        correctAnswer = "B",
                        explanation = "الخيار الثاني هو المعتمد في المنهج الوزاري.",
                        year = 2024,
                        difficulty = "سهل"
                    ),
                    MinisterialQuestion(
                        subject = subject,
                        unitName = "الوحدة الأولى",
                        lessonName = "الدرس الثاني",
                        questionText = "سؤال تجريبي 2 في مادة $subject: اختر الإجابة الصحيحة:",
                        optionA = "الإجابة أ الصحيحة",
                        optionB = "الإجابة ب",
                        optionC = "الإجابة ج",
                        optionD = "الإجابة د",
                        correctAnswer = "A",
                        explanation = "الإجابة أ هي الصحيحة علمياً.",
                        year = 2024,
                        difficulty = "متوسط"
                    )
                )
                _examState.value = ExamUiState(
                    isExamActive = true,
                    subject = subject,
                    questions = fallbackQuestions,
                    currentQuestionIndex = 0,
                    selectedAnswers = emptyMap(),
                    timeLeftSeconds = 300,
                    isFinished = false
                )
            } else {
                // Shuffle questions to make it a "Smart Random Exam"
                val examQuestions = allSubjectQuestions.shuffled().take(5)
                _examState.value = ExamUiState(
                    isExamActive = true,
                    subject = subject,
                    questions = examQuestions,
                    currentQuestionIndex = 0,
                    selectedAnswers = emptyMap(),
                    timeLeftSeconds = 300,
                    isFinished = false
                )
            }

            // Start countdown timer
            examTimerJob?.cancel()
            examTimerJob = viewModelScope.launch(Dispatchers.Main) {
                while (_examState.value.timeLeftSeconds > 0 && !_examState.value.isFinished) {
                    delay(1000)
                    _examState.value = _examState.value.copy(
                        timeLeftSeconds = _examState.value.timeLeftSeconds - 1
                    )
                }
                if (_examState.value.timeLeftSeconds == 0 && !_examState.value.isFinished) {
                    submitExam()
                }
            }
        }
    }

    fun selectExamAnswer(questionIndex: Int, option: String) {
        val currentAnswers = _examState.value.selectedAnswers.toMutableMap()
        currentAnswers[questionIndex] = option
        _examState.value = _examState.value.copy(selectedAnswers = currentAnswers)
    }

    fun setExamQuestionIndex(index: Int) {
        if (index in 0 until _examState.value.questions.size) {
            _examState.value = _examState.value.copy(currentQuestionIndex = index)
        }
    }

    fun submitExam() {
        examTimerJob?.cancel()
        val currentState = _examState.value
        val questions = currentState.questions
        val selectedAnswers = currentState.selectedAnswers
        
        var correctCount = 0
        val recommendations = mutableListOf<String>()

        questions.forEachIndexed { index, q ->
            val userAnswer = selectedAnswers[index]
            if (userAnswer == q.correctAnswer) {
                correctCount++
            } else {
                // Add lesson recommendation for missed questions
                val rec = "ننصحك بمراجعة درس [${q.lessonName}] في [${q.unitName}] لمادة ${currentState.subject}."
                if (!recommendations.contains(rec)) {
                    recommendations.add(rec)
                }
            }
        }

        val scorePercentage = (correctCount.toFloat() / questions.size * 100).toInt()

        _examState.value = currentState.copy(
            isFinished = true,
            score = scorePercentage,
            correctAnswersCount = correctCount,
            reviewRecommendations = if (recommendations.isEmpty()) listOf("عمل رائع ومتميز! لقد أجبت على جميع الأسئلة بشكل صحيح تماماً.") else recommendations
        )

        // Update local statistics reactively in database
        viewModelScope.launch(Dispatchers.IO) {
            val stats = repository.userStats.firstOrNull() ?: UserStats()
            val updatedStats = stats.copy(
                solvedQuestionsCount = stats.solvedQuestionsCount + questions.size,
                correctAnswersCount = stats.correctAnswersCount + correctCount
            )
            repository.updateUserStats(updatedStats)
        }
    }

    fun exitExam() {
        examTimerJob?.cancel()
        _examState.value = ExamUiState()
        navigateTo(Screen.Home)
    }

    // Secret Admin Dashboard Management (لوحة الإدارة السرية)
    fun adminAddBook(subjectName: String, title: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val book = Book(subjectName = subjectName, title = title, description = description, imageUrl = "")
            repository.updateBook(book)
        }
    }

    fun adminAddLesson(subjectName: String, unitName: String, title: String, pageNumber: Int, content: String, summary: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val lesson = Lesson(
                bookId = 1,
                subjectName = subjectName,
                unitName = unitName,
                title = title,
                pageNumber = pageNumber,
                content = content,
                summary = summary
            )
            repository.updateLesson(lesson)
        }
    }

    fun adminAddQuestion(
        subject: String,
        unitName: String,
        lessonName: String,
        questionText: String,
        optionA: String,
        optionB: String,
        optionC: String,
        optionD: String,
        correctAnswer: String,
        explanation: String,
        year: Int,
        difficulty: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val question = MinisterialQuestion(
                subject = subject,
                unitName = unitName,
                lessonName = lessonName,
                questionText = questionText,
                optionA = optionA,
                optionB = optionB,
                optionC = optionC,
                optionD = optionD,
                correctAnswer = correctAnswer,
                explanation = explanation,
                year = year,
                difficulty = difficulty
            )
            repository.updateQuestion(question)
        }
    }

    fun adminGenerateCard(code: String, tier: String, durationDays: Int, deviceCount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createActivationCard(code, tier, durationDays, deviceCount)
        }
    }

    fun adminClearLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearLogs()
        }
    }
}
