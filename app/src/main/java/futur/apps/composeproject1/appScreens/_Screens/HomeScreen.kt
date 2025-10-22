package futur.apps.composeproject1.appScreens._Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import futur.apps.composeproject1.RoomDatabase.userroom.UserCategoryEntity
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuizViewModel
import futur.apps.composeproject1.quizsystem.ui.theme.progressResultColor
import futur.apps.composeproject1.utils.DefaultCategory
import futur.apps.composeproject1.utils.QuizMode
import futur.apps.composeproject1.utils.Screen
import futur.apps.composeproject1.viewmodels.*

/* ============================================================
   🏠 Home Screen — Unified for Built-in & User-Created
   ============================================================ */
@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    quizViewModel: QuizViewModel = hiltViewModel(),
    userQuizViewModel: UserQuizViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val currentQuizMode by quizViewModel.mode.collectAsState()
    val userStats by quizViewModel.stats.collectAsState()
    val userCategories by userQuizViewModel.categories.collectAsState(initial = emptyList())

    when {
        uiState.isLoading -> LoadingScreen()
        else -> HomeContent(
            navController = navController,
            uiState = uiState,
            quizMode = currentQuizMode,
            onModeSelected = { mode -> quizViewModel.saveGlobalQuizMode(mode) },
            userStats = userStats,
            userCategories = userCategories
        )
    }
}

/* ============================================================
   🧩 Home Content
   ============================================================ */
@Composable
private fun HomeContent(
    navController: NavController,
    uiState: HomeUiState,
    quizMode: QuizMode,
    onModeSelected: (QuizMode) -> Unit,
    userStats: StatsUiState,
    userCategories: List<UserCategoryEntity>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        QuizModeSelector(selectedMode = quizMode, onModeSelected = onModeSelected)
        Spacer(modifier = Modifier.height(8.dp))

        when (quizMode) {
            QuizMode.DEFAULT -> {
                QuizOverviewSection(uiState = uiState)
                QuizCategorySection(totalPoints = uiState.defaultTotalPoints, navController = navController)
            }

            QuizMode.CUSTOM -> {
                UserCreatedOverviewSection(uiState = uiState)
                UserCreatedCategorySection(
                    userCategories = userCategories,
                    userPoints = uiState.userPoints,
                    userTotalPoints = uiState.userTotalPoints,
                    navController = navController
                )
            }
        }
    }
}

/* ============================================================
   🔘 Quiz Mode Selector
   ============================================================ */
@Composable
fun QuizModeSelector(selectedMode: QuizMode, onModeSelected: (QuizMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuizModeChip(
            text = "Built-in",
            isSelected = selectedMode == QuizMode.DEFAULT,
            onClick = { onModeSelected(QuizMode.DEFAULT) }
        )
        QuizModeChip(
            text = "User Created",
            isSelected = selectedMode == QuizMode.CUSTOM,
            onClick = { onModeSelected(QuizMode.CUSTOM) }
        )
    }
}

@Composable
fun QuizModeChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = if (isSelected) 6.dp else 2.dp,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/* ============================================================
   🧠 Overview (Built-in)
   ============================================================ */
@Composable
fun QuizOverviewSection(uiState: HomeUiState) {
    val totalPoints = uiState.defaultTotalPoints
    val level = totalPoints / 20 + 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GradientInfoCard(title = "LEVEL", value = "$level")
            GradientInfoCard(title = "TOTAL POINTS", value = "$totalPoints XP", isPrimary = false)
        }

        ProfileCard(username = uiState.username, modifier = Modifier.weight(1f))
    }
}

/* ============================================================
   👤 Overview (User-Created)
   ============================================================ */
@Composable
fun UserCreatedOverviewSection(uiState: HomeUiState) {
    val totalPoints = uiState.userTotalPoints
    val level = totalPoints / 20 + 1

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GradientInfoCard(title = "LEVEL", value = "$level")
            GradientInfoCard(title = "TOTAL POINTS", value = "$totalPoints XP", isPrimary = false)
        }

        ProfileCard(username = uiState.username, modifier = Modifier.weight(1f))
    }
}

/* ============================================================
   🪪 Shared Profile Card
   ============================================================ */
@Composable
fun ProfileCard(username: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxHeight(),
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
                "YOUR PROFILE",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    letterSpacing = 0.5.sp
                )
            )
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "Profile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(65.dp)
                    .clip(CircleShape)
            )
            Text(
                username,
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface)
            )
        }
    }
}

/* ============================================================
   🎨 Gradient Info Card
   ============================================================ */
@Composable
private fun GradientInfoCard(title: String, value: String, isPrimary: Boolean = true) {
    val startColor =
        if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
    val endColor = startColor.copy(alpha = 0.6f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(getGradientColor(startColor, endColor))
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontSize = 26.sp
                    )
                )
            }
        }
    }
}

/* ============================================================
   📚 Built-in Category Section
   ============================================================ */
@Composable
fun QuizCategorySection(totalPoints: Int, navController: NavController) {
    val categories = DefaultCategory.entries.toTypedArray()

    Column {
        Text(
            text = "Quiz Categories",
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(12.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(categories) { index, category ->

                // ------------------------------------------------------------
                // 🧩 Improved Progress Logic
                // ------------------------------------------------------------
                val unlockThreshold = index * category.maxPoints       // points needed to unlock this category
                val nextThreshold = (index + 1) * category.maxPoints   // points needed to unlock the next category

                val progress = when {
                    // ✅ Category fully completed (player reached the next threshold)
                    totalPoints >= nextThreshold -> 1f

                    // 🔹 Category partially completed
                    totalPoints > unlockThreshold -> {
                        ((totalPoints - unlockThreshold).toFloat() / category.maxPoints)
                            .coerceIn(0f, 1f)
                    }

                    // 🔒 Locked category
                    else -> 0f
                }

                val isUnlocked = totalPoints >= unlockThreshold
                val isCompleted = progress >= 1f

                // ------------------------------------------------------------
                // 🎴 Category Card
                // ------------------------------------------------------------
                QuizCategoryCard(
                    title = category.displayName,
                    score = progress,
                    isActive = isUnlocked,
                    isCompleted = isCompleted
                ) {
                    if (isUnlocked) {
                        navController.navigate(
                            Screen.Quiz.createRoute(QuizMode.DEFAULT, category.name)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


/* ============================================================
   👥 User-Created Category Section
   ============================================================ */
@Composable
fun UserCreatedCategorySection(
    userCategories: List<UserCategoryEntity>,
    userPoints: Map<String, Int>,
    userTotalPoints: Int,
    navController: NavController
) {
    if (userCategories.isEmpty()) {
        UserCreatedPlaceholder()
        return
    }

    val maxPerCategory = 20 // maximum points each user category can give

    Column {
        Text(
            text = "Your Quizzes",
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            ),
            modifier = Modifier.padding(12.dp)
        )

        LazyColumn {
            itemsIndexed(userCategories) { index, category ->
                val catName = category.name
                val catPoints = userPoints[catName] ?: 0

                // ------------------------------------------------------------
                // 🧩 Improved Progress Logic (consistent with built-in section)
                // ------------------------------------------------------------
                val unlockThreshold = index * maxPerCategory
                val nextThreshold = (index + 1) * maxPerCategory

                val progress = when {
                    // ✅ Completed: total points already passed next threshold
                    userTotalPoints >= nextThreshold -> 1f

                    // 🔹 In progress between thresholds
                    userTotalPoints > unlockThreshold -> {
                        ((userTotalPoints - unlockThreshold).toFloat() / maxPerCategory)
                            .coerceIn(0f, 1f)
                    }

                    // 🔒 Locked
                    else -> 0f
                }

                val isUnlocked = userTotalPoints >= unlockThreshold
                val isCompleted = progress >= 1f

                // ------------------------------------------------------------
                // 🎴 Category Card
                // ------------------------------------------------------------
                QuizCategoryCard(
                    title = catName,
                    score = progress,
                    isActive = isUnlocked,
                    isCompleted = isCompleted
                ) {
                    if (isUnlocked) {
                        navController.navigate(
                            Screen.Quiz.createRoute(QuizMode.CUSTOM, catName)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


/* ============================================================
   🧱 Shared Category Card
   ============================================================ */
@Composable
fun QuizCategoryCard(
    title: String,
    score: Float,
    isActive: Boolean,
    isCompleted: Boolean = false,
    onClick: () -> Unit
) {
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
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = score.coerceIn(0f, 1f),
                    modifier = Modifier.size(50.dp),
                    color = progressResultColor(score * 100),
                    strokeWidth = 5.dp,
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )
                Text(
                    "${(score * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isActive) MaterialTheme.colorScheme.onSurface else Color.Gray
                )
            }

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isActive) MaterialTheme.colorScheme.onSurface else Color(0xFF37474F)
                )

                val statusText = when {
                    isCompleted -> "Completed ✅"
                    !isActive -> "Locked"
                    else -> "Tap to start"
                }

                Text(
                    statusText,
                    fontSize = 12.sp,
                    color = when {
                        isCompleted -> Color(0xFF4CAF50)
                        !isActive -> Color(0xFFB0BEC5)
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Filled.ArrowForward, contentDescription = "Go")
        }
    }
}

/* ============================================================
   🚫 Empty Placeholder
   ============================================================ */
@Composable
fun UserCreatedPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No user-created quizzes yet.",
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        )
    }
}



@Preview(showBackground = true)
@Composable
fun QuizCategoryCardPreview() {
    QuizCategoryCard(title = "Verbs", score = 0.5f, isActive = true, onClick = {})
}
