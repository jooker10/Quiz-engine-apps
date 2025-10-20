package futur.apps.composeproject1.appScreens._Screens


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StatsScreen(
    quizViewModel: QuizViewModel = hiltViewModel(),
    userQuizViewModel: UserQuizViewModel = hiltViewModel(),
    onGoHome: (() -> Unit)? = null,
    onGoToQuiz: (() -> Unit)? = null
) {
    val builtInStats by quizViewModel.statsUiState.collectAsState()
    val userCategories by userQuizViewModel.categories.collectAsState(initial = emptyList())
    val userQuestionsMap by remember { mutableStateOf(mutableMapOf<Int, List<UserQuestionEntity>>()) }

    // 🟢 Convert User data to StatsUiState
    val userStats by remember(userCategories, userQuestionsMap) {
        derivedStateOf {
            userCategoriesToStats(userCategories, userQuestionsMap)
        }
    }

    var selectedDataSource by remember { mutableStateOf(0) } // 0 = Built-in, 1 = User
    var selectedTab by remember { mutableIntStateOf(0) }

    val stats = if (selectedDataSource == 0) builtInStats else userStats
    val totalAnswers = stats.totalCorrectAnswers + stats.totalWrongAnswers
    val accuracy = if (totalAnswers > 0) (stats.totalCorrectAnswers.toFloat() / totalAnswers * 100).roundToInt() else 0

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
        // ---------- DATA SOURCE CHIPS ----------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            FilterChip(
                selected = selectedDataSource == 0,
                onClick = { selectedDataSource = 0 },
                label = { Text("Built-in") }
            )
            Spacer(modifier = Modifier.width(12.dp))
            FilterChip(
                selected = selectedDataSource == 1,
                onClick = { selectedDataSource = 1 },
                label = { Text("User") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ---------- GENERAL STATS ----------
        GeneralStatsGrid(stats, accuracy)

        Spacer(modifier = Modifier.height(24.dp))

        // ---------- TABS ----------
        StatsTabs(selectedTab) { selectedTab = it }

        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "Tab Switch Animation"
        ) { tab ->
            when (tab) {
                0 -> StatsBarChart(stats)
                1 -> CategoryStatsList(
                    stats = stats,
                    currentMode = if (selectedDataSource == 0) QuizMode.BUILT_IN else QuizMode.USER_CREATED
                )

            }
        }

        // ---------- ACTION BUTTONS ----------
        ActionsSection(
            onReset = {
                if (selectedDataSource == 0) quizViewModel.resetStats()
                else {
                    // Reset User stats logic if exists
                }
            },
            onGoHome = onGoHome,
            onGoToQuiz = onGoToQuiz
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}



@Composable
fun ModeHeader(currentMode: QuizMode, onModeChange: (QuizMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ModeChip(
            text = "Default categories",
            selected = currentMode == QuizMode.BUILT_IN,
            onClick = { onModeChange(QuizMode.BUILT_IN) }
        )
        Spacer(modifier = Modifier.width(12.dp))
        ModeChip(
            text = "My Categories",
            selected = currentMode == QuizMode.USER_CREATED,
            onClick = { onModeChange(QuizMode.USER_CREATED) }
        )
    }
}

@Composable
fun ModeChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() },
        color = if (selected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = if (selected) 4.dp else 0.dp,
        shadowElevation = if (selected) 3.dp else 0.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = if (selected)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 15.sp
        )
    }
}


@Composable
fun QuizModeButton(text: String, selected: Boolean, onClick: () -> Unit) {
    val bgColor = if (selected)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    else Color.Transparent

    val textColor = if (selected)
        MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ---------------- GENERAL STATS ----------------
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
                title = "Total Quizzes",
                value = stats.totalQuizzes.toString(),
                gradient = getGradientColor(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                ),
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Correct",
                value = stats.totalCorrectAnswers.toString(),
                gradient = getGradientColor(Color(0xFF4CAF50), Color(0xFF66BB6A)),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StatCard(
                title = "Wrong",
                value = stats.totalWrongAnswers.toString(),
                gradient = getGradientColor(Color(0xFFD32F2F), Color(0xFFE57373)),
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Accuracy",
                value = "$accuracy%",
                gradient = getGradientColor(Color(0xFF1976D2), Color(0xFF64B5F6)),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    gradient: Brush,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = value,
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


// ---------------- DETAILS SECTION ----------------
@Composable
private fun CategoryStatsList(
    stats: StatsUiState,
    currentMode: QuizMode,
    userQuizViewModel: UserQuizViewModel = hiltViewModel(),
    onGoToAddCategory: (() -> Unit)? = null
) {
    if (currentMode == QuizMode.USER_CREATED) {
        val userCategories by userQuizViewModel.categories.collectAsState(initial = emptyList())

        if (userCategories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🚧 No category yet.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    ElevatedButton(
                        onClick = { onGoToAddCategory?.invoke() },
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Add Category")
                    }
                }
            }
            return
        }

        // Show User Categories Stats
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            userCategories.forEach { category ->
                val questions = userQuizViewModel.getQuestions(category.id)
                    .collectAsState(initial = emptyList()).value
                val total = questions.size
                val correct = stats.correctPerCategory[category.name] ?: 0
                val wrong = stats.wrongPerCategory[category.name] ?: 0
                val acc = if (total > 0) (correct.toFloat() / total * 100).roundToInt() else 0

                CategoryStatCard(category.name, total, correct, wrong, acc)
            }
        }

    } else {
        // Default Built-in categories
        if (stats.quizzesPerCategory.isEmpty()) {
            Text(
                text = "No data available yet.",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                stats.quizzesPerCategory.keys.forEach { category ->
                    val quizzes = stats.quizzesPerCategory[category] ?: 0
                    val correct = stats.correctPerCategory[category] ?: 0
                    val wrong = stats.wrongPerCategory[category] ?: 0
                    val total = correct + wrong
                    val acc = if (total > 0) (correct.toFloat() / total * 100).roundToInt() else 0
                    CategoryStatCard(category, quizzes, correct, wrong, acc)
                }
            }
        }
    }
}

/*@Composable
private fun CategoryStatsList(stats: StatsUiState) {
    if (stats.quizzesPerCategory.isEmpty()) {
        Text(
            text = "No data available yet.",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            stats.quizzesPerCategory.keys.forEach { category ->
                val quizzes = stats.quizzesPerCategory[category] ?: 0
                val correct = stats.correctPerCategory[category] ?: 0
                val wrong = stats.wrongPerCategory[category] ?: 0
                val total = correct + wrong
                val acc = if (total > 0) (correct.toFloat() / total * 100).roundToInt() else 0
                CategoryStatCard(category, quizzes, correct, wrong, acc)
            }
        }
    }
}*/

@Composable
private fun CategoryStatCard(
    category: String,
    quizzes: Int,
    correct: Int,
    wrong: Int,
    accuracy: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
            Text(
                text = category,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Q: $quizzes", style = MaterialTheme.typography.labelMedium)
                Text("C: $correct", style = MaterialTheme.typography.labelMedium)
                Text("W: $wrong", style = MaterialTheme.typography.labelMedium)
                Text("Acc: $accuracy%", style = MaterialTheme.typography.labelMedium)
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
            progress = { accuracy / 100f },
            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50)),
            color = progressResultColor(accuracy.toFloat()),
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        }
    }
}



// ---------------- ACTION BUTTONS ----------------

@Composable
private fun ActionsSection(
    onReset: () -> Unit,
    onGoHome: (() -> Unit)?,
    onGoToQuiz: (() -> Unit)?
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ElevatedButton(onClick = onReset, shape = CircleShape) {
                Text("Reset", style = MaterialTheme.typography.labelLarge)
            }
            ElevatedButton(onClick = { onGoHome?.invoke() }, shape = CircleShape) {
                Text("Home", style = MaterialTheme.typography.labelLarge)
            }
            ElevatedButton(onClick = { onGoToQuiz?.invoke() }, shape = CircleShape) {
                Text("Quiz", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

//-------------- Tabs --------------------
@Composable
fun StatsTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedTab])
                    .height(3.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(50)
                    ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        Tab(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            text = { Text("Chart") }
        )
        Tab(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            text = { Text("Details") }
        )
    }
}



// ---------------- CHART ----------------
@Composable
fun StatsBarChart(
    stats: StatsUiState,
    modifier: Modifier = Modifier
) {
    if (stats.quizzesPerCategory.isEmpty()) {
        Box(
            modifier = modifier.height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No data yet — play quizzes to see results!",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    // ---------- Build Bar Data ----------
    val bars = stats.quizzesPerCategory.keys.map { category ->
        val correct = stats.correctPerCategory[category] ?: 0
        val wrong = stats.wrongPerCategory[category] ?: 0
        val total = (correct + wrong).coerceAtLeast(1)
        val acc = correct.toFloat() / total * 100f
        BarChartData.Bar(
            label = category,
            value = acc,
            color = progressResultColor(acc)
        )
    }

    val chartData = BarChartData(bars = bars)

    // ---------- Draw Chart ----------
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            BarChart(
                barChartData = chartData,
                modifier = Modifier.fillMaxSize(),
                animation = simpleChartAnimation(),
                barDrawer = SimpleBarDrawer(),
                xAxisDrawer = SimpleXAxisDrawer(
                    axisLineThickness = 2.dp,
                    axisLineColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                yAxisDrawer = SimpleYAxisDrawer(
                    labelTextSize = 12.sp,
                    labelTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    labelValueFormatter = { "${it.toInt()}%" } // ✅ show %
                ),
                labelDrawer = SimpleLabelDrawer(
                    drawLocation = SimpleLabelDrawer.DrawLocation.Outside,
                    labelTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    labelTextSize = 11.sp
                )
            )
        }

        // ---------- Legend ----------
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendDot(progressResultColor(10f), "Low")
            LegendDot(progressResultColor(60f), "Average")
            LegendDot(progressResultColor(80f), "Good")
            LegendDot(progressResultColor(100f), "Perfect")
        }
    }
}

// ---------- Legend Helper ----------
@Composable
private fun LegendDot(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}



@Preview(showBackground = true)
@Composable
fun StatsScreenPreview() {
    GeneralStatsGrid(
        stats = StatsUiState(
            totalQuizzes = 10,
            totalCorrectAnswers = 8,
            totalWrongAnswers = 2,
            quizzesPerCategory = mapOf("BuildInCategory 1" to 5, "BuildInCategory 2" to 3),
            correctPerCategory = mapOf("BuildInCategory 1" to 4, "BuildInCategory 2" to 2),
            wrongPerCategory = mapOf("BuildInCategory 1" to 1, "BuildInCategory 2" to 1)
        ),
        accuracy = 75
    )
}

@Preview
@Composable
fun ActionButtonsPreview() {
    ActionsSection(
        onReset = {},
        onGoHome = {},
        onGoToQuiz = {}
    )
}

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
        val correct = questions.count { /* replace with actual user answer correctness */ true } // Placeholder
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




