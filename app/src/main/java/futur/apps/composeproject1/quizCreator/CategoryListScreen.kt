package futur.apps.composeproject1.quizCreator

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import futur.apps.composeproject1.RoomDatabase.userroom.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    navController: NavController,
    viewModel: UserQuizViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState(initial = emptyList())
    var showAdd by remember { mutableStateOf(false) }
    var editCat by remember { mutableStateOf<UserCategoryEntity?>(null) }
    var catToDelete by remember { mutableStateOf<UserCategoryEntity?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAdd = true },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .padding(WindowInsets.navigationBars.asPaddingValues()) // adaptive bottom space
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Category",
                    tint = Color.White
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (categories.isEmpty()) {
                item {
                    Box(
                        Modifier
                            .fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No categories yet.\nTap the + button to add one.",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(categories) { cat ->
                    CategoryCardModern(
                        category = cat,
                        onClick = { navController.navigate("questions/${cat.id}") },
                        onEdit = { editCat = cat },
                        onDelete = { catToDelete = cat }
                    )
                }
            }
        }
    }

    // Add new category dialog
    if (showAdd) {
        CategoryDialog(
            title = "Add Category",
            onDismiss = { showAdd = false },
            onConfirm = { name, desc ->
                viewModel.addCategory(name, desc)
                showAdd = false
            }
        )
    }

    // Edit category dialog
    editCat?.let { cat ->
        CategoryDialog(
            title = "Edit Category",
            initialName = cat.name,
            initialDesc = cat.description ?: "",
            onDismiss = { editCat = null },
            onConfirm = { name, desc ->
                viewModel.updateCategory(cat, name, desc)
                editCat = null
            }
        )
    }

    // Confirm delete dialog
    catToDelete?.let { cat ->
        AlertDialog(
            onDismissRequest = { catToDelete = null },
            title = { Text("Delete Category") },
            text = {
                Text("Are you sure you want to delete \"${cat.name}\"? " +
                        "All its questions will also be removed.")
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteCategory(cat)
                    catToDelete = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { catToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/* ------------------------------------------------------------------
   ✨ Modern Card — Same vibe as HomeScreen
------------------------------------------------------------------- */
@Composable
private fun CategoryCardModern(
    category: UserCategoryEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.08f)
                        )
                    )
                )
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🟣 Circle avatar
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category.name.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = category.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                category.description?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = if (it.length > 40) it.take(37) + "..." else it,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
