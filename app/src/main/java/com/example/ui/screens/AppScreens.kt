package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreensContainer(viewModel: AppViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    MyApplicationTheme(darkTheme = isDarkMode, dynamicColor = false) {
        // Enforce native Arabic Right-To-Left layout direction globally
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                contentWindowInsets = WindowInsets.safeDrawing
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            is Screen.Splash -> SplashScreen(viewModel)
                            is Screen.Home -> HomeScreen(viewModel)
                            is Screen.AskAi -> AskAiScreen(viewModel)
                            is Screen.Summaries -> SummariesScreen(viewModel)
                            is Screen.Ministerial -> MinisterialScreen(viewModel)
                            is Screen.Exams -> ExamsScreen(viewModel)
                            is Screen.Performance -> PerformanceScreen(viewModel)
                            is Screen.Subscriptions -> SubscriptionsScreen(viewModel)
                            is Screen.AdminDashboard -> AdminDashboardScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 1. SPLASH SCREEN (شاشة البداية)
// ==========================================
@Composable
fun SplashScreen(viewModel: AppViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF0D9488).copy(alpha = 0.15f))
                drawRect(
                    brush = Brush.verticalGradient(colors)
                )
            }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // App Emblem with dual glowing rings
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .drawBehind {
                        drawCircle(
                            color = Color(0xFF2DD4BF).copy(alpha = 0.1f),
                            radius = size.minDimension / 1.6f
                        )
                        drawCircle(
                            color = Color(0xFF2DD4BF).copy(alpha = 0.2f),
                            radius = size.minDimension / 2.0f
                        )
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0D9488), Color(0xFF2DD4BF))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Emblem",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App Name with elegant display typography
            Text(
                text = "مدرس اليمن الذكي",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle indicating the target grade and curriculum context
            Text(
                text = "المساعد التعليمي الذكي للمنهج الرسمي\nالصف الثالث الثانوي - القسم العلمي",
                fontSize = 16.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Welcome educational slogan
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "ذكاء اصطناعي مقيد بالكتاب والوزاريات المعتمدة لمنع التخمين",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE2E8F0),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Start Button
            Button(
                onClick = { viewModel.navigateTo(Screen.Home) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0D9488)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(56.dp)
                    .testTag("start_button")
            ) {
                Text(
                    text = "ابدأ التعلم الآن",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    tint = Color.White
                )
            }
        }
    }
}

// ==========================================
// 2. HOME SCREEN (الصفحة الرئيسية)
// ==========================================
@Composable
fun HomeScreen(viewModel: AppViewModel) {
    val stats by viewModel.userStats.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val context = LocalContext.current

    // Rotation list of curriculum formulas for the "Quick Review" banner
    val quickFormulas = listOf(
        Triple("المدى الأفقي للمقذوف", "ف = (ع.^2 * جا 2هـ) / جـ", "يكون أقصى مدى أفقي عندما تكون زاوية الإطلاق هـ = 45 درجة."),
        Triple("زمن تحليق المقذوف", "ز = (2 * ع. * جا هـ) / جـ", "هو الزمن الكلي المقضي في الهواء حتى العودة للأفق."),
        Triple("سعة مكثف كهربائي", "س = ش / جـ", "تقاس بالفاراد، وهي نسبة الشحنة المخزونة لفرق الجهد."),
        Triple("توصيل مكثفات على التوالي", "1 / س_كل = 1/س1 + 1/س2 + ...", "تقل السعة المكافئة الإجمالية للمجموعة وتتحمل جهد أكبر."),
        Triple("توصيل مكثفات على التوازي", "س_كل = س1 + س2 + ...", "تزداد السعة المكافئة الإجمالية وتساوي مجموع السعات.")
    )

    var currentFormulaIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // App Top Bar Style Custom Greeting
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "مرحباً بك يا بطل 🚀",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Text(
                    text = "مدرس اليمن الذكي",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Dark Mode & Subscription Quick Actions
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.toggleDarkMode() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Theme Toggle",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                // Subscription Badge with interactive navigate
                val tier = stats?.subscriptionTier ?: "FREE"
                val (badgeColor, badgeText) = when (tier) {
                    "VIP" -> GoldVIP to "👑 VIP ذكي"
                    "PLUS" -> PurplePlus to "✨ بلس"
                    "STUDENT" -> SkyStudent to "🎓 طالب"
                    else -> BlueFree to "🌟 مجاني"
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = badgeColor.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f)),
                    onClick = { viewModel.navigateTo(Screen.Subscriptions) },
                    modifier = Modifier.height(38.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Stats Strip
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${stats?.solvedQuestionsCount ?: 0}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "الأسئلة المحلولة",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val correct = stats?.correctAnswersCount ?: 0
                    val total = stats?.solvedQuestionsCount ?: 1
                    val rate = if (total > 0) (correct * 100) / total else 0
                    Text(
                        text = "$rate%",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                    Text(
                        text = "معدل النجاح",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(30.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "8 مواد",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "التغطية العلمية",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ⚡ QUICK REVIEW ROTATING BANNER (مراجعة سريعة)
        val formula = quickFormulas[currentFormulaIndex]
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Bolt",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مراجعة سريعة - قوانين هامة",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Next Formula Button
                    IconButton(
                        onClick = {
                            currentFormulaIndex = (currentFormulaIndex + 1) % quickFormulas.size
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Next formula",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = formula.first,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formula.second,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = formula.third,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Action Grid (الخدمات الرئيسية للتطبيق)
        Text(
            text = "الخدمات التعليمية",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val gridItems = listOf(
            GridActionItem("🧠 اسأل الذكاء", "اسأل أي سؤال من المنهج مع مصادره", Icons.Default.Psychology, Screen.AskAi, Color(0xFF2DD4BF)),
            GridActionItem("📚 الملخصات", "ملخصات دروس مبسطة شاملة وقابلة للبحث", Icons.Default.MenuBook, Screen.Summaries, Color(0xFF38BDF8)),
            GridActionItem("📝 الوزاريات", "أسئلة الامتحانات الوزارية للسنوات السابقة", Icons.Default.Quiz, Screen.Ministerial, Color(0xFFFBBF24)),
            GridActionItem("🧪 الاختبارات", "اختبر نفسك مع تصحيح فوري وتقييم ذكي", Icons.Default.Assignment, Screen.Exams, Color(0xFFF43F5E)),
            GridActionItem("📊 أدائي الدراسي", "مراقبة نقاط القوة والضعف للمراجعة", Icons.Default.Analytics, Screen.Performance, Color(0xFF10B981)),
            GridActionItem("⚙️ الإعدادات", "تعديل المظهر، الحساب، وشحن كروت", Icons.Default.Settings, Screen.Subscriptions, Color(0xFF8B5CF6))
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            for (i in gridItems.indices step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        GridCard(gridItems[i], viewModel)
                    }
                    if (i + 1 < gridItems.size) {
                        Box(modifier = Modifier.weight(1f)) {
                            GridCard(gridItems[i + 1], viewModel)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Secret Admin Door
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
            onClick = { viewModel.navigateTo(Screen.AdminDashboard) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Admin",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "الدخول السري للوحة تحكم المعلمين",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

data class GridActionItem(
    val title: String,
    val desc: String,
    val icon: ImageVector,
    val screen: Screen,
    val accentColor: Color
)

@Composable
fun GridCard(item: GridActionItem, viewModel: AppViewModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
        onClick = { viewModel.navigateTo(item.screen) },
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .testTag("grid_card_${item.title.replace(" ", "_")}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(item.accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = item.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.desc,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                    maxLines = 2,
                    lineHeight = 14.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ==========================================
// 3. ASK AI SCREEN (🧠 اسأل الذكاء)
// ==========================================
@Composable
fun AskAiScreen(viewModel: AppViewModel) {
    val state by viewModel.askAiState.collectAsState()
    val books by viewModel.books.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var questionText by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("الفيزياء") }
    var attachedImage by remember { mutableStateOf<Bitmap?>(null) }
    var isSubjectMenuExpanded by remember { mutableStateOf(false) }

    // Standard high-fidelity Android photo picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            // Simulator: Attach custom bitmap mock to trigger OCR
            attachedImage = decodeStringMock()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Header Bar with Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.navigateBack() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = "اسأل مدرس اليمن الذكي 🧠",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Main Scrolling Content
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error banner if any
            if (state.error != null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = state.error!!,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.clearAskAiError() }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            // Input Form Section (Only visible when not running active analysis)
            if (!state.isSolving && !state.isAnalyzing && state.solutionText == null) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // 1. Subject Selector Dropdown
                            Text(
                                text = "اختر المادة التعليمية لحصر البحث:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { isSubjectMenuExpanded = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = selectedSubject, color = MaterialTheme.colorScheme.onSurface)
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Drop", tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                
                                DropdownMenu(
                                    expanded = isSubjectMenuExpanded,
                                    onDismissRequest = { isSubjectMenuExpanded = false }
                                ) {
                                    books.forEach { book ->
                                        DropdownMenuItem(
                                            text = { Text(book.subjectName) },
                                            onClick = {
                                                selectedSubject = book.subjectName
                                                isSubjectMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 2. Question Text Field
                            Text(
                                text = "اكتب سؤالك العلمي هنا:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            
                            OutlinedTextField(
                                value = questionText,
                                onValueChange = { questionText = it },
                                placeholder = { Text("اكتب السؤال هنا بالتفصيل للبحث عنه وحله...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .testTag("question_input_field"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 3. OCR Image Attach Controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                                        contentColor = MaterialTheme.colorScheme.secondary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Gallery")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "صورة السؤال", fontSize = 13.sp)
                                }

                                Button(
                                    onClick = { 
                                        // Simulate Camera capture
                                        attachedImage = decodeStringMock()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                                        contentColor = MaterialTheme.colorScheme.secondary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "تصوير كاميرا", fontSize = 13.sp)
                                }
                            }

                            // Image attachment preview status
                            if (attachedImage != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                    // Custom high-fidelity look showing image crop status
                                        .background(SuccessGreen.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Attached", tint = SuccessGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "تم إرفاق الصورة وتجهيز OCR التلقائي",
                                        fontSize = 11.sp,
                                        color = SuccessGreen,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { attachedImage = null },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = ErrorRed)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // 4. Submit Button
                            Button(
                                onClick = {
                                    viewModel.askAiQuestion(questionText, selectedSubject, attachedImage)
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("submit_ai_question_button")
                            ) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "إرسال وتحليل السؤال", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ACTIVE AI ANALYZER AND LOADER SEQUENCE
            if (state.isAnalyzing || state.isSolving) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(48.dp))
                            
                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = if (state.isAnalyzing) "نظام الفهم الذكي يحلل مفاهيم السؤال..." else "جاري صياغة الحل من الكتاب المعتمد...",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // High fidelity interactive step logs
                            val steps = listOf(
                                "تفكيك السؤال واستخراج المفاهيم العلمية" to state.isAnalyzing,
                                "البحث في كتب الملخصات والوزاريات اليمنية" to (state.isSolving),
                                "مطابقة القوانين واستخراج النصوص المعتمدة" to state.isSolving,
                                "صياغة الرد ومنع أي تخمين خارجي" to state.isSolving
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                steps.forEach { (text, active) ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = if (active) Icons.Default.HourglassBottom else Icons.Default.CheckCircle,
                                            contentDescription = "Status",
                                            tint = if (active) MaterialTheme.colorScheme.primary else SuccessGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = text,
                                            fontSize = 12.sp,
                                            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // RESULTS OF ANALYSIS AND ANSWERS
            if (state.solutionText != null) {
                // Display Question Analysis Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Insights, contentDescription = "Analysis", tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "نتائج تحليل السؤال الذكي",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))

                            // Metadata row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "المادة: ${state.analysisSubject ?: "غير محدد"}", fontSize = 12.sp)
                                    Text(text = "الدرس: ${state.analysisLesson ?: "خارج المنهج"}", fontSize = 12.sp)
                                }
                                Column {
                                    Text(text = "الصعوبة: ${state.difficulty ?: "متوسط"}", fontSize = 12.sp)
                                    Text(
                                        text = "درجة الثقة: ${(state.confidence * 100).toInt()}%",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.isSourceFound) SuccessGreen else ErrorRed
                                    )
                                }
                            }
                        }
                    }
                }

                // Solution Step-by-Step Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.TaskAlt, contentDescription = "Check", tint = SuccessGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "الإجابة النموذجية المعتمدة", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = state.solutionText!!,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Official Grounding Source Card (المصادر)
                if (state.isSourceFound && state.sourceBook != null) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Source, contentDescription = "Source", tint = MaterialTheme.colorScheme.secondary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "المصدر الرسمي المعتمد من وزارة التربية والتعليم",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "${state.sourceBook} - ${state.sourceUnit}\nالدرس: ${state.sourceLesson} (صفحة ${state.sourcePage})",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "« ${state.sourceText} »",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                    lineHeight = 18.sp,
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f))
                                        .padding(8.dp)
                                )
                            }
                        }
                    }
                }

                // Similar past ministerial exam questions if found
                if (state.similarQuestions.isNotEmpty()) {
                    item {
                        Text(
                            text = "أسئلة وزارية مشابهة متكررة:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }

                    items(state.similarQuestions) { q ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "امتحان وزاري سنة ${q.year}م",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "تكرر ${q.frequency} مرات",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = q.questionText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "الحل النموذجي: ${q.explanation}",
                                    fontSize = 11.sp,
                                    color = SuccessGreen,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Reset Action buttons to run another search
                item {
                    Button(
                        onClick = {
                            questionText = ""
                            attachedImage = null
                            viewModel.askAiQuestion("", "", null) // Reset ViewModel State back
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "اسأل سؤالاً آخر", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

// Helper to convert Bitmap mockup
fun decodeStringMock(): Bitmap {
    val size = 50
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    return bitmap
}

// ==========================================
// 4. SUMMARIES SCREEN (📚 الملخصات)
// ==========================================
@Composable
fun SummariesScreen(viewModel: AppViewModel) {
    val searchQuery by viewModel.summarySearchQuery.collectAsState()
    val filteredLessons by viewModel.filteredLessons.collectAsState()
    val books by viewModel.books.collectAsState()

    var activeSubjectFilter by remember { mutableStateOf("الرياضيات") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }, modifier = Modifier.size(48.dp)) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(text = "ملخصات المنهج المعتمدة 📚", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // Live Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.summarySearchQuery.value = it },
            placeholder = { Text("ابحث عن درس، قانون، أو موضوع محدد...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .testTag("summary_search_bar"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Subject Horizontal Select Tabs
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(books) { book ->
                val selected = activeSubjectFilter == book.subjectName
                FilterChip(
                    selected = selected,
                    onClick = { activeSubjectFilter = book.subjectName },
                    label = { Text(book.subjectName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Main List
        val displayLessons = filteredLessons.filter { it.subjectName == activeSubjectFilter }
        if (displayLessons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                        contentDescription = "Empty",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "لا توجد ملخصات تطابق البحث في مادة $activeSubjectFilter.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayLessons) { lesson ->
                    var isExpanded by remember { mutableStateOf(false) }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = lesson.unitName,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = lesson.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "ص ${lesson.pageNumber}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = "Expand",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                }
                            }

                            // Expandable Summary Content
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    
                                    Text(
                                        text = "الخلاصة المعتمدة للدرس:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = lesson.summary,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 18.sp
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "نص الكتاب الوزاري التفصيلي:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = lesson.content,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                                        lineHeight = 17.sp,
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f), RoundedCornerShape(6.dp))
                                            .padding(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. MINISTERIAL QUESTIONS SCREEN (📝 الوزاريات)
// ==========================================
@Composable
fun MinisterialScreen(viewModel: AppViewModel) {
    val books by viewModel.books.collectAsState()
    val activeSubject by viewModel.selectedSubjectForMinisterial.collectAsState()
    val activeUnitFilter by viewModel.selectedUnitForMinisterial.collectAsState()
    val allQuestions by viewModel.ministerialQuestions.collectAsState()

    var unitsInSubject by remember { mutableStateOf<List<String>>(emptyList()) }

    // Resolve units dynamically from DB based on selected subject
    LaunchedEffect(activeSubject, allQuestions) {
        unitsInSubject = listOf("الكل") + allQuestions
            .filter { it.subject == activeSubject }
            .map { it.unitName }
            .distinct()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }, modifier = Modifier.size(48.dp)) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(text = "بنك الأسئلة الوزارية الرسمية 📝", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // Subject selector Horizontal Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(books) { book ->
                val selected = activeSubject == book.subjectName
                FilterChip(
                    selected = selected,
                    onClick = {
                        viewModel.selectedSubjectForMinisterial.value = book.subjectName
                        viewModel.selectedUnitForMinisterial.value = "الكل"
                    },
                    label = { Text(book.subjectName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Unit selector Horizontal Row
        if (unitsInSubject.size > 1) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(unitsInSubject) { unit ->
                    val selected = activeUnitFilter == unit
                    ElevatedFilterChip(
                        selected = selected,
                        onClick = { viewModel.selectedUnitForMinisterial.value = unit },
                        label = { Text(unit) }
                    )
                }
            }
        }

        // Filter Questions List
        val filteredQuestions = allQuestions.filter { q ->
            q.subject == activeSubject && (activeUnitFilter == "الكل" || q.unitName == activeUnitFilter)
        }

        if (filteredQuestions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد أسئلة مضافة في هذا القسم حالياً.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredQuestions) { q ->
                    var isAnswerRevealed by remember { mutableStateOf(false) }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Question header tags
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                    ) {
                                        Text(
                                            text = "${q.year}م",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                                    ) {
                                        Text(
                                            text = "تكرار: ${q.frequency}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = q.difficulty,
                                    fontSize = 11.sp,
                                    color = if (q.difficulty == "صعب") ErrorRed else SuccessGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Question Body
                            Text(
                                text = q.questionText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Options List with indicators
                            val options = listOf(
                                "أ" to q.optionA,
                                "ب" to q.optionB,
                                "ج" to q.optionC,
                                "د" to q.optionD
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                options.forEachIndexed { idx, pair ->
                                    val isCorrect = (idx == 0 && q.correctAnswer == "A") || 
                                                    (idx == 1 && q.correctAnswer == "B") || 
                                                    (idx == 2 && q.correctAnswer == "C") || 
                                                    (idx == 3 && q.correctAnswer == "D")
                                    
                                    val bg = if (isAnswerRevealed && isCorrect) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f)
                                    val border = if (isAnswerRevealed && isCorrect) SuccessGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(bg, RoundedCornerShape(8.dp))
                                            .border(1.dp, border, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(if (isAnswerRevealed && isCorrect) SuccessGreen else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = pair.first,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isAnswerRevealed && isCorrect) Color.White else MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = pair.second, fontSize = 13.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Click to Reveal action
                            Button(
                                onClick = { isAnswerRevealed = !isAnswerRevealed },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAnswerRevealed) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primary,
                                    contentColor = if (isAnswerRevealed) MaterialTheme.colorScheme.primary else Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                Icon(
                                    imageVector = if (isAnswerRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Reveal"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAnswerRevealed) "إخفاء التوضيح" else "كشف الإجابة والحل الوزاري النموذجي",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Reveal explanation details
                            AnimatedVisibility(
                                visible = isAnswerRevealed,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "خطوات الحل التفصيلية (خطوات المنهج):",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = q.explanation,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. EXAMS SCREEN (🧪 الاختبارات الذكية والتصحيح الفوري)
// ==========================================
@Composable
fun ExamsScreen(viewModel: AppViewModel) {
    val state by viewModel.examState.collectAsState()
    val books by viewModel.books.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Header Bar with back controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { 
                    if (state.isExamActive) {
                        viewModel.exitExam()
                    } else {
                        viewModel.navigateBack()
                    }
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(text = "الاختبارات الذكية التلقائية 🧪", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // SCENARIO A: EXAM SELECTION PAGE
        if (!state.isExamActive) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "آلية الاختبار الذكي",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "يقوم النظام بسحب 5 أسئلة عشوائية من الأسئلة الوزارية السابقة للمادة المحددة وتخصيص مؤقت زمني مدته 5 دقائق. بمجرد الانتهاء، ستحصل على تصحيح فوري للمسائل وتحليل لأخطائك واقتراح الدروس التي يجب عليك مراجعتها لتقوية مستواك.",
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                            )
                        }
                    }
                }

                item {
                    Text(text = "اختر مادة الاختبار لبدء التحدي:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                items(books) { book ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = book.subjectName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(text = "امتحان ذكي شامل للوحدات والدروس", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                            Button(
                                onClick = { viewModel.startSmartExam(book.subjectName) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.testTag("start_exam_${book.subjectName}")
                            ) {
                                Text(text = "بدء الاختبار")
                            }
                        }
                    }
                }
            }
        }

        // SCENARIO B: ACTIVE EXAM SCREEN
        if (state.isExamActive && !state.isFinished) {
            val questions = state.questions
            val currentIdx = state.currentQuestionIndex
            val question = questions.getOrNull(currentIdx)

            if (question != null) {
                // Timer & Progress strip
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Timer countdown
                        val minutes = state.timeLeftSeconds / 60
                        val seconds = state.timeLeftSeconds % 60
                        val timeStr = String.format("%02d:%02d", minutes, seconds)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Timer",
                                tint = if (state.timeLeftSeconds < 60) ErrorRed else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "الوقت المتبقي: $timeStr",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (state.timeLeftSeconds < 60) ErrorRed else MaterialTheme.colorScheme.primary
                            )
                        }

                        // Progress
                        Text(
                            text = "السؤال ${currentIdx + 1} من ${questions.size}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Question Box
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = question.questionText,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Radio select options
                    item {
                        val selectedAnswer = state.selectedAnswers[currentIdx]
                        val options = listOf(
                            "A" to question.optionA,
                            "B" to question.optionB,
                            "C" to question.optionC,
                            "D" to question.optionD
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            options.forEach { (optionCode, optionText) ->
                                val selected = selectedAnswer == optionCode
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(
                                        width = if (selected) 2.dp else 1.dp,
                                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                                    ),
                                    onClick = { viewModel.selectExamAnswer(currentIdx, optionCode) },
                                    modifier = Modifier.fillMaxWidth()
                                        .testTag("option_${optionCode}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selected,
                                            onClick = { viewModel.selectExamAnswer(currentIdx, optionCode) }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = optionText, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Prev / Next / Submit buttons
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (currentIdx > 0) {
                                OutlinedButton(
                                    onClick = { viewModel.setExamQuestionIndex(currentIdx - 1) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(48.dp)
                                ) {
                                    Text(text = "السابق")
                                }
                            }
                            
                            if (currentIdx < questions.size - 1) {
                                Button(
                                    onClick = { viewModel.setExamQuestionIndex(currentIdx + 1) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).height(48.dp)
                                ) {
                                    Text(text = "التالي")
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.submitExam() },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                                    modifier = Modifier.weight(1f).height(48.dp)
                                        .testTag("submit_exam_button")
                                ) {
                                    Text(text = "تسليم ورقة الاختبار", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // SCENARIO C: EXAM RESULTS PANEL
        if (state.isExamActive && state.isFinished) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Performance Gauge Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "نتيجة الاختبار الفورية 📊", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            
                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(120.dp)
                                    .drawBehind {
                                        drawCircle(
                                            color = if (state.score >= 50) SuccessGreen.copy(alpha = 0.1f) else ErrorRed.copy(alpha = 0.1f),
                                            radius = size.minDimension / 2
                                        )
                                    }
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${state.score}%",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.score >= 50) SuccessGreen else ErrorRed
                                    )
                                    Text(text = "الدرجة الكلية", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "لقد أجبت بشكل صحيح على ${state.correctAnswersCount} من أصل ${state.questions.size} أسئلة.",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // AI Diagnostics and Review Recommendations
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI", tint = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "توصيات خطة المذاكرة والمراجعة الذكية",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            state.reviewRecommendations.forEach { rec ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkBorder,
                                        contentDescription = "Rec",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = rec,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Exit back Button
                item {
                    Button(
                        onClick = { viewModel.exitExam() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = "العودة للرئيسية")
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. PERFORMANCE SCREEN (📊 أدائي)
// ==========================================
@Composable
fun PerformanceScreen(viewModel: AppViewModel) {
    val stats by viewModel.userStats.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }, modifier = Modifier.size(48.dp)) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(text = "أدائي الدراسي ومراقبة المستويات 📊", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // General status card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "مستوى التمكن الدراسي الكلي", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(14.dp))
                        
                        val correct = stats?.correctAnswersCount ?: 0
                        val total = stats?.solvedQuestionsCount ?: 1
                        val percentage = if (total > 0) (correct.toFloat() / total) else 0f
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "المعدل العام:", fontSize = 13.sp, modifier = Modifier.width(80.dp))
                            LinearProgressIndicator(
                                progress = { percentage },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = if (percentage >= 0.5f) SuccessGreen else ErrorRed
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${(percentage * 100).toInt()}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (percentage >= 0.5f) SuccessGreen else ErrorRed
                            )
                        }
                    }
                }
            }

            // Strengths and Weaknesses dashboard
            item {
                Text(text = "تحليل نقاط القوة والضعف للمواد الأساسية:", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            val subjectAnalyses = listOf(
                Triple("الفيزياء", "قوة تمكن متميزة في المقذوفات", true),
                Triple("الرياضيات", "حاجة لمراجعة درس الأعداد المركبة", false),
                Triple("الكيمياء", "تمكن ممتاز من العناصر الانتقالية الخواص العامة", true),
                Triple("الأحياء", "مستوى معتدل في الهندسة الوراثية وتطبيقاتها", true),
                Triple("القرآن الكريم", "حفظ ممتاز وصحيح لتفسير الآيات والوصايا", true)
            )

            items(subjectAnalyses) { (subject, msg, isStrength) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isStrength) SuccessGreen.copy(alpha = 0.1f) else ErrorRed.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isStrength) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                contentDescription = "Trend",
                                tint = if (isStrength) SuccessGreen else ErrorRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(text = subject, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = msg,
                                fontSize = 12.sp,
                                color = if (isStrength) SuccessGreen else ErrorRed
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 8. SUBSCRIPTIONS AND ACTIVATION CARDS (نظام الاشتراكات والبطاقات)
// ==========================================
@Composable
fun SubscriptionsScreen(viewModel: AppViewModel) {
    val stats by viewModel.userStats.collectAsState()
    val redeemStatus by viewModel.cardRedeemStatus.collectAsState()
    val lastRedeemedTier by viewModel.lastRedeemedTier.collectAsState()

    var cardCode by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // App Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }, modifier = Modifier.size(48.dp)) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(text = "الاشتراكات وشحن بطاقة الفتح 🎟️", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Display active subscription tier card
            item {
                val tier = stats?.subscriptionTier ?: "FREE"
                val (colorAccent, tierTitle, durationDesc) = when (tier) {
                    "VIP" -> Triple(GoldVIP, "العضوية الملكية 👑 VIP", "وصول غير محدود لجميع الخدمات والكتب مع دعم الأجهزة المتعددة.")
                    "PLUS" -> Triple(PurplePlus, "عضوية بلس الممتازة ✨ PLUS", "وصول لجميع كتب المنهج والملخصات والأسئلة الوزارية.")
                    "STUDENT" -> Triple(SkyStudent, "عضوية الطالب 🎓 STUDENT", "وصول لمعظم ملخصات المواد وحل الوزاريات.")
                    else -> Triple(BlueFree, "العضوية المجانية 🌟 FREE", "محدودية الأسئلة اليومية للذكاء الاصطناعي مع ملخصات محدودة.")
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, colorAccent.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "حالة الاشتراك الحالي:",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Card(
                                colors = CardDefaults.cardColors(containerColor = colorAccent.copy(alpha = 0.15f))
                            ) {
                                Text(
                                    text = tier,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorAccent,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(text = tierTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = durationDesc,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 17.sp
                        )

                        if (stats?.subscriptionExpiry ?: 0L > System.currentTimeMillis()) {
                            val expiryDate = Date(stats!!.subscriptionExpiry)
                            val format = SimpleDateFormat("yyyy/MM/ddم", Locale.getDefault())
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "تاريخ انتهاء الصلاحية: ${format.format(expiryDate)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }
                }
            }

            // Serial Activate Code Portal
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "تفعيل كرت الاشتراك الجديد:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "أدخل رقم الكرت (مثال: YMN-92KQ-77LP) المكتوب على الكرت الورقي لترقية حسابك فوراً.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = cardCode,
                            onValueChange = { cardCode = it },
                            placeholder = { Text("مثال: YMN-92KQ-77LP") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("card_serial_input"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { 
                                if (cardCode.isNotEmpty()) {
                                    viewModel.redeemSubscriptionCard(cardCode)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("activate_card_button")
                        ) {
                            Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scan")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "شحن وتفعيل كرت الاشتراك")
                        }

                        // Success / Failure banners
                        if (redeemStatus != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            when (redeemStatus) {
                                "LOADING" -> {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                    }
                                }
                                "SUCCESS" -> {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.15f)),
                                        border = BorderStroke(1.dp, SuccessGreen)
                                    ) {
                                        Text(
                                            text = "تهانينا! 🎉 تم تفعيل كرت الترقية بنجاح إلى الفئة ($lastRedeemedTier). تم فتح جميع الميزات وصلاحيات الذكاء الاصطناعي بنجاح.",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessGreen,
                                            modifier = Modifier.padding(12.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                "INVALID" -> {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.15f)),
                                        border = BorderStroke(1.dp, ErrorRed)
                                    ) {
                                        Text(
                                            text = "كرت الشحن غير صحيح أو تم استخدامه مسبقاً! يرجى التحقق وإعادة المحاولة.",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ErrorRed,
                                            modifier = Modifier.padding(12.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Preloaded card serial guide for demo
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(text = "💡 بطاقات تجريبية متاحة للاستخدام والترقية:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "• VIP (سنة): YMN-VIP-9999", fontSize = 12.sp)
                        Text(text = "• PLUS (٦ شهور): YMN-PLUS-GOLD", fontSize = 12.sp)
                        Text(text = "• STUDENT (٣ شهور): YMN-92KQ-77LP", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// 9. SECRET TEACHERS ADMIN DASHBOARD (لوحة الإدارة السرية)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(viewModel: AppViewModel) {
    val logs by viewModel.studyLogs.collectAsState()
    val books by viewModel.books.collectAsState()
    val scope = rememberCoroutineScope()

    var activeTab by remember { mutableStateOf("LOGS") } // "LOGS", "BOOK", "LESSON", "QUESTION", "CARD"

    // Form states
    var subjectName by remember { mutableStateOf("الفيزياء") }
    var bookTitle by remember { mutableStateOf("") }
    var bookDesc by remember { mutableStateOf("") }

    var lessonUnit by remember { mutableStateOf("") }
    var lessonTitle by remember { mutableStateOf("") }
    var lessonPage by remember { mutableStateOf("1") }
    var lessonContent by remember { mutableStateOf("") }
    var lessonSummary by remember { mutableStateOf("") }

    var qText by remember { mutableStateOf("") }
    var qOptA by remember { mutableStateOf("") }
    var qOptB by remember { mutableStateOf("") }
    var qOptC by remember { mutableStateOf("") }
    var qOptD by remember { mutableStateOf("") }
    var qCorrect by remember { mutableStateOf("A") }
    var qExplanation by remember { mutableStateOf("") }
    var qYear by remember { mutableStateOf("2024") }
    var qDiff by remember { mutableStateOf("متوسط") }

    var cardCode by remember { mutableStateOf("") }
    var cardTier by remember { mutableStateOf("STUDENT") }
    var cardDuration by remember { mutableStateOf("90") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateBack() }, modifier = Modifier.size(48.dp)) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(text = "لوحة الإدارة والمراقبة السرية 🔐", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        // Horizontal navigation tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tabs = listOf(
                "LOGS" to "مراقبة الذكاء",
                "BOOK" to "إضافة كتب",
                "LESSON" to "إضافة دروس",
                "QUESTION" to "إضافة وزاريات",
                "CARD" to "إنشاء كروت"
            )
            items(tabs) { (code, title) ->
                val selected = activeTab == code
                FilterChip(
                    selected = selected,
                    onClick = { activeTab = code },
                    label = { Text(title) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        // Form fields and dashboard containers
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (activeTab) {
                "LOGS" -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "سجل عمليات بحث ومطابقة الذكاء:", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Button(
                                onClick = { viewModel.adminClearLogs() },
                                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(text = "مسح السجلات", color = Color.White)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        if (logs.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = "لا توجد سجلات حالية بعد.", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                            }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(logs) { log ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = "المادة: ${log.subject}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                                Text(text = "درجة الثقة: ${(log.confidence * 100).toInt()}%", fontSize = 10.sp, color = SuccessGreen)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = "سؤال الطالب: ${log.queryText}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = "النص المطابق: ${log.matchedText}", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = "إجابة المدرس الذكي: ${log.responseText}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "BOOK" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "رفع وإدارة الكتب المنهجية:", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        
                        OutlinedTextField(value = subjectName, onValueChange = { subjectName = it }, label = { Text("اسم المادة") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = bookTitle, onValueChange = { bookTitle = it }, label = { Text("عنوان الكتاب") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = bookDesc, onValueChange = { bookDesc = it }, label = { Text("وصف الكتاب") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

                        Button(
                            onClick = {
                                if (bookTitle.isNotEmpty()) {
                                    viewModel.adminAddBook(subjectName, bookTitle, bookDesc)
                                    bookTitle = ""
                                    bookDesc = ""
                                    activeTab = "LOGS"
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "حفظ ورفع الكتاب في قاعدة البيانات")
                        }
                    }
                }

                "LESSON" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "إضافة وتصنيف الدروس المنهجية:", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        
                        OutlinedTextField(value = subjectName, onValueChange = { subjectName = it }, label = { Text("اسم المادة (مثال: الفيزياء)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = lessonUnit, onValueChange = { lessonUnit = it }, label = { Text("عنوان الوحدة") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = lessonTitle, onValueChange = { lessonTitle = it }, label = { Text("عنوان الدرس") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = lessonPage, onValueChange = { lessonPage = it }, label = { Text("رقم الصفحة في الكتاب الوزاري") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = lessonContent, onValueChange = { lessonContent = it }, label = { Text("نص الكتاب الوزاري (هام جداً لمطابقة الذكاء)") }, modifier = Modifier.fillMaxWidth(), minLines = 4)
                        OutlinedTextField(value = lessonSummary, onValueChange = { lessonSummary = it }, label = { Text("ملخص الدرس المبسط للطالب") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

                        Button(
                            onClick = {
                                if (lessonTitle.isNotEmpty() && lessonContent.isNotEmpty()) {
                                    viewModel.adminAddLesson(subjectName, lessonUnit, lessonTitle, lessonPage.toIntOrNull() ?: 1, lessonContent, lessonSummary)
                                    lessonTitle = ""
                                    lessonContent = ""
                                    lessonSummary = ""
                                    activeTab = "LOGS"
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "حفظ وإدراج الدرس")
                        }
                    }
                }

                "QUESTION" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "إضافة الأسئلة الوزارية السابقة والحل النموذجي:", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        
                        OutlinedTextField(value = subjectName, onValueChange = { subjectName = it }, label = { Text("اسم المادة") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = lessonUnit, onValueChange = { lessonUnit = it }, label = { Text("عنوان الوحدة المرتبطة") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = lessonTitle, onValueChange = { lessonTitle = it }, label = { Text("عنوان الدرس المرتبط") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = qText, onValueChange = { qText = it }, label = { Text("نص السؤال الوزاري") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                        
                        OutlinedTextField(value = qOptA, onValueChange = { qOptA = it }, label = { Text("الخيار أ") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = qOptB, onValueChange = { qOptB = it }, label = { Text("الخيار ب") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = qOptC, onValueChange = { qOptC = it }, label = { Text("الخيار ج") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = qOptD, onValueChange = { qOptD = it }, label = { Text("الخيار د") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = qCorrect, onValueChange = { qCorrect = it }, label = { Text("الإجابة الصحيحة (A أو B أو C أو D)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = qExplanation, onValueChange = { qExplanation = it }, label = { Text("شرح الحل النموذجي خطوة بخطوة") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                        OutlinedTextField(value = qYear, onValueChange = { qYear = it }, label = { Text("سنة الامتحان") }, modifier = Modifier.fillMaxWidth())

                        Button(
                            onClick = {
                                if (qText.isNotEmpty()) {
                                    viewModel.adminAddQuestion(
                                        subjectName, lessonUnit, lessonTitle, qText, qOptA, qOptB, qOptC, qOptD, qCorrect, qExplanation, qYear.toIntOrNull() ?: 2024, qDiff
                                    )
                                    qText = ""
                                    qOptA = ""
                                    qOptB = ""
                                    qOptC = ""
                                    qOptD = ""
                                    activeTab = "LOGS"
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "حفظ السؤال الوزاري")
                        }
                    }
                }

                "CARD" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "توليد كروت شحن اشتراكات جديدة:", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        
                        OutlinedTextField(value = cardCode, onValueChange = { cardCode = it }, label = { Text("كود الكرت الفريد (مثال: YMN-XXXX-XXXX)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = cardTier, onValueChange = { cardTier = it }, label = { Text("فئة الاشتراك (FREE / STUDENT / PLUS / VIP)") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = cardDuration, onValueChange = { cardDuration = it }, label = { Text("مدة الصلاحية بالأيام (مثال: 90)") }, modifier = Modifier.fillMaxWidth())

                        Button(
                            onClick = {
                                if (cardCode.isNotEmpty()) {
                                    viewModel.adminGenerateCard(cardCode, cardTier, cardDuration.toIntOrNull() ?: 90, 1)
                                    cardCode = ""
                                    activeTab = "LOGS"
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "إنشاء وتفعيل الكرت")
                        }
                    }
                }
            }
        }
    }
}
