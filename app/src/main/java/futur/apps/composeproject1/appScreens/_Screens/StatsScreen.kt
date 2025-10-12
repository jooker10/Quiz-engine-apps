package futur.apps.composeproject1.appScreens._Screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import futur.apps.composeproject1.viewmodels.StatsUiState
import futur.apps.composeproject1.viewmodels.QuizViewModel
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
    onGoHome: (() -> Unit)? = null,
    onGoToQuiz: (() -> Unit)? = null
) {
    val stats by quizViewModel.statsUiState.collectAsState()

    val totalAnswers = stats.totalCorrectAnswers + stats.totalWrongAnswers
    val accuracy = if (totalAnswers > 0)
        (stats.totalCorrectAnswers.toFloat() / totalAnswers * 100).roundToInt()
    else 0

    var selectedTab by remember { mutableStateOf(0) }

    // ---------- LAYOUT ----------
    Scaffold(
        bottomBar = {
            ActionsSection(
                onReset = { quizViewModel.resetStats() },
                onGoHome = onGoHome,
                onGoToQuiz = onGoToQuiz
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
        /*    // ---------- TITLE ----------
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))*/

            // ---------- GENERAL STATS ----------
            GeneralStatsGrid(stats, accuracy)

            Spacer(modifier = Modifier.height(24.dp))

            // ---------- TABS ----------
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Chart") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Details") })
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith  fadeOut() },
                label = "Tab Switch Animation"
            ) { tab ->
                when (tab) {
                    0 -> StatsBarChart(stats)
                    1 -> CategoryStatsList(stats)
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // spacing before bottom bar
        }
    }
}

@Composable
private fun GeneralStatsGrid(stats: StatsUiState, accuracy: Int) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard("Total Quizzes", stats.totalQuizzes.toString(), MaterialTheme.colorScheme.primaryContainer, Modifier.weight(1f))
            StatCard("Correct", stats.totalCorrectAnswers.toString(), Color(0xFFB9FBC0), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard("Wrong", stats.totalWrongAnswers.toString(), Color(0xFFFFADAD), Modifier.weight(1f))
            StatCard("Accuracy", "$accuracy%", Color(0xFFA0C4FF), Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .height(90.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = MaterialTheme.typography.bodySmall, color = Color.Black.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
private fun CategoryStatsList(stats: StatsUiState) {
    if (stats.quizzesPerCategory.isEmpty()) {
        Text(
            "No data available.",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
    } else {
        Column {
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

@Composable
private fun CategoryStatCard(category: String, quizzes: Int, correct: Int, wrong: Int, accuracy: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(category, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Q: $quizzes")
                Text("C: $correct")
                Text("W: $wrong")
                Text("Acc: $accuracy%")
            }

            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = accuracy / 100f)
                        .height(10.dp)
                        .background(Color(0xFF4CAF50), RoundedCornerShape(6.dp))
                )
            }
        }
    }
}

@Composable
private fun ActionsSection(
    onReset: () -> Unit,
    onGoHome: (() -> Unit)?,
    onGoToQuiz: (() -> Unit)?
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = onReset) { Text("Reset") }
            OutlinedButton(onClick = { onGoHome?.invoke() }) { Text("Home") }
            OutlinedButton(onClick = { onGoToQuiz?.invoke() }) { Text("Quiz") }
        }
    }
}

@Composable
fun StatsBarChart(
    stats: StatsUiState,
    modifier: Modifier = Modifier
) {
    if (stats.quizzesPerCategory.isEmpty()) {
        // placeholder
        Box(modifier = modifier.height(200.dp), contentAlignment = Alignment.Center) {
            Text("No data yet — start playing quizzes!", color = Color.Gray)
        }
        return
    }


    // build bars (accuracy percentage per category)
    val bars = stats.quizzesPerCategory.keys.map { category ->
        val correct = stats.correctPerCategory[category] ?:0
        val wrong = stats.wrongPerCategory[category] ?: 0
        val total = (correct + wrong).coerceAtLeast(1)
        val acc = correct.toFloat() / total * 100f
        BarChartData.Bar(
            label = category,
            value = acc,
            color = when {
                acc >= 80f -> Color(0xFF4CAF50)
                acc >= 50f -> Color(0xFFFFC107)
                else -> Color(0xFFF44336)
            }
        )
    }

    val chartData = BarChartData(bars = bars)

    Box(modifier = modifier.fillMaxWidth().height(260.dp)) {
        BarChart(
            barChartData = chartData,
            modifier = Modifier.fillMaxSize(),
            animation = simpleChartAnimation(),
            barDrawer = SimpleBarDrawer(),
            xAxisDrawer = SimpleXAxisDrawer(),
            yAxisDrawer = SimpleYAxisDrawer(),
            labelDrawer = SimpleLabelDrawer()
        )
    }
}
