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
import futur.apps.composeproject1.RoomDatabase.DbViewModel
 import futur.apps.composeproject1.utils.CategoryName
 import futur.apps.composeproject1.viewmodels.HomeViewModel


/*
// -------------------- HomeScreen --------------------
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    dbViewModel: DbViewModel = hiltViewModel(),
    onQuizClick: () -> Unit = {},
    onTableClick: () -> Unit = {}
) {
    val uiState by homeViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Profile Section
        ProfileSection(
            username = uiState.username,
            level = uiState.level,
            points = uiState.points
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Scores Section
        Text(text = "Your Scores", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // داتا تجريبية مباشرة
            val categories = listOf(
                Triple("Verbs", 20, Color(0xFF81D4FA)),
                Triple("Adjectives", 15, Color(0xFFFFCC80)),
                Triple("Nouns", 10, Color(0xFFA5D6A7)),
                Triple("Phrases", 25, Color(0xFFCE93D8)),
                Triple("Idioms", 10, Color(0xFFFF8A65)),
                Triple("Expressions", 20, Color(0xFF90CAF9)),
                Triple("Sentences", 20, Color(0xFFFFF176))
            )

            items(categories) { (name, score, color) ->
                ScoreCard(
                    categoryName = name,
                    score = score,
                    maxScore = 100,
                    color = color
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
fun ProfileSection(username: String, level: Int, points: Int) {
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
            Text(text = "$points", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// -------------------- Score Card with Progress --------------------
@Composable
fun ScoreCard(categoryName: String, score: Int, maxScore: Int, color: Color) {
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
                Text(text = "$score/$maxScore", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = score / maxScore.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = color,
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
                        .background(Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = word, fontSize = 14.sp)
                }
            }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Profile Section
        ProfileSection(
            username = uiState.username,
            level = uiState.level,
            points = uiState.points
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Scores Section
        Text(text = "Your Scores", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(CategoryName.entries) { category ->
                val score = uiState.scores[category] ?: 0
                ScoreCard(
                    categoryName = category.displayName,
                    score = score,
                    maxScore = 100, // مؤقتًا
                    color = category.color
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
fun ProfileSection(username: String, level: Int, points: Int) {
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
            Text(text = "$points", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// -------------------- Score Card with Progress --------------------
@Composable
fun ScoreCard(categoryName: String, score: Int, maxScore: Int, color: Color) {
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
                Text(text = "$score/$maxScore", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = (score.toFloat() / maxScore).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = color,
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
