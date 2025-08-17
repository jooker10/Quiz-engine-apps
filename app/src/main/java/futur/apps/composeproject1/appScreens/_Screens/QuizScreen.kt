package futur.apps.composeproject1.appScreens._Screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import futur.apps.composeproject1._Mains.EffectsViewModel
import futur.apps.composeproject1._Mains.QuizViewModel
import futur.apps.composeproject1.utils.CategoryName

@Composable
fun QuizScreen(
    category : CategoryName?,
    quizViewModel: QuizViewModel = hiltViewModel(),
    effectsViewModel: EffectsViewModel = hiltViewModel(),
) {
    val questions = quizViewModel.questions.collectAsState()
    Log.d("see", "quiz questions size: ${questions.value.size}")
    val index by quizViewModel.currentIndex
    val score by quizViewModel.score
    val selectedOption by quizViewModel.selectedOption
    val timeLeft by quizViewModel.timeLeft
    val selectedOptionText by quizViewModel.selectedOptionText


    LaunchedEffect(Unit) {
        quizViewModel.events.collect { event ->
            when(event) {
                QuizViewModel.EffectsEvent.CorrectAnswer -> {
                    effectsViewModel.playCorrectSound()
                    effectsViewModel.speak("CorrectAnswer Answer!")
                }
                QuizViewModel.EffectsEvent.WrongAnswer -> {
                    effectsViewModel.playWrongSound()
                    effectsViewModel.speak("WrongAnswer Answer!")
                }

            }
        }
    }

if(questions.value.isEmpty()) {
    Log.d("see", "questions is empty")}
    if (questions.value.isNotEmpty()) {
        val question = questions.value[index]
        val options = question.options

        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Score: $score", style = MaterialTheme.typography.bodyMedium)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(40.dp)
                ){
                    CircularProgressIndicator(
                    progress =  { 1f - (timeLeft.toFloat()/15f) },
                    modifier = Modifier.fillMaxSize(),
                    color = if(timeLeft<5) Color.Red else MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp,
                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap
                    )
                    Text(
                        text = "${timeLeft}s",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (timeLeft <= 5) Color.Red else Color.Unspecified,
                        textAlign = TextAlign.Center
                    )
                }
                Text(
                    "${index + 1} / ${questions.value.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = question.questionText,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            options.forEachIndexed { index, option ->
                QuizOption(
                    text = option,
                    isSelected = selectedOptionText == option,
                    onClick = {
                        quizViewModel.selectOptionText(option)
                        quizViewModel.selectOption(index)
                    }
                )
            }


            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    quizViewModel.confirmOrNext()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = (selectedOption != null || quizViewModel.isAnswerChecked.value)
            ) {
                Text(
                    text =
                        when {
                            !quizViewModel.isAnswerChecked.value -> "Confirm"
                            index == questions.value.size - 1 -> "Finish"
                            else -> "Next"
                        }
                )

            }
        }
    } else {
        Text(
            text = "Loading questions....",
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun QuizOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            2.dp,
            if (isSelected) Color(0xFF4CAF50) else Color.Gray
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .border(
                        BorderStroke(2.dp, if (isSelected) Color(0xFF4CAF50) else Color.Gray),
                        shape = CircleShape
                    )
                    .background(
                        if (isSelected) Color(0xFF4CAF50) else Color.Transparent,
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

