package futur.apps.composeproject1.appScreens._Screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import futur.apps.composeproject1.quiz.ui.components.QuizModeSelectorRow
import futur.apps.composeproject1.quiz.ui.components.getGradientColor
import futur.apps.composeproject1.quiz.ui.theme.progressResultColor
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
import kotlin.math.max
import kotlin.math.roundToInt

/* ============================================================
   🎛 Chart controls
   ============================================================ */
enum class ChartMetric { ACCURACY, CORRECT, WRONG }
enum class ChartSort { ALPHA, VALUE_DESC }
/* ============================================================
   📊 Stats Screen (Refined Design)
   ============================================================ */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StatsScreen(
    quizViewModel: QuizViewModel = hiltViewModel(),
    onGoHome: (() -> Unit)? = null
) {
    val stats by quizViewModel.stats.collectAsState()
    val currentMode by quizViewModel.mode.collectAsState()
    var selectedSource by remember { mutableIntStateOf(if (currentMode == QuizMode.DEFAULT) 0 else 1) }

    // Keep quiz mode synced with the selector
    LaunchedEffect(selectedSource) {
        quizViewModel.saveGlobalQuizMode(if (selectedSource == 0) QuizMode.DEFAULT else QuizMode.CUSTOM)
    }

    val totalAnswers = stats.totalCorrectAnswers + stats.totalWrongAnswers
    val accuracy = if (totalAnswers > 0)
        (stats.totalCorrectAnswers * 100f / totalAnswers).roundToInt()
    else 0

    // ✅ Use unified screen loader
    if (stats.isLoading) {
        ScreenLoadingIndicator()
        return
    }

    var showConfirm by remember { mutableStateOf(false) }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Confirm Reset") },
            text = {
                Text(
                    "Reset ${if (selectedSource == 0) "App Quizzes" else "My Quizzes"} stats? This cannot be undone.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    quizViewModel.resetStatsFor(
                        mode = if (selectedSource == 0) QuizMode.DEFAULT else QuizMode.CUSTOM
                    )
                }) { Text("Clear Stats", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.background) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    QuizModeSelectorRow(
                        selectedSource = selectedSource,
                        onSourceSelected = { selectedSource = it }
                    )
                }
            }
        },
        bottomBar = {
            ActionsSection(
                onReset = { showConfirm = true },
                onGoHome = onGoHome
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (stats.totalQuizzes == 0) {
                item { EmptyStatsMessage(selectedSource) }
            } else {
                item { GeneralStatsGrid(stats, accuracy) }

                item {
                    var selectedTab by remember { mutableIntStateOf(0) }
                    StatsTabs(selectedTab) { selectedTab = it }
                    Spacer(Modifier.height(12.dp))
                    Crossfade(targetState = selectedTab, label = "tab") { tab ->
                        when (tab) {
                            0 -> {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp, bottom = 12.dp)
                                ) {
                                    var metric by remember { mutableStateOf(ChartMetric.ACCURACY) }
                                    var sort by remember { mutableStateOf(ChartSort.VALUE_DESC) }

                                    ChartControls(
                                        metric = metric,
                                        onMetricChange = { metric = it },
                                        sort = sort,
                                        onSortChange = { sort = it }
                                    )

                                    StatsBarChart(
                                        stats = stats,
                                        metric = metric,
                                        sort = sort,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(
                                                min = 240.dp,
                                                max = (140 + stats.quizzesPerCategory.size * 28)
                                                    .coerceAtMost(480)
                                                    .dp
                                            )
                                    )
                                }
                            }

                            1 -> CategoryStatsList(
                                stats = stats,
                                currentMode = if (selectedSource == 0)
                                    QuizMode.DEFAULT else QuizMode.CUSTOM,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}



/* ============================================================
   🧭 Bottom buttons — simplified
   ============================================================ */
@Composable
private fun ActionsSection(
    onReset: () -> Unit,
    onGoHome: (() -> Unit)?
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ElevatedButton(
            onClick = onReset,
            shape = CircleShape,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) { Text("Clear Stats", color = Color.White) }

        ElevatedButton(
            onClick = { onGoHome?.invoke() },
            shape = CircleShape,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) { Text("Home", color = Color.White) }
    }
}

/* ============================================================
   🧮 Updated Stat Cards Titles
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
            StatCard("Total Quizzes", stats.totalQuizzes.toString(),
                getGradientColor(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)),
                Modifier.weight(1f)
            )
            StatCard("Correct Answers", stats.totalCorrectAnswers.toString(),
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
            StatCard("Wrong Answers", stats.totalWrongAnswers.toString(),
                getGradientColor(Color(0xFFD32F2F), Color(0xFFE57373)),
                Modifier.weight(1f)
            )
            StatCard("Accuracy Rate", "$accuracy%",
                getGradientColor(Color(0xFF1976D2), Color(0xFF64B5F6)),
                Modifier.weight(1f)
            )
        }
    }
}


/* ============================================================
   📴 Empty State
   ============================================================ */
@Composable
fun EmptyStatsMessage(source: Int) {
    val text = if (source == 0)
        "No built-in quiz data yet.\nPlay quizzes to see your statistics!"
    else
        "No user-created quiz data yet.\nAdd questions and start your custom quizzes!"

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

/* ============================================================
   🧮 Stat cards (icon + gradient)
   ============================================================ */


@Composable
private fun StatCard(
    title: String,
    value: String,
    gradient: Brush,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        letterSpacing = 1.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


/* ============================================================
   📋 Category list
   ============================================================ */
@Composable
private fun CategoryStatsList(
    stats: StatsUiState,
    currentMode: QuizMode,
    modifier: Modifier = Modifier
) {
    if (stats.quizzesPerCategory.isEmpty()) {
        Text(
            text = if (currentMode == QuizMode.DEFAULT) "No built-in category data yet."
            else "No user-created category data yet.",
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.padding(horizontal = 4.dp)
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
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.07f)
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
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
            )
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
   🎛 Segmented controls
   ============================================================ */
@Composable
private fun ChartControls(
    metric: ChartMetric,
    onMetricChange: (ChartMetric) -> Unit,
    sort: ChartSort,
    onSortChange: (ChartSort) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SmallChoiceChip(
            label = "Accuracy",
            selected = metric == ChartMetric.ACCURACY,
            onClick = { onMetricChange(ChartMetric.ACCURACY) },
            color = MaterialTheme.colorScheme.primary
        )
        SmallChoiceChip(
            label = "Correct",
            selected = metric == ChartMetric.CORRECT,
            onClick = { onMetricChange(ChartMetric.CORRECT) },
            color = Color(0xFF4CAF50)
        )
        SmallChoiceChip(
            label = "Wrong",
            selected = metric == ChartMetric.WRONG,
            onClick = { onMetricChange(ChartMetric.WRONG) },
            color = Color(0xFFD32F2F)
        )

        Spacer(Modifier.weight(1f))

        SmallChoiceChip(
            label = "A–Z",
            selected = sort == ChartSort.ALPHA,
            onClick = { onSortChange(ChartSort.ALPHA) },
            color = MaterialTheme.colorScheme.tertiary
        )
        SmallChoiceChip(
            label = "Value",
            selected = sort == ChartSort.VALUE_DESC,
            onClick = { onSortChange(ChartSort.VALUE_DESC) },
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun SmallChoiceChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color
) {
    val bg = if (selected) color else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = fg, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

/* ============================================================
   📈 Pro Bar Chart
   ============================================================ */
@Composable
fun StatsBarChart(
    stats: StatsUiState,
    metric: ChartMetric,
    sort: ChartSort,
    modifier: Modifier = Modifier
) {
    if (stats.quizzesPerCategory.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No data yet — play quizzes to see results!", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    // Build data
    val items = stats.quizzesPerCategory.map { (category, _) ->
        val correct = stats.correctPerCategory[category] ?: 0
        val wrong = stats.wrongPerCategory[category] ?: 0
        val total = max(1, correct + wrong)
        val value = when (metric) {
            ChartMetric.ACCURACY -> (correct.toFloat() / total) * 100f
            ChartMetric.CORRECT  -> correct.toFloat()
            ChartMetric.WRONG    -> wrong.toFloat()
        }
        Triple(category, value, correct to wrong)
    }

    val sorted = when (sort) {
        ChartSort.ALPHA -> items.sortedBy { it.first.lowercase() }
        ChartSort.VALUE_DESC -> items.sortedByDescending { it.second }
    }

    val color = when (metric) {
        ChartMetric.ACCURACY -> MaterialTheme.colorScheme.primary
        ChartMetric.CORRECT  -> Color(0xFF4CAF50)
        ChartMetric.WRONG    -> Color(0xFFD32F2F)
    }

    val bars = sorted.map { (label, value, _) ->
        BarChartData.Bar(
            label = label,
            value = value,
            color = if (metric == ChartMetric.ACCURACY) progressResultColor(value) else color
        )
    }

    val maxY = when (metric) {
        ChartMetric.ACCURACY -> 100f
        ChartMetric.CORRECT  -> (stats.correctPerCategory.values.maxOrNull() ?: 1).toFloat()
        ChartMetric.WRONG    -> (stats.wrongPerCategory.values.maxOrNull() ?: 1).toFloat()
    }

    val chartData = BarChartData(
        bars = bars,
        maxBarValue = maxY
    )

    Column(modifier = modifier) {
        // Header
        Row(
            Modifier.fillMaxWidth().padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                when (metric) {
                    ChartMetric.ACCURACY -> "Accuracy by category"
                    ChartMetric.CORRECT  -> "Correct answers by category"
                    ChartMetric.WRONG    -> "Wrong answers by category"
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                if (metric == ChartMetric.ACCURACY) "0–100%" else "Count",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Chart
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 240.dp, max = 480.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            BarChart(
                barChartData = chartData,
                modifier = Modifier.fillMaxSize(),
                animation = simpleChartAnimation(),
                barDrawer = SimpleBarDrawer(),
                xAxisDrawer = SimpleXAxisDrawer(
                    axisLineColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    axisLineThickness = 2.dp),
                yAxisDrawer = SimpleYAxisDrawer(
                    labelTextSize = 12.sp,
                    labelTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    axisLineColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    axisLineThickness = 2.dp,
                    labelValueFormatter = { v ->
                        if (metric == ChartMetric.ACCURACY) "${v.toInt()}%" else v.toInt().toString()
                    }
                ),
                labelDrawer = SimpleLabelDrawer(
                    labelTextSize = 11.sp,
                    labelTextColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}


