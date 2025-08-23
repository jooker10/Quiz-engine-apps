package futur.apps.composeproject1.appScreens.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import futur.apps.composeproject1.viewmodels.DatabaseViewModel
import futur.apps.composeproject1.appScreens.TableCard
import futur.apps.composeproject1.viewmodels.EffectsViewModel
import futur.apps.composeproject1.utils.Category
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * TableScreen displays all quiz items grouped by categories in a tabbed horizontal pager.
 * Each tab corresponds to a TableCategory (Verbs, Nouns, etc.).
 * Items can be spoken aloud via EffectsViewModel.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TableScreen(
    dbViewModel: DatabaseViewModel = hiltViewModel(),
    effectsViewModel: EffectsViewModel = hiltViewModel()
) {
    // List of all categories for tabs
    val categories = remember { Category.entries.toList() }

    // Pager state to control the horizontal pager
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { categories.size }
    )
    val coroutineScope = rememberCoroutineScope()

    Column {
        // ---------- Tabs ----------
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White,
            edgePadding = 8.dp,
            indicator = { tabPositions ->
                // Defensive: Ensure tabPositions is not empty
                if (tabPositions.isNotEmpty()) {
                    val safeIndex = pagerState.currentPage.coerceIn(0, tabPositions.lastIndex)
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[safeIndex])
                            .height(3.dp),
                        color = Color.Yellow
                    )
                }
            }
        ) {
            categories.forEachIndexed { index, category ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = {
                        Text(
                            text = category.displayName.uppercase(Locale.getDefault()),
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                            color = Color.White
                        )
                    }
                )
            }
        }

        // ---------- Pager Pages ----------
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pageIndex ->
            val category = categories.getOrNull(pageIndex) ?: return@HorizontalPager

            // Get StateFlow of items for this category
            val itemsFlow = dbViewModel.categoryData[category]
            if (itemsFlow == null) {
                // Defensive UI if category flow is missing
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No data available for ${category.displayName}")
                }
                return@HorizontalPager
            }

            val items by itemsFlow.collectAsState()

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No items yet in ${category.displayName}")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    items(items) { quizItem ->
                        TableCard(
                            category = quizItem,
                            onSpeakClick = { effectsViewModel.speak(quizItem.en) }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TableScreenPreview() {
    TableScreen()
}