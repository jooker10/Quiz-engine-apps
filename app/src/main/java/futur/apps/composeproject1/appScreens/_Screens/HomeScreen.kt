package futur.apps.composeproject1.appScreens._Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import futur.apps.composeproject1.R
import futur.apps.composeproject1.quizsystem.ui.theme.progressResultColor
import futur.apps.composeproject1.utils.Category
import futur.apps.composeproject1.utils.Screen
import futur.apps.composeproject1.viewmodels.HomeUiState
import futur.apps.composeproject1.viewmodels.HomeViewModel

/*@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()

    Column(modifier = Modifier.padding(4.dp)) {
        QuizOverviewSection(uiState = uiState)
        QuizCategorySection(
            totalPoints = uiState.totalPoints,
            navController = navController
        )
    }
}*/
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        LoadingScreen() // 👈 your reusable composable
    } else {
        Column(modifier = Modifier.padding(4.dp)) {
            QuizOverviewSection(uiState = uiState)
            QuizCategorySection(
                totalPoints = uiState.totalPoints,
                navController = navController
            )
        }
    }
}


@Composable
fun QuizOverviewSection(uiState: HomeUiState)
{

    val totalPoints = uiState.totalPoints
    val level = totalPoints / 20 + 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    )
    {
        // ---------------- Left Column: Level & Points ----------------
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // LEVEL CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            getGradientColor(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "LEVEL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "$level",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontSize = 30.sp
                            )
                        )
                    }
                }
            }

            // TOTAL POINTS CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            getGradientColor(
                                MaterialTheme.colorScheme.tertiary,
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f)
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "TOTAL POINTS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White.copy(alpha = 0.9f),
                                letterSpacing = 1.sp
                            )
                        )
                        Text(
                            text = "$totalPoints XP",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color.White,
                                fontSize = 24.sp
                            )
                        )
                    }
                }
            }
        }

        // ---------------- Right Column: Profile ----------------
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "YOUR PROFILE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        letterSpacing = 0.5.sp
                    )
                )

                Image(
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = "Profile Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(65.dp)
                        .clip(CircleShape)
                )

                Text(
                    text = uiState.username,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }
    }
}
@Composable
fun QuizCategorySection(
    totalPoints: Int,
    navController: NavController
) {
    val categories = Category.entries.toTypedArray()

    Column {
        Text(
            text = "Quiz Categories",
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(categories) { index, category ->

                val unlockThreshold = index * category.maxPoints
                val isUnlocked = totalPoints >= unlockThreshold
                val progress =
                    ((totalPoints - unlockThreshold).coerceIn(0, category.maxPoints).toFloat()
                            / category.maxPoints)

                val isCompleted = progress >= 1f

                QuizCategoryCard(
                    index = index,
                    score = progress,
                    isActive = isUnlocked,
                    isCompleted = isCompleted, // pass completed flag
                    onClick = {
                        if (isUnlocked) {
                            navController.navigate(Screen.Quiz.createRoute(category))
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun QuizCategoryCard(
    index: Int,
    score: Float,
    isActive: Boolean,
    isCompleted: Boolean = false, // optional flag if you track completion
    onClick: () -> Unit
) {
    val category = Category.entries[index]

    // Card container
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(enabled = isActive) { onClick() }
            .alpha(if (isActive) 1f else 0.6f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(if (isActive) 4.dp else 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = if (isActive) MaterialTheme.colorScheme.onSurface else Color.Gray
        )
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular progress indicator
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = score.coerceIn(0f, 1f),
                    modifier = Modifier.size(50.dp),
                    color = progressResultColor(score * 100),
                    strokeWidth = 5.dp,
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )
                Text(
                    text = "${(score * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isActive) MaterialTheme.colorScheme.onSurface else Color.Gray
                )
            }

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = category.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isActive) MaterialTheme.colorScheme.onSurface else Color(0xFF37474F)
                )

                val statusText = when {
                    isCompleted -> "Completed ✅"
                    !isActive -> "Locked - reach ${(index * 20)} pts"
                    else -> "Tap to start"
                }

                val statusColor = when {
                    isCompleted -> Color(0xFF4CAF50) // green for completed
                    !isActive -> Color(0xFFB0BEC5)   // gray for locked
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f) // active
                }

                Text(
                    text = statusText,
                    fontSize = 12.sp,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Go",
                tint = if (isActive) MaterialTheme.colorScheme.onSurface else Color.Gray
            )
        }
    }
}


fun getGradientColor(startColor: Color, endColor: Color): Brush {
    return Brush.horizontalGradient(listOf(startColor, endColor))
}


@Preview(showBackground = true)
@Composable
fun QuizCategoryCardPreview(){
    QuizCategoryCard(
        index = 1,
        score = 0.5f,
        isActive = false,
        isCompleted = false,
        onClick = {}


    )
}



