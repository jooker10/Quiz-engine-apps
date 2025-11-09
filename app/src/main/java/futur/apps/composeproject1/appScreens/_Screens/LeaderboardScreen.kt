package futur.apps.composeproject1.appScreens._Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import futur.apps.composeproject1.R
import kotlinx.coroutines.tasks.await

data class PlayerScore(
    val uid: String = "",
    val name: String = "Unknown",
    val points: Int = 0,
    val photoUrl: String? = null
)

@Composable
fun LeaderboardScreen() {
    val db = Firebase.firestore
    val auth = Firebase.auth

    var topPlayers by remember { mutableStateOf<List<PlayerScore>>(emptyList()) }
    var currentUser by remember { mutableStateOf<PlayerScore?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var listener: ListenerRegistration? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        try {
            listener = db.collection("users")
                .orderBy("totalPoints", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        errorMessage = error.localizedMessage
                        return@addSnapshotListener
                    }
                    val fetched = snapshot?.documents?.mapIndexed { index, doc ->
                        PlayerScore(
                            uid = doc.id,
                            name = doc.getString("name") ?: "Player ${index + 1}",
                            points = (doc.getLong("totalPoints") ?: 0L).toInt(),
                            photoUrl = doc.getString("photoUrl")
                        )
                    } ?: emptyList()
                    topPlayers = fetched
                    isLoading = false
                }

            auth.currentUser?.uid?.let { uid ->
                val doc = db.collection("users").document(uid).get().await()
                if (doc.exists()) {
                    currentUser = PlayerScore(
                        uid = uid,
                        name = doc.getString("name") ?: "You",
                        points = (doc.getLong("totalPoints") ?: 0L).toInt(),
                        photoUrl = doc.getString("photoUrl")
                    )
                }
            }
        } catch (e: Exception) {
            errorMessage = e.localizedMessage
            isLoading = false
        }
    }

    DisposableEffect(Unit) { onDispose { listener?.remove() } }

    when {
        isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        errorMessage != null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        }
        else -> LeaderboardContent(topPlayers, currentUser)
    }
}

/* -----------------------------------------------
   🏆 Content
----------------------------------------------- */
@Composable
fun LeaderboardContent(players: List<PlayerScore>, currentUser: PlayerScore?) {
    val userInTop = players.any { it.uid == currentUser?.uid }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "🏆 Top 10 Players",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            itemsIndexed(players) { index, player ->
                LeaderboardCard(
                    player = player,
                    rank = index + 1,
                    highlight = player.uid == currentUser?.uid
                )
            }

            if (!userInTop && currentUser != null) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "Your Position",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                    LeaderboardCard(player = currentUser, rank = null, highlight = true)
                }
            }
        }
    }
}

/* -----------------------------------------------
   🥇 Player Card — styled like CategoryListScreen
----------------------------------------------- */
@Composable
fun LeaderboardCard(
    player: PlayerScore,
    rank: Int?,
    highlight: Boolean = false
) {
    val rankColor = when (rank) {
        1 -> Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile photo or initial
            if (!player.photoUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = player.photoUrl,
                    contentDescription = "Profile",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(rankColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.name.firstOrNull()?.uppercase() ?: "?",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Points: ${player.points}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
            }

            if (rank != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = rankColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "#$rank",
                        color = rankColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
