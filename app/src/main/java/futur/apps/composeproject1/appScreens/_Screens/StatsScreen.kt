package futur.apps.composeproject1.appScreens._Screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import futur.apps.composeproject1.RoomDatabase.userroom.UserCategoryEntity
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuestionEntity
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuizViewModel
import futur.apps.composeproject1.quizsystem.ui.theme.progressResultColor
import futur.apps.composeproject1.utils.QuizMode
import futur.apps.composeproject1.viewmodels.QuizViewModel
import futur.apps.composeproject1.viewmodels.StatsUiState
import me.bytebeats.views.charts.bar.BarChart
import me.bytebeats.views.charts.bar.BarChartData
import me.bytebeats.views.charts.bar.render.bar.SimpleBarDrawer
import me.bytebeats.views.charts.bar.render.label.SimpleLabelDrawer
import me.bytebeats.views.charts.bar.render.xaxis.SimpleXAxisDrawer
import me.bytebeats.views.charts.bar.render.yaxis.SimpleYAxisDrawer
import me.bytebeats.views.charts.simpleChartAnimation
import kotlin.math.roundToInt

/* ============================================================
   📊 Stats Screen — Unified (Built-in / User-Created)
   ============================================================ */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StatsScreen(
    quizViewModel: QuizViewModel = hiltViewModel(),
    userQuizViewModel: UserQuizViewModel = hiltViewModel(),
    onGoHome: (() -> Unit)? = null,
    onGoToQuiz: (() -> Unit)? = null
) {
    // --- Collect Data ---
    val builtInStats by quizViewModel.stats.collectAsState()
    val userCategories by userQuizViewModel.categories.collectAsState(initial = emptyList())
    val userQuestionsMap by remember { mutableStateOf<Map<Int, List<UserQuestionEntity>>>(emptyMap()) }

    // Derived user stats (in future: compute actual progress)
    val userStats by remember(userCategories, userQuestionsMap) {
        derivedStateOf { userCategoriesToStats(userCategories, userQuestionsMap) }
    }

    var selectedSource by remember { mutableIntStateOf(0) } // 0 = built-in, 1 = user
    var selectedTab by remember { mutableIntStateOf(0) }

    val stats = if (selectedSource == 0) builtInStats else userStats
    val totalAnswers = stats.totalCorrectAnswers + stats.totalWrongAnswers
    val accuracy =
        if (totalAnswers > 0) (stats.totalCorrectAnswers * 100f / totalAnswers).roundToInt() else 0

    if (stats.isLoading) {
        LoadingScreen()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* 🔘 Source Selector */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            FilterChip(
                selected = selectedSource == 0,
                onClick = { selectedSource = 0 },
                label = { Text("Built-in") }
            )
            Spacer(modifier = Modifier.width(12.dp))
            FilterChip(
                selected = selectedSource == 1,
                onClick = { selectedSource = 1 },
                label = { Text("User") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        /* 🧠 General Stats */
        GeneralStatsGrid(stats, accuracy)
        Spacer(modifier = Modifier.height(24.dp))

        /* 🗂 Tabs */
        StatsTabs(selectedTab) { selectedTab = it }

        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "Tab switch"
        ) { tab ->
            when (tab) {
                0 -> StatsBarChart(stats)
                1 -> CategoryStatsList(
                    stats = stats,
                    currentMode = if (selectedSource == 0) QuizMode.BUILT_IN else QuizMode.USER_CREATED
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        /* 🎯 Action Buttons */
        ActionsSection(
            onReset = {
                if (selectedSource == 0) quizViewModel.resetStats()
                else userQuizViewModel.resetUserStats() // optional method in user viewModel
            },
            onGoHome = onGoHome,
            onGoToQuiz = onGoToQuiz
        )
    }
}

/* ============================================================
   🧮 General Stats Grid
   ============================================================ */
@Composable
private fun GeneralStatsGrid(stats: StatsUiState, accuracy: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StatCard(
                "Total Quizzes", stats.totalQuizzes.toString(),
                getGradientColor(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
                Modifier.weight(1f)
            )
            StatCard(
                "Correct", stats.totalCorrectAnswers.toString(),
                getGradientColor(Color(0xFF4CAF50), Color(0xFF66BB6A)),
                Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StatCard(
                "Wrong", stats.totalWrongAnswers.toString(),
                getGradientColor(Color(0xFFD32F2F), Color(0xFFE57373)),
                Modifier.weight(1f)
            )
            StatCard(
                "Accuracy", "$accuracy%",
                getGradientColor(Color(0xFF1976D2), Color(0xFF64B5F6)),
                Modifier.weight(1f)
            )
        }
    }
}

/* ============================================================
   📈 Single Stat Card
   ============================================================ */
@Composable
private fun StatCard(title: String, value: String, gradient: Brush, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(Modifier.background(gradient).fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

/* ============================================================
   🗃 Category Stats (Details Tab)
   ============================================================ */
@Composable
private fun CategoryStatsList(stats: StatsUiState, currentMode: QuizMode) {
    if (stats.quizzesPerCategory.isEmpty()) {
        Text(
            text = if (currentMode == QuizMode.BUILT_IN)
                "No built-in quiz stats yet."
            else "No user-created quiz stats yet.",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        stats.quizzesPerCategory.keys.forEach { category ->
            val correct = stats.correctPerCategory[category] ?: 0
            val wrong = stats.wrongPerCategory[category] ?: 0
            val total = correct + wrong
            val acc = if (total > 0) (correct * 100f / total).roundToInt() else 0
            CategoryStatCard(category, total, correct, wrong, acc)
        }
    }
}

@Composable
private fun CategoryStatCard(category: String, quizzes: Int, correct: Int, wrong: Int, accuracy: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(6.dp)
    ) {
        Column(
            Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Text(category, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Q: $quizzes"); Text("C: $correct"); Text("W: $wrong"); Text("Acc: $accuracy%")
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { accuracy / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = progressResultColor(accuracy.toFloat()),
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
        }
    }
}

/* ============================================================
   🎯 Action Buttons
   ============================================================ */
@Composable
private fun ActionsSection(onReset: () -> Unit, onGoHome: (() -> Unit)?, onGoToQuiz: (() -> Unit)?) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ElevatedButton(onClick = onReset, shape = CircleShape) { Text("Reset") }
            ElevatedButton(onClick = { onGoHome?.invoke() }, shape = CircleShape) { Text("Home") }
            ElevatedButton(onClick = { onGoToQuiz?.invoke() }, shape = CircleShape) { Text("Quiz") }
        }
    }
}

/* ============================================================
   🧭 Tabs
   ============================================================ */
@Composable
fun StatsTabs(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTab])
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50))
            )
        }
    ) {
        Tab(selected = selectedTab == 0, onClick = { onTabSelected(0) }, text = { Text("Chart") })
        Tab(selected = selectedTab == 1, onClick = { onTabSelected(1) }, text = { Text("Details") })
    }
}

/* ============================================================
   📊 Chart Tab
   ============================================================ */
@Composable
fun StatsBarChart(stats: StatsUiState, modifier: Modifier = Modifier) {
    if (stats.quizzesPerCategory.isEmpty()) {
        Box(modifier = modifier.height(200.dp), contentAlignment = Alignment.Center) {
            Text(
                "No data yet — play quizzes to see results!",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val bars = stats.quizzesPerCategory.keys.map { category ->
        val correct = stats.correctPerCategory[category] ?: 0
        val wrong = stats.wrongPerCategory[category] ?: 0
        val total = (correct + wrong).coerceAtLeast(1)
        val acc = correct.toFloat() / total * 100f
        BarChartData.Bar(label = category, value = acc, color = progressResultColor(acc))
    }

    val chartData = BarChartData(bars)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.fillMaxWidth().height(260.dp)) {
            BarChart(
                barChartData = chartData,
                modifier = Modifier.fillMaxSize(),
                animation = simpleChartAnimation(),
                barDrawer = SimpleBarDrawer(),
                xAxisDrawer = SimpleXAxisDrawer(axisLineThickness = 2.dp),
                yAxisDrawer = SimpleYAxisDrawer(
                    labelTextSize = 12.sp,
                    labelValueFormatter = { "${it.toInt()}%" }
                ),
                labelDrawer = SimpleLabelDrawer(labelTextSize = 11.sp)
            )
        }
    }
}

/* ============================================================
   🧩 User Stats Conversion Helper
   ============================================================ */
fun userCategoriesToStats(
    categories: List<UserCategoryEntity>,
    questionsMap: Map<Int, List<UserQuestionEntity>>
): StatsUiState {
    var totalQuizzes = 0
    var totalCorrectAnswers = 0
    var totalWrongAnswers = 0
    val quizzesPerCategory = mutableMapOf<String, Int>()
    val correctPerCategory = mutableMapOf<String, Int>()
    val wrongPerCategory = mutableMapOf<String, Int>()

    categories.forEach { category ->
        val questions = questionsMap[category.id] ?: emptyList()
        val quizzes = questions.size
        val correct = 0 // TODO: Replace with actual logic
        val wrong = quizzes - correct
        quizzesPerCategory[category.name] = quizzes
        correctPerCategory[category.name] = correct
        wrongPerCategory[category.name] = wrong
        totalQuizzes += quizzes
        totalCorrectAnswers += correct
        totalWrongAnswers += wrong
    }

    return StatsUiState(
        isLoading = false,
        totalQuizzes = totalQuizzes,
        totalCorrectAnswers = totalCorrectAnswers,
        totalWrongAnswers = totalWrongAnswers,
        quizzesPerCategory = quizzesPerCategory,
        correctPerCategory = correctPerCategory,
        wrongPerCategory = wrongPerCategory
    )
}
