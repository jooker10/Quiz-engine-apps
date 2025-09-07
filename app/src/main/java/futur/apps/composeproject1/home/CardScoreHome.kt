package futur.apps.composeproject1.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import futur.apps.composeproject1.utils.Category

@Composable
fun CardScoreHome(index: Int, score: Float = 0.7f) {
    val isActive = index == 0

    // Card colors
    val cardBackground = if (isActive) Color.White else Color(0xFFF5F5F5) // white or soft gray
    val cardElevation = if (isActive) 8.dp else 2.dp
    val titleColor = if (isActive) Color(0xFF37474F) else Color(0xFF90A4AE) // dark gray vs muted gray
    val subtitleColor = if (isActive) Color(0xFF607D8B) else Color(0xFFB0BEC5) // soft gray shades
    val iconColor = if (isActive) Color(0xFF546E7A) else Color(0xFFB0BEC5) // subtle dark gray or muted
    val progressGradientStart = if (isActive) Color(0xFF90CAF9) else Color(0xFFCFD8DC)
    val progressGradientEnd = if (isActive) Color(0xFF42A5F5) else Color(0xFFE0E0E0)
    val progressTrackColor = if (isActive) Color(0xFFE3F2FD) else Color(0xFFF0F0F0)

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(cardElevation),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular progress with gradient effect
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = score.coerceIn(0f, 1f),
                    modifier = Modifier.size(50.dp),
                    color = progressGradientEnd,
                    strokeWidth = 5.dp,
                    trackColor = progressTrackColor
                )
                Text(
                    text = "${(score * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = progressGradientStart
                )
            }

            // Text column
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = Category.entries[index].displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )
                Text(
                    text = "Almost there",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = subtitleColor
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Go",
                tint = iconColor
            )
        }
    }
}


@Preview
@Composable
fun CardScoreHomePreview() {
    Column {
        CardScoreHome(index = 0, score = 0.75f)
        CardScoreHome(index = 1, score = 0.5f)
        CardScoreHome(index = 2, score = 0.9f)
    }
}
