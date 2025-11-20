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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import futur.apps.composeproject1.R
import futur.apps.composeproject1.RoomDatabase.userroom.UserCategoryEntity
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuizViewModel
import futur.apps.composeproject1.auth.AuthViewModel
import futur.apps.composeproject1.auth.UserProfile
import futur.apps.composeproject1.quiz.ui.theme.progressResultColor
import futur.apps.composeproject1.utils.DefaultCategory
import futur.apps.composeproject1.utils.QuizMode
import futur.apps.composeproject1.utils.Screen
import futur.apps.composeproject1.viewmodels.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.datastore.dataStore
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import futur.apps.composeproject1.quiz.core.AppConfig
import futur.apps.composeproject1.quiz.ui.components.QuizModeSelectorRow

@Composable
fun QuizHomeScreen(
    nav: NavController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    quizViewModel: QuizViewModel = hiltViewModel(),
    userQuizViewModel: UserQuizViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val homeUiState by homeViewModel.uiState.collectAsState()
    val currentQuizMode by quizViewModel.mode.collectAsState()
    val userCategories by userQuizViewModel.categories.collectAsState(initial = emptyList())
    val userProfile by authViewModel.userProfile.collectAsState()

    val totalPoints = when (currentQuizMode) {
        QuizMode.DEFAULT -> homeUiState.defaultTotalPoints
        QuizMode.CUSTOM -> homeUiState.userTotalPoints
    }
    val username = userProfile?.name ?: homeUiState.username

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        QuizHomeContent(
            userProfile = userProfile,
            navController = nav,
            uiState = homeUiState.copy(
                username = username,
                defaultTotalPoints = homeUiState.defaultTotalPoints,
                userTotalPoints = homeUiState.userTotalPoints
            ),
            quizMode = currentQuizMode,
            onModeSelected = { mode -> quizViewModel.saveGlobalQuizMode(mode) },
            userCategories = userCategories
        )

        // 🔄 Local spinner (only covers Home)
        if (homeUiState.isLoading) {
            ScreenLoadingIndicator()
        }
    }

    var lastSynced by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        homeViewModel.onHomeOpened()
    }


    LaunchedEffect(homeUiState.defaultTotalPoints, userProfile?.uid) {
        val points = homeUiState.defaultTotalPoints

        if (!AppConfig.USE_FIRESTORE_SYNC) return@LaunchedEffect
        if (userProfile == null) return@LaunchedEffect
        if (points <= 0) return@LaunchedEffect

        // ⚡ هذا هو المكان الذي نستخدم فيه lastSynced
        if (points == lastSynced) return@LaunchedEffect

        lastSynced = points

        authViewModel.updateDefaultPointsInFirestore(points)
    }



}




/* ============================================================
   🧩 MAIN CONTENT — DISPLAYS QUIZ MODE SECTIONS
   ============================================================ */
@Composable
private fun QuizHomeContent(
    navController: NavController,
    uiState: HomeUiState,
    quizMode: QuizMode,
    onModeSelected: (QuizMode) -> Unit,
    userCategories: List<UserCategoryEntity>,
    userProfile: UserProfile? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // 🟩 Mode selector (Built-in / My Quizzes)
        QuizModeSelectorRow(
            selectedSource = if (quizMode == QuizMode.DEFAULT) 0 else 1,
            onSourceSelected = {
                onModeSelected(if (it == 0) QuizMode.DEFAULT else QuizMode.CUSTOM)
            }
        )
        Spacer(modifier = Modifier.height(8.dp))

        // 🟩 Mode-specific content
        when (quizMode) {
            QuizMode.DEFAULT -> {
                BuiltInOverviewSection(uiState = uiState, userProfile = userProfile)
                BuiltInCategoryList(
                    totalPoints = uiState.defaultTotalPoints,
                    navController = navController
                )
            }
            QuizMode.CUSTOM -> {
                UserOverviewSection(uiState = uiState,userProfile = userProfile)
                UserCategoryList(
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
   🧠 OVERVIEW SECTIONS (LEVEL + POINTS)
   ============================================================ */

/* Built-in quizzes overview */
@Composable
fun BuiltInOverviewSection(uiState: HomeUiState , userProfile: UserProfile?) {
    val totalPoints = uiState.defaultTotalPoints
    val level = totalPoints / 20 + 1

    OverviewRow(
        level = level,
        totalPoints = totalPoints,
        userProfile = userProfile,

    )
}

/* User-created quizzes overview */
@Composable
fun UserOverviewSection(uiState: HomeUiState, userProfile: UserProfile?) {
    val totalPoints = uiState.userTotalPoints
    val level = totalPoints / 20 + 1

    OverviewRow(
        level = level,
        totalPoints = totalPoints,
        userProfile = userProfile
    )
}

/* Shared layout for both modes */
@Composable
private fun OverviewRow(level: Int, totalPoints: Int, userProfile: UserProfile?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCardGradient(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                title = "LEVEL",
                value = "$level"
            )
            StatCardGradient(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f),
                title = "TOTAL POINTS",
                value = "$totalPoints XP",
                isPrimary = false
            )
        }

        PlayerProfileCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            username = userProfile?.name ?: "Guest",
            photoUrl = userProfile?.photoUrl
        )

    }
}

/* Gradient info card for stats (reusable) */
@Composable
fun StatCardGradient(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    isPrimary: Boolean = true
) {
    val start = if (isPrimary) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.tertiary
    val end = start.copy(alpha = 0.6f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Brush.horizontalGradient(listOf(start, end)))
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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

/* ============================================================
   👤 PLAYER PROFILE CARD
   ============================================================ */
@Composable
fun PlayerProfileCard(
    modifier: Modifier = Modifier,
    username: String,
    photoUrl: String? = null
) {
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
                "Player Profile",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    letterSpacing = 0.5.sp
                )
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
            ) {
                if (photoUrl != null) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = "Default Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                }
            }

            // ✅ Auto-adjust text for long usernames
            Text(
                text = username,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}



/* ============================================================
   📚 BUILT-IN CATEGORY LIST
   ============================================================ */
@Composable
fun BuiltInCategoryList(totalPoints: Int, navController: NavController) {
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
                val unlockThreshold = index * category.maxPoints
                val nextThreshold = (index + 1) * category.maxPoints
                val progress = when {
                    totalPoints >= nextThreshold -> 1f
                    totalPoints > unlockThreshold ->
                        ((totalPoints - unlockThreshold).toFloat() / category.maxPoints)
                            .coerceIn(0f, 1f)
                    else -> 0f
                }

                val isUnlocked = totalPoints >= unlockThreshold
                val isCompleted = progress >= 1f

                CategoryCard(
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
   👥 USER-CREATED CATEGORY LIST
   ============================================================ */
@Composable
fun UserCategoryList(
    userCategories: List<UserCategoryEntity>,
    userPoints: Map<String, Int>,
    userTotalPoints: Int,
    navController: NavController
) {
    if (userCategories.isEmpty()) {
        EmptyUserCategoryPlaceholder()
        return
    }

    val maxPerCategory = 20 // Each custom quiz gives up to 20 pts

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
                val unlockThreshold = index * maxPerCategory
                val nextThreshold = (index + 1) * maxPerCategory
                val progress = when {
                    userTotalPoints >= nextThreshold -> 1f
                    userTotalPoints > unlockThreshold ->
                        ((userTotalPoints - unlockThreshold).toFloat() / maxPerCategory)
                            .coerceIn(0f, 1f)
                    else -> 0f
                }

                val isUnlocked = userTotalPoints >= unlockThreshold
                val isCompleted = progress >= 1f

                CategoryCard(
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
   🧱 SHARED CATEGORY CARD DESIGN
   ============================================================ */
@Composable
fun CategoryCard(
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
            // 🟩 Circular progress
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
                    color = if (isActive)
                        MaterialTheme.colorScheme.onSurface
                    else Color(0xFF37474F)
                )

                val statusText = when {
                    isCompleted -> "Completed"
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
   🚫 EMPTY PLACEHOLDER (When no user quizzes exist)
   ============================================================ */
@Composable
fun EmptyUserCategoryPlaceholder() {
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
