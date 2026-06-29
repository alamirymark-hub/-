package com.example.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class CurriculumRepository(private val db: AppDatabase) {

    val allBooks: Flow<List<Book>> = db.bookDao().getAllBooks()
    val allLessons: Flow<List<Lesson>> = db.lessonDao().getAllLessons()
    val allQuestions: Flow<List<MinisterialQuestion>> = db.ministerialQuestionDao().getAllQuestions()
    val userStats: Flow<UserStats?> = db.userStatsDao().getUserStats()
    val allStudyLogs: Flow<List<StudyLog>> = db.studyLogDao().getAllStudyLogs()

    fun getLessonsBySubject(subject: String): Flow<List<Lesson>> =
        db.lessonDao().getLessonsBySubject(subject)

    fun searchLessons(query: String): Flow<List<Lesson>> =
        db.lessonDao().searchLessons(query)

    fun getQuestionsBySubject(subject: String): Flow<List<MinisterialQuestion>> =
        db.ministerialQuestionDao().getQuestionsBySubject(subject)

    suspend fun getUnitsBySubject(subject: String): List<String> =
        db.ministerialQuestionDao().getUnitsBySubject(subject)

    suspend fun logStudySession(
        query: String,
        subject: String,
        unit: String,
        lesson: String,
        confidence: Float,
        matchedText: String,
        responseText: String
    ) {
        val log = StudyLog(
            queryText = query,
            subject = subject,
            unitName = unit,
            lessonName = lesson,
            confidence = confidence,
            matchedText = matchedText,
            responseText = responseText
        )
        db.studyLogDao().insertStudyLog(log)
    }

    suspend fun clearLogs() {
        db.studyLogDao().clearLogs()
    }

    suspend fun redeemCard(code: String): Card? {
        val cleanCode = code.trim().uppercase()
        val card = db.cardDao().getCardByCode(cleanCode)
        if (card != null && !card.isRedeemed) {
            val updatedCard = card.copy(isRedeemed = true, redeemDate = System.currentTimeMillis())
            db.cardDao().updateCard(updatedCard)
            
            // Update User Stats with subscription tier and extend time
            val currentStats = db.userStatsDao().getUserStats().firstOrNull() ?: UserStats()
            val expiryTime = System.currentTimeMillis() + (card.durationDays * 24L * 60L * 60L * 1000L)
            val updatedStats = currentStats.copy(
                subscriptionTier = card.tier,
                subscriptionExpiry = expiryTime
            )
            db.userStatsDao().insertUserStats(updatedStats)
            return updatedCard
        }
        return null
    }

    suspend fun updateUserStats(stats: UserStats) {
        db.userStatsDao().insertUserStats(stats)
    }

    suspend fun updateBook(book: Book) {
        db.bookDao().insertBooks(listOf(book))
    }

    suspend fun updateLesson(lesson: Lesson) {
        db.lessonDao().insertLessons(listOf(lesson))
    }

    suspend fun updateQuestion(question: MinisterialQuestion) {
        db.ministerialQuestionDao().insertQuestions(listOf(question))
    }

    suspend fun createActivationCard(code: String, tier: String, durationDays: Int, deviceCount: Int) {
        val card = Card(
            code = code.trim().uppercase(),
            tier = tier,
            durationDays = durationDays,
            deviceCount = deviceCount,
            isRedeemed = false
        )
        db.cardDao().insertCard(card)
    }

    suspend fun prepopulateIfNeeded() {
        if (db.bookDao().getCount() > 0) return

        // 1. Insert Books
        val subjects = listOf(
            "القرآن الكريم", "التربية الإسلامية", "اللغة العربية", "اللغة الإنجليزية",
            "الرياضيات", "الفيزياء", "الكيمياء", "الأحياء"
        )
        val books = subjects.mapIndexed { idx, sub ->
            Book(
                id = idx + 1,
                subjectName = sub,
                title = "كتاب $sub - الثالث الثانوي العلمي",
                description = "المنهج الرسمي المعتمد من وزارة التربية والتعليم في الجمهورية اليمنية.",
                imageUrl = ""
            )
        }
        db.bookDao().insertBooks(books)

        // 2. Insert User Stats
        db.userStatsDao().insertUserStats(
            UserStats(
                id = 1,
                solvedQuestionsCount = 28,
                correctAnswersCount = 22,
                subscriptionTier = "FREE",
                subscriptionExpiry = 0L
            )
        )

        // 3. Insert Activation Cards (Preloaded for the user)
        val activationCards = listOf(
            Card("YMN-92KQ-77LP", "STUDENT", 90, 1),
            Card("YMN-PLUS-GOLD", "PLUS", 180, 2),
            Card("YMN-VIP-9999", "VIP", 365, 4)
        )
        db.cardDao().insertCards(activationCards)

        // 4. Insert Lessons (Curriculum Chapters/Summaries used as grounding source text)
        val initialLessons = listOf(
            Lesson(
                id = 1,
                bookId = 5,
                subjectName = "الرياضيات",
                unitName = "الوحدة الأولى: الأعداد المركبة",
                title = "الدرس الأول: تعريف العدد المركب وصورته الجبرية",
                pageNumber = 12,
                content = "العدد المركب هو عدد يكتب على الصورة س + ت ص، حيث س، ص عددان حقيقيان، وت هو الوحدة التخيلية وتساوي جذر -1. يسمى س الجزء الحقيقي وص الجزء التخيلي.",
                summary = "العدد المركب ز = س + ت ص. س = الحقيقي، ص = التخيلي. ت² = -1. الصورة الجبرية تستخدم لتمثيل الأعداد في المستوي العقدي (مستوي أرجاند)."
            ),
            Lesson(
                id = 2,
                bookId = 5,
                subjectName = "الرياضيات",
                unitName = "الوحدة الثانية: التكامل وتطبيقاته",
                title = "الدرس الأول: التكامل غير المحدود وقواعده الأساسية",
                pageNumber = 56,
                content = "التكامل هو العملية العكسية للتفاضل. قواعد التكامل الأساسية: تكامل س^ن بالنسبة لـ س هو (س^(ن+1))/(ن+1) + ث حيث ن لا تساوي -1.",
                summary = "تكامل القوة: ∫ س^ن د س = س^(ن+1) / (ن+1) + ث. تكامل الجيب: ∫ جا س د س = - جتا س + ث. تكامل جيب التمام: ∫ جتا س د س = جا س + ث."
            ),
            Lesson(
                id = 3,
                bookId = 6,
                subjectName = "الفيزياء",
                unitName = "الوحدة الأولى: الحركة الدائرية والمقذوفات",
                title = "الدرس الثاني: حركة المقذوفات في بعدين",
                pageNumber = 24,
                content = "المقذوف هو جسم يطلق في الهواء بزاوية هـ مع الأفق ويتأثر فقط بقوة الجاذبية الأرضية. زمن التحليق ز = (2 ع. جا هـ) / جـ. المدى الأفقي ف = (ع.^2 جا 2هـ) / جـ. أقصى ارتفاع ص = (ع.^2 جا^2 هـ) / 2جـ.",
                summary = "معادلات المقذوفات: أقصى ارتفاع يصل إليه المقذوف يعتمد على مربع المركبة الرأسية للسرعة الابتدائية ع.ي = ع. جا هـ. المدى الأفقي يكون أقصى ما يمكن عندما تكون زاوية الإطلاق هـ = 45 درجة."
            ),
            Lesson(
                id = 4,
                bookId = 6,
                subjectName = "الفيزياء",
                unitName = "الوحدة الثالثة: الدوائر الكهربائية والالكترونيات",
                title = "الدرس الثالث: المكثف الكهربائي وسعته",
                pageNumber = 104,
                content = "المكثف هو جهاز يستخدم لتخزين الشحنة الكهربائية والطاقة. سعة المكثف س = ش / جـ، حيث ش الشحنة وجـ فرق الجهد. السعة تقاس بالفاراد. عند التوصيل على التوالي: 1/س_كلي = 1/س1 + 1/س2. على التوازي: س_كلي = س1 + س2.",
                summary = "قوانين المكثفات: س = ش / جـ. توصيل المكثفات: على التوازي تجمع السعات لزيادة السعة الكلية. على التوالي تقل السعة الكلية ويتحمل فرق جهد أعلى."
            ),
            Lesson(
                id = 5,
                bookId = 7,
                subjectName = "الكيمياء",
                unitName = "الوحدة الأولى: العناصر الانتقالية",
                title = "الدرس الأول: الخواص العامة للعناصر الانتقالية",
                pageNumber = 8,
                content = "العناصر الانتقالية هي العناصر التي يمتلك غلافها الفرعي d أو f إلكترونات غير ممتلئة في الحالة الذرية أو في أي من حالات تأكسدها. تتميز بتعدد حالات تأكسدها ونشاطها الحفزي وتكوينها لمركبات ملونة.",
                summary = "الخواص العامة للـ d-block: 1. تعدد حالات التأكسد بسبب تقارب طاقتي المستويين (4s) و(3d). 2. بارامغناطيسية لوجود إلكترونات مفردة. 3. تكوين محاليل ملونة."
            ),
            Lesson(
                id = 6,
                bookId = 8,
                subjectName = "الأحياء",
                unitName = "الوحدة الأولى: الهندسة الوراثية وتطبيقاتها",
                title = "الدرس الثاني: تقنية إعادة توليد الحمض النووي DNA",
                pageNumber = 42,
                content = "تقنية الهندسة الوراثية تعتمد على عزل جين معين ودمجه مع ناقل مناسب (مثل البلازميد) وتكاثره في خلية مضيفة لإنتاج بروتين مفيد مثل الأنسولين البشري. تستخدم إنزيمات القطع لقص الـ DNA وإنزيمات الربط لربط القطع الجينية.",
                summary = "الهندسة الوراثية: 1. عزل الجين المطلوب. 2. قصه بإنزيمات القطع المحددة. 3. ربطه بالبلازميد بإنزيم الربط. 4. إدخاله في بكتيريا لإنتاج كميات تجارية من البروتين المستهدف."
            ),
            Lesson(
                id = 7,
                bookId = 1,
                subjectName = "القرآن الكريم",
                unitName = "سورة لقمان",
                title = "الدرس الأول: تفسير الآيات (1-11) ومنهج لقمان الحكيم",
                pageNumber = 5,
                content = "تبدأ سورة لقمان بوصف القرآن الكريم بأنه هدى ورحمة للمحسنين، وتنتقل لعرض قصة لقمان الحكيم ووصاياه العظيمة لابنه التي تحذر من الشرك وتأمر ببر الوالدين وإقامة الصلاة والأمر بالمعروف والنهي عن المنكر والصبر.",
                summary = "من وصايا لقمان: التحذير التام من الشرك بالله كأعظم ذنب (إن الشرك لظلم عظيم)، بر الوالدين حتى لو جاهدا على الشرك، إقامة الصلاة والأمر بالمعروف والنهي عن المنكر مع الصبر على ما أصاب العبد."
            )
        )
        db.lessonDao().insertLessons(initialLessons)

        // 5. Insert Ministerial Questions (الوزاريات الرسمية السابقة)
        val initialQuestions = listOf(
            // Physics
            MinisterialQuestion(
                id = 1,
                subject = "الفيزياء",
                unitName = "الوحدة الأولى: الحركة الدائرية والمقذوفات",
                lessonName = "الدرس الثاني: حركة المقذوفات في بعدين",
                questionText = "يصل المقذوف إلى أقصى مدى أفقي ممكن له عندما تكون زاوية قذفه هـ مع الأفق مساوية لـ:",
                optionA = "15 درجة",
                optionB = "30 درجة",
                optionC = "45 درجة",
                optionD = "90 درجة",
                correctAnswer = "C",
                explanation = "المدى الأفقي ف = (ع.^2 * جا 2هـ) / جـ. لكي يكون ف أكبر ما يمكن يجب أن تكون قيمة جا 2هـ أكبر ما يمكن وهي تساوي 1. بالتالي 2هـ = 90 درجة، أي هـ = 45 درجة.",
                year = 2023,
                difficulty = "سهل",
                frequency = 5
            ),
            MinisterialQuestion(
                id = 2,
                subject = "الفيزياء",
                unitName = "الوحدة الثالثة: الدوائر الكهربائية والالكترونيات",
                lessonName = "الدرس الثالث: المكثف الكهربائي وسعته",
                questionText = "عند توصيل ثلاثة مكثفات سعة كل منها (9 ميكروفاراد) على التوالي، فإن السعة المكافئة الكلية للمجموعة تساوي:",
                optionA = "27 ميكروفاراد",
                optionB = "9 ميكروفاراد",
                optionC = "3 ميكروفاراد",
                optionD = "1 ميكروفاراد",
                correctAnswer = "C",
                explanation = "في التوصيل على التوالي: 1/س_كلي = 1/س1 + 1/س2 + 1/س3. بما أن السعات متساوية، فإن س_كلي = سعة المكثف الواحد / عدد المكثفات = 9 / 3 = 3 ميكروفاراد.",
                year = 2024,
                difficulty = "متوسط",
                frequency = 3
            ),
            // Maths
            MinisterialQuestion(
                id = 3,
                subject = "الرياضيات",
                unitName = "الوحدة الأولى: الأعداد المركبة",
                lessonName = "الدرس الأول: تعريف العدد المركب وصورته الجبرية",
                questionText = "قيمة المقدار ت^18 تساوي:",
                optionA = "1",
                optionB = "-1",
                optionC = "ت",
                optionD = "-ت",
                correctAnswer = "B",
                explanation = "قوى الوحدة التخيلية ت: نقوم بقسمة الأس على 4. الباقي من قسمة 18 على 4 هو 2 (لأن 18 = 4 * 4 + 2). بالتالي ت^18 = ت^2 = -1.",
                year = 2022,
                difficulty = "سهل",
                frequency = 4
            ),
            // Chemistry
            MinisterialQuestion(
                id = 4,
                subject = "الكيمياء",
                unitName = "الوحدة الأولى: العناصر الانتقالية",
                lessonName = "الدرس الأول: الخواص العامة للعناصر الانتقالية",
                questionText = "العنصر الانتقالي الذي يتميز بأنه يمتلك حالة تأكسد وحيدة هي (+3) هو عنصر:",
                optionA = "الحديد Fe",
                optionB = "السكانديوم Sc",
                optionC = "النحاس Cu",
                optionD = "المنجنيز Mn",
                correctAnswer = "B",
                explanation = "عنصر السكانديوم Sc (العدد الذري 21) يمتلك توزيعاً إلكترونياً [Ar] 4s^2 3d^1. يفقد إلكترونات s و d معاً ليكون حالة التأكسد المستقرة الوحيدة وهي +3 ليصبح توزيعاً مشابهاً للغاز الخامل.",
                year = 2024,
                difficulty = "متوسط",
                frequency = 2
            ),
            // Holy Quran
            MinisterialQuestion(
                id = 5,
                subject = "القرآن الكريم",
                unitName = "سورة لقمان",
                lessonName = "الدرس الأول: تفسير الآيات (1-11) ومنهج لقمان الحكيم",
                questionText = "ما هو أعظم ذنب حذر منه لقمان الحكيم ابنه في أول وصاياه كما ورد في السورة؟",
                optionA = "عقوق الوالدين",
                optionB = "الشرك بالله",
                optionC = "أكل مال اليتيم",
                optionD = "التكبر والغرور",
                correctAnswer = "B",
                explanation = "الوصية الأولى للقمان لابنه كانت النهي الشديد عن الشرك بالله مقروناً بتعليل ذلك: 'يا بني لا تشرك بالله إن الشرك لظلم عظيم'. وهذا ما يجعله أعظم ذنب حذر منه.",
                year = 2023,
                difficulty = "سهل",
                frequency = 6
            )
        )
        db.ministerialQuestionDao().insertQuestions(initialQuestions)
    }
}
