package futur.apps.composeproject1.appScreens._Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import futur.apps.composeproject1.R
import futur.apps.composeproject1.viewmodels.QuizViewModel
import futur.apps.composeproject1.utils.CategoryName

@Composable
fun HomeScreen(quizViewModel: QuizViewModel = hiltViewModel()) {
    val quizState by quizViewModel.quizUiState.collectAsState()
    val username by quizViewModel.username.collectAsState()

    // Example: default profile image
    val profileImage = remember { R.drawable.ic_launcher_background }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Profile section
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = profileImage),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = username,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your Scores",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // LazyColumn for categories scores
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quizState.scoresByCategory.forEach { (category, score) ->
                item {
                    ScoreItem(category = category, score = score)
                }
            }
        }
    }
}

@Composable
fun ScoreItem(category: CategoryName, score: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = category.displayName, style = MaterialTheme.typography.bodyLarge)
            Text(text = score.toString(), style = MaterialTheme.typography.bodyLarge)
        }
    }
}