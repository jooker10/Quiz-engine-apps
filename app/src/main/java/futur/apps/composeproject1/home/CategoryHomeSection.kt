package futur.apps.composeproject1.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryScoreSection() {
    // Implementation of the main content
    Column {
        Text(
            text = "Quiz Categories",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray.copy(0.7f),
            modifier = Modifier
                .padding(8.dp)
        )
        Box(
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, top = 4.dp)
                .fillMaxWidth(),
        ) {
            LazyColumn {
                items(7) { index ->
                    CardScoreHome(index = index)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}