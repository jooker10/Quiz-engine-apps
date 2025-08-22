package futur.apps.composeproject1.appScreens._Screens

 import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
 import androidx.compose.foundation.lazy.LazyColumn
 import androidx.compose.foundation.lazy.items
 import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
 import futur.apps.composeproject1.utils.CategoryName
 import futur.apps.composeproject1.viewmodels.HomeViewModel



/*

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onQuizClick: (CategoryName) -> Unit = {},
    onTableClick: () -> Unit = {}
) {
    val uiState by homeViewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ProfileSection(
            username = uiState.username,
            level = uiState.level,
            totalPoints = uiState.scores.values.sum()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Your Scores", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(CategoryName.entries) { category ->
                val score = uiState.scores[category] ?: 0
                val requiredPoints = category.requiredPoints
                ScoreCard(
                    categoryName = category.displayName,
                    score = score,
                    maxScore = requiredPoints,
                    color = category.color,
                    isLocked = score < requiredPoints
                )
            }
        }



        Spacer(modifier = Modifier.height(24.dp))

       // RecentWordsRow(words = uiState.recentWords)
    }
}

@Composable
fun ScoreCard(
    categoryName: String,
    score: Int,
    maxScore: Int,
    color: Color,
    isLocked: Boolean
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = categoryName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = if (isLocked) "$score/$maxScore (Locked)" else "$score/$maxScore",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = if(maxScore > 0) { (score.toFloat() / maxScore).coerceIn(0f, 1f)}
                else {0f},
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (isLocked) Color.Gray else color,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )
            }
        }
}

@Composable
fun ProfileSection(username: String, level: Int, totalPoints: Int) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(text = "Hello, $username!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Level: $level", fontSize = 16.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "Points", fontSize = 14.sp)
            Text(text = "$totalPoints", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
}

*/


// -------------------- HomeScreen --------------------
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    onQuizClick: () -> Unit = {},
    onTableClick: () -> Unit = {}
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val totalPoints = uiState.pointsList.values.sum()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Profile Section
        ProfileSection(
            username = uiState.username,
            level = uiState.level,
            totalPoints = totalPoints
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Scores Section
        Text(text = "Your Progress", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(CategoryName.entries) { category ->
                val score = uiState.pointsList[category] ?: 0
                val previousIndex = CategoryName.entries.indexOf(category) - 1
                val unlocked = if (previousIndex < 0) {
                    true // أول فئة دائماً مفتوحة
                } else {
                    val prevCategory = CategoryName.entries[previousIndex]
                    (uiState.pointsList[prevCategory] ?: 0) >= prevCategory.maxScore
                }

                ScoreCard(
                    categoryName = category.displayName,
                    score = if (unlocked) score else 0,
                    maxScore = category.maxScore,
                    color = category.color,
                    enabled = unlocked
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Actions
        QuickActionsRow(onQuizClick = onQuizClick, onTableClick = onTableClick)

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Words
        RecentWordsRow(words = uiState.recentWords)
    }
}

// -------------------- Profile --------------------
@Composable
fun ProfileSection(username: String, level: Int, totalPoints: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = "Hello, $username!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Level: $level", fontSize = 16.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "Points", fontSize = 14.sp)
            Text(text = "$totalPoints", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// -------------------- Score Card with Progress --------------------
@Composable
fun ScoreCard(categoryName: String, score: Int, maxScore: Int, color: Color, enabled: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) color.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = categoryName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) Color.Black else Color.DarkGray
                )
                Text(
                    text = "$score/$maxScore",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color.Black else Color.DarkGray
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = (score.toFloat() / maxScore).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (enabled) color else Color.DarkGray,
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )
        }
    }
}

// -------------------- Quick Actions --------------------
@Composable
fun QuickActionsRow(onQuizClick: () -> Unit, onTableClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(onClick = onQuizClick, modifier = Modifier.weight(1f)) {
            Text(text = "Take Quiz")
        }
        Button(onClick = onTableClick, modifier = Modifier.weight(1f)) {
            Text(text = "View Table")
        }
    }
}

// -------------------- Recent Words --------------------
@Composable
fun RecentWordsRow(words: List<String>) {
    Column {
        Text(text = "Recent Words", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            words.take(5).forEach { word ->
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFFF9C4), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = word, fontSize = 14.sp)
                }
            }
            }
        }
}