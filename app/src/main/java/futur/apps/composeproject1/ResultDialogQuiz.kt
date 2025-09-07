package futur.apps.composeproject1

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ScoreCell(
    val color : Color,
    val value : String,
    val label : String,
)

@Composable
fun ResultDialogQuiz(
    completion : Int,
    totalQuestions : Int,
    correctAnswers :Int,
    wrongAnswers : Int
) {
    val scoreList = listOf(
        ScoreCell(
            color = Color.Blue,
            value = "$completion%",
            label = "Completion"
        ),
        ScoreCell(
            color = Color.Blue,
            value = "$totalQuestions",
            label = "Total questions"
        ),
        ScoreCell(
            color = Color.Green,
                value = "$correctAnswers",
            label = "Correct"
        ),
        ScoreCell(
            color = Color.Red,
            value = "$wrongAnswers",
            label = "Wrong"
        )
    )
    Card (
        modifier = Modifier
            .padding(start = 32.dp, end = 32.dp)
            .offset(y = (100).dp)
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
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .height(130.dp)

            ) {
                items(scoreList.size) { index ->
                    Cell(scoreList[index] )
                }
            }

        }
    }
}

@Composable
fun Cell(cellItem: ScoreCell) {
   // val cellItem = scoreList[index]
    Row {
        Icon(
            Icons.Filled.PlayArrow,
            contentDescription = "icon",
            tint = cellItem.color
        )
        Column {
            Text(
                text = cellItem.value,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = cellItem.color
            )
            Text(
                text = cellItem.label,
                color = Color.DarkGray)
        }
    }
}
