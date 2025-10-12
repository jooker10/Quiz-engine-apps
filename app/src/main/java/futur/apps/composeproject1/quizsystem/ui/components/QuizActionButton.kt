package futur.apps.composeproject1.quizsystem.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import futur.apps.composeproject1.R
import futur.apps.composeproject1.viewmodels.QuizUiState
import futur.apps.composeproject1.viewmodels.QuizEvent

/**
 * ================================================
 * QuizActionButton.kt
 *
 * A reusable button for Quiz screens that adapts its text and behavior
 * depending on the current state of the quiz.
 *
 * Features:
 *  - Shows "Confirm" if the user hasn't checked the answer
 *  - Shows "Next" if not the last question and answer is checked
 *  - Shows "Finish" on the last question
 *  - Disabled until the user selects an option (unless already checked)
 *
 * Buyer Notes:
 *  - Connect this button to your QuizViewModel events
 *  - Can be customized with different colors, shapes, or text
 *  - Supports preview in Android Studio
 * ================================================
 */
@Composable
fun QuizActionButton(
    uiState: QuizUiState,
    onEvent: (QuizEvent) -> Unit,
) {
    val buttonText = when {
        !uiState.isAnswerChecked -> stringResource(R.string.action_confirm)
        uiState.isLastQuestion -> stringResource(R.string.action_finish)
        else -> stringResource(R.string.action_next)
    }

    Button(
        onClick = { onEvent(QuizEvent.ConfirmOrNext) },
        enabled = (uiState.selectedOptionText != null || uiState.isAnswerChecked),
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(buttonText)
    }
}

@Preview(showBackground = true)
@Composable
fun QuizActionButtonPreview(){
    QuizActionButton(
        uiState = QuizUiState(),
        onEvent = {}
    )
}
