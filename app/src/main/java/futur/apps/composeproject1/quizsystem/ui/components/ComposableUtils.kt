package futur.apps.composeproject1.quizsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/* ============================================================
   🌈 QUIZ MODE SELECTOR (Top Chips)
   ============================================================ */
@Composable
fun QuizModeSelectorRow(
    selectedSource: Int,
    onSourceSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuizModeChip(
            text = "App Quizzes",
            selected = selectedSource == 0,
            onClick = { onSourceSelected(0) },
            modifier = Modifier.weight(1f)
        )
        QuizModeChip(
            text = "My Quizzes",
            selected = selectedSource == 1,
            onClick = { onSourceSelected(1) },
            modifier = Modifier.weight(1f)
        )
    }
}

/* Small toggle chip for switching between modes */
@Composable
fun QuizModeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor =
        if (selected) MaterialTheme.colorScheme.onSurface
        else MaterialTheme.colorScheme.surface
    val textColor =
        if (selected) MaterialTheme.colorScheme.surface
        else MaterialTheme.colorScheme.onSurface

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(100))
            .clickable(
                interactionSource = interactionSource,
                indication = null, // optional, remove ripple if unwanted
                onClick = onClick
            )
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
