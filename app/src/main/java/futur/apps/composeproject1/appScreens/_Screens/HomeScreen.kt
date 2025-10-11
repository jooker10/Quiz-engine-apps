/*
package futur.apps.composeproject1.home

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import futur.apps.composeproject1.R
import futur.apps.composeproject1.ui.theme.lightGreen
import futur.apps.composeproject1.ui.theme.lightOrange
import futur.apps.composeproject1.utils.Category
import futur.apps.composeproject1.viewmodels.QuizViewModel
import futur.apps.composeproject1.utils.Screen

@Composable
fun HomeScreen(
    navController: NavController,
    quizViewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by quizViewModel.quizUiState.collectAsState()
    val totalPoints = quizViewModel.getTotalPoints()

    Column(modifier = Modifier.padding(4.dp)) {
        InfoSection(quizViewModel)
        CategoryScoreSection(
            totalPoints = totalPoints,
            navController = navController
        )
    }
}

@Composable
fun InfoSection(quizViewModel: QuizViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Ranking
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(getGradientColor(lightOrange, lightOrange))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("YOUR RANKING", color = Color.White, fontSize = 12.sp)
                        Text("100", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Coins (Total Points)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(getGradientColor(lightGreen, lightGreen))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("COINS EARNED", color = Color.White, fontSize = 12.sp)
                        Text(
                            quizViewModel.getTotalPoints().toString(),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Profile Card
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "YOUR PROFILE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                    )
                    Spacer(Modifier.height(8.dp))

                    Image(
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Anouar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "User",
                        color = Color.Gray,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryScoreSection(
    totalPoints: Int,
    navController: NavController
) {
    val categories = Category.values()

    Column {
        Text(
            text = "Quiz Categories",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray.copy(0.7f),
            modifier = Modifier.padding(8.dp)
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(),
        ) {
            LazyColumn {
                itemsIndexed(categories) { index, category ->
                    val unlockThreshold = index * category.maxPoints
                    val isUnlocked = totalPoints >= unlockThreshold
                    val progress =
                        ((totalPoints - unlockThreshold).coerceIn(0, category.maxPoints)
                            .toFloat() / category.maxPoints)

                    CardScoreHome(
                        index = index,
                        score = progress,
                        isActive = isUnlocked,
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
}

@Composable
fun CardScoreHome(index: Int, score: Float, isActive: Boolean, onClick: () -> Unit) {
    val category = Category.entries[index]

    val cardBackground = if (isActive) Color.White else Color(0xFFF5F5F5)
    val cardElevation = if (isActive) 8.dp else 2.dp
    val titleColor = if (isActive) Color(0xFF37474F) else Color(0xFF90A4AE)
    val subtitleColor = if (isActive) Color(0xFF607D8B) else Color(0xFFB0BEC5)
    val iconColor = if (isActive) Color(0xFF546E7A) else Color(0xFFB0BEC5)
    val progressGradientEnd = if (isActive) category.themeColor else Color(0xFFCFD8DC)
    val progressTrackColor = if (isActive) Color(0xFFE3F2FD) else Color(0xFFF0F0F0)

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .alpha(if (isActive) 1f else 0.6f)
            .clickable(enabled = isActive) { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(cardElevation),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = score.coerceIn(0f, 1f),
                    modifier = Modifier.size(50.dp),
                    color = progressGradientEnd,
                    strokeWidth = 5.dp,
                    trackColor = progressTrackColor
                )
                Text(
                    text = "${(score * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = progressGradientEnd
                )
            }

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = category.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )
                Text(
                    text = if (isActive) "Tap to start" else "Locked - reach ${(index * 20)} pts",
                    fontSize = 12.sp,
                    color = subtitleColor
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Go",
                tint = iconColor
            )
        }
    }
}

fun getGradientColor(startColor: Color, endColor: Color): Brush {
    return Brush.horizontalGradient(listOf(startColor, endColor))
}

*/
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import futur.apps.composeproject1.R

import futur.apps.composeproject1.utils.Category
import futur.apps.composeproject1.utils.Screen
import futur.apps.composeproject1.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by homeViewModel.uiState.collectAsState()

    Column(modifier = Modifier.padding(4.dp)) {
        InfoSection(homeViewModel)
        CategoryScoreSection(
            totalPoints = uiState.totalPoints,
            navController = navController
        )
    }
}

@Composable
fun InfoSection(homeViewModel: HomeViewModel) {
    val uiState by homeViewModel.uiState.collectAsState()
    val totalPoints = uiState.totalPoints // already computed
    val level = totalPoints / 20 + 1 // Example: every 20 points = 1 level

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left Column: Level & Total Points
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Level Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(getGradientColor(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.secondary.copy(0.5f)))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "LEVEL",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Text(
                            "$level",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Total Points Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(getGradientColor(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.tertiary.copy(0.5f)))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "TOTAL POINTS",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Text(
                            "$totalPoints XP",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Profile Card
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "YOUR PROFILE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                    )
                    Spacer(Modifier.height(8.dp))

                    Image(
                        painter = painterResource(id = R.drawable.app_icon),
                        contentDescription = "Profile Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        uiState.username,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "User",
                        color = Color.Gray,
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryScoreSection(
    totalPoints: Int,
    navController: NavController
) {
    val categories = Category.entries.toTypedArray()

    Column {
        Text(
            text = "Quiz Categories",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray.copy(0.7f),
            modifier = Modifier.padding(8.dp)
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxWidth(),
        ) {
            LazyColumn {
                itemsIndexed(categories) { index, category ->
                    val unlockThreshold = index * category.maxPoints
                    val isUnlocked = totalPoints >= unlockThreshold
                    val progress =
                        ((totalPoints - unlockThreshold).coerceIn(0, category.maxPoints)
                            .toFloat() / category.maxPoints)

                    CardScoreHome(
                        index = index,
                        score = progress,
                        isActive = isUnlocked,
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
}

@Composable
fun CardScoreHome(index: Int, score: Float, isActive: Boolean, onClick: () -> Unit) {
    val category = Category.entries[index]

    val cardBackground = if (isActive) Color.White else Color(0xFFF5F5F5)
    val cardElevation = if (isActive) 8.dp else 2.dp
    val titleColor = if (isActive) Color(0xFF37474F) else Color(0xFF90A4AE)
    val subtitleColor = if (isActive) Color(0xFF607D8B) else Color(0xFFB0BEC5)
    val iconColor = if (isActive) Color(0xFF546E7A) else Color(0xFFB0BEC5)
    val progressGradientEnd = if (isActive) category.themeColor else Color(0xFFCFD8DC)
    val progressTrackColor = if (isActive) Color(0xFFE3F2FD) else Color(0xFFF0F0F0)

    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .alpha(if (isActive) 1f else 0.6f)
            .clickable(enabled = isActive) { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(cardElevation),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = score.coerceIn(0f, 1f),
                    modifier = Modifier.size(50.dp),
                    color = progressGradientEnd,
                    strokeWidth = 5.dp,
                    trackColor = progressTrackColor
                )
                Text(
                    text = "${(score * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = progressGradientEnd
                )
            }

            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(
                    text = category.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = titleColor
                )
                Text(
                    text = if (isActive) "Tap to start" else "Locked - reach ${(index * 20)} pts",
                    fontSize = 12.sp,
                    color = subtitleColor
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Go",
                tint = iconColor
            )
        }
    }
}

fun getGradientColor(startColor: Color, endColor: Color): Brush {
    return Brush.horizontalGradient(listOf(startColor, endColor))
}
