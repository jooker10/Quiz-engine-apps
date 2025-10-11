package futur.apps.composeproject1.appScreens._Screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import futur.apps.composeproject1.quizsystem.viewmodels.StatsUiState
import me.bytebeats.views.charts.bar.BarChart
import me.bytebeats.views.charts.bar.BarChartData
import me.bytebeats.views.charts.bar.render.bar.SimpleBarDrawer
import me.bytebeats.views.charts.bar.render.label.SimpleLabelDrawer
import me.bytebeats.views.charts.bar.render.xaxis.SimpleXAxisDrawer
import me.bytebeats.views.charts.bar.render.yaxis.SimpleYAxisDrawer
import me.bytebeats.views.charts.simpleChartAnimation


@Composable
fun StatsBarChartWithLibrary(
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
