package futur.apps.composeproject1.appScreens._Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

data class PlayerScore(
    val uid: String = "",
    val name: String = "Unknown",
    val points: Int = 0
)

@Composable
fun LeaderboardScreen() {
    val db = Firebase.firestore
    val auth = Firebase.auth

    var topPlayers by remember { mutableStateOf<List<PlayerScore>>(emptyList()) }
    var currentUser by remember { mutableStateOf<PlayerScore?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // ✅ Real-time listener registration
    var listener: ListenerRegistration? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        try {
            // 👂 Listen in real-time for top 10 players
            listener = db.collection("users")
                .orderBy("totalPoints", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        errorMessage = error.localizedMessage
                        return@addSnapshotListener
                    }

                    val fetchedPlayers = snapshot?.documents?.mapIndexed { index, doc ->
                        PlayerScore(
                            uid = doc.id,
                            name = doc.getString("name") ?: "Player ${index + 1}",
                            points = (doc.getLong("totalPoints") ?: 0L).toInt()
                        )
                    } ?: emptyList()

                    topPlayers = fetchedPlayers
                    isLoading = false
                }

            // 👤 Load current user's data once
            auth.currentUser?.uid?.let { uid ->
                val doc = db.collection("users").document(uid).get().await()
                if (doc.exists()) {
                    currentUser = PlayerScore(
                        uid = uid,
                        name = doc.getString("name") ?: "You",
                        points = (doc.getLong("totalPoints") ?: 0L).toInt()
                    )
                }
            }
        } catch (e: Exception) {
            errorMessage = e.localizedMessage
            isLoading = false
        }
    }

    DisposableEffect(Unit) {
        onDispose { listener?.remove() } // ✅ Stop listening when leaving screen
    }

    when {
        isLoading -> LoadingLeaderboard()
        errorMessage != null -> ErrorLeaderboard(errorMessage!!)
        else -> LeaderboardContent(topPlayers, currentUser)
    }
}

/* -----------------------------------------------
   💫 Loading & Error
----------------------------------------------- */
@Composable
fun LoadingLeaderboard() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ErrorLeaderboard(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $message",
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

/* -----------------------------------------------
   🏆 Main Content
----------------------------------------------- */
@Composable
fun LeaderboardContent(players: List<PlayerScore>, currentUser: PlayerScore?) {
    val userInTop = players.any { it.uid == currentUser?.uid }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(16.dp)
    ) {
        Text(
            text = "🏆 Top 10 Players",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            textAlign = TextAlign.Center
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(players) { index, player ->
                LeaderboardCard(player, rank = index + 1)
            }

            if (!userInTop && currentUser != null) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Your Stats",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LeaderboardCard(currentUser, rank = null, highlight = true)
                }
            }
        }
    }
}

/* -----------------------------------------------
   🥇 Player Card
----------------------------------------------- */
@Composable
fun LeaderboardCard(
    player: PlayerScore,
    rank: Int?,
    highlight: Boolean = false
) {
    val gradientColors = when (rank) {
        1 -> listOf(Color(0xFFFFD700), Color(0xFFFFE97F)) // Gold
        2 -> listOf(Color(0xFFC0C0C0), Color(0xFFE0E0E0)) // Silver
        3 -> listOf(Color(0xFFCD7F32), Color(0xFFE6A96B)) // Bronze
        else -> if (highlight)
            listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.tertiary)
        else
            listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surfaceVariant)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.horizontalGradient(gradientColors))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (rank != null) {
                Text(
                    text = "#$rank",
                    color = Color.Black,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
            Text(
                text = player.name,
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.Black
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${player.points}",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
