package futur.apps.composeproject1.appScreens._Screens

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
import futur.apps.composeproject1.appScreens.TableCard
import futur.apps.composeproject1.RoomDatabase.DbViewModel
import futur.apps.composeproject1._Mains.EffectsViewModel
import futur.apps.composeproject1.utils.TableCategory
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TableScreen(
    dbViewModel: DbViewModel = hiltViewModel(),
    effectsViewModel: EffectsViewModel = hiltViewModel()
) {
    // Stable, non-null list from enum entries
    val categories = remember { TableCategory.entries.toList() }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { categories.size }
    )
    val scope = rememberCoroutineScope()

    Column {
        // ---------- Tabs ----------
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color(0xFF1976D2),
            contentColor = Color.White,
            edgePadding = 8.dp,
            indicator = { tabPositions ->
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
            categories.forEachIndexed { index, cat ->
                // cat غير قابل لأن يكون null مع enum
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = {
                        Text(
                            text = cat.displayName,
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                            color = Color.White
                        )
                    }
                )
            }
        }

        // ---------- Pages ----------
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val cat = categories.getOrNull(page) ?: return@HorizontalPager

            // Guaranteed non-null StateFlow from the map (by construction)
            val itemsFlow = dbViewModel.categoryFlows[cat]
            if (itemsFlow == null) {
                // Defensive UI (shouldn't happen if map covers all)
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No data for ${cat.displayName}")
                }
                return@HorizontalPager
            }

            val items by itemsFlow.collectAsState()

            if (items.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No items yet")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize().padding(8.dp)
                ) {
                    items(items) { item ->
                        TableCard(
                            category = item,
                            onSpeakClick = { effectsViewModel.speak(item.en) }
                        )
                    }
                }
            }
            }
        }
}

/*@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TableScreen(
    dbViewModel : DbViewModel = hiltViewModel(),
    effectsViewModel: EffectsViewModel = hiltViewModel()) {
    val tabs = listOf(
        "Verbs",
        "Sentences",
        "Phrasal verbs",
        "Nouns",
        "Adjectives",
        "Adverbs",
        "Idioms"
    )
    val categories  = listOf(
        dbViewModel.verbs.collectAsState(),
        dbViewModel.sentences.collectAsState(),
        dbViewModel.phrasalVerbs.collectAsState(),
        dbViewModel.nouns.collectAsState(),
        dbViewModel.adjectives.collectAsState(),
        dbViewModel.adverbs.collectAsState(),
        dbViewModel.idioms.collectAsState(),
    )


    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabs.size })
    val scope = rememberCoroutineScope()


    Column {

        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color(0xFF1976D2), // لون الخلفية
            contentColor = Color.White,
            edgePadding = 8.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier
                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                        .height(3.dp),
                    color = Color.Yellow
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = {
                        Text(
                            title,
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                            color = Color.White
                        )
                    }
                )
            }
        }


        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = {
                        items(categories[page].value) { category ->
                            TableCard(
                                category = category,
                                onSpeakClick = {
                                    effectsViewModel.speak(category.en)
                                }
                            )

                        }

                    }
                )
            }

        }
    }
}*/

@Preview
@Composable
fun PreviewTabs() {
    TableScreen()
}