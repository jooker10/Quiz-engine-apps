package futur.apps.composeproject1.appScreens._Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import futur.apps.composeproject1.QuizFiles.Question
import futur.apps.composeproject1.viewmodels.QuizUiState

@Composable
fun HeaderQuestionSection(uiState : QuizUiState , currentQuestion: Question) {

    Box(
        modifier = Modifier
            .offset(y = 80.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .padding(start = 32.dp, end = 32.dp)
                .shadow(16.dp, shape = RoundedCornerShape(25.dp)),

            ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.White)
                    .fillMaxWidth()
                    .height(160.dp)
                    .clickable {}
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${uiState.correctScore}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Green,
                            modifier = Modifier
                                .padding(8.dp)
                        )
                        LinearProgressIndicator(
                            progress = { (uiState.correctScore.toFloat())/(uiState.questions.size) },
                            modifier = Modifier
                                .width(40.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(100.dp)),
                            //  .clip(RoundedCornerShape(16.dp)),
                            color = Color.Green,
                            trackColor = Color.Green.copy(0.3f),
                            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                            gapSize = 0.dp,
                            drawStopIndicator = {
                                // Custom drawing logic for the stop indicator
                                drawPoints(
                                    listOf(center.copy(x = center.x + size.width * 0.3f)),
                                    pointMode = androidx.compose.ui.graphics.PointMode.Points,
                                    color = Color.Green,
                                    strokeWidth = 0f,
                                    cap = StrokeCap.Round
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${uiState.wrongScore}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Red,
                            modifier = Modifier
                                .padding(8.dp)
                        )
                        LinearProgressIndicator(
                            progress = { 0.3f },
                            modifier = Modifier
                                .width(40.dp)
                                .height(8.dp),
                            color = Color.Red,
                            trackColor = Color.Red.copy(0.3f),
                            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                            gapSize = 0.dp,
                            drawStopIndicator = {
                                // Custom drawing logic for the stop indicator

                            }
                        )
                    }
                }
                Text(
                    text = "Question : ${uiState.currentIndex + 1}/${uiState.questions.size}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(8.dp)
                )
                Text(
                    text = currentQuestion.questionText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(8.dp)
                )


            }
        }

        // Circular progress for Timer
        Surface(
            modifier = Modifier
                .background(Color.White)
                .offset(y = (-25).dp),
            shape = CircleShape
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    progress = { 1f - (uiState.timeLeft.toFloat() / uiState.maxTime.toFloat()) },
                    modifier = Modifier.size(50.dp),
                    color = if (uiState.timeLeft <= 5) Color.Red else MaterialTheme.colorScheme.primary,
                    strokeWidth = 5.dp,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                )
                Text(
                    text = "${uiState.timeLeft}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.timeLeft <= 5) Color.Red else MaterialTheme.colorScheme.primary,
                )
            }
        }

    }
}