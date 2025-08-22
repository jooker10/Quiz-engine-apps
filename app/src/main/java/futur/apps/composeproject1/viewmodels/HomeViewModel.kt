package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.DataStore.DataStoreManager
import futur.apps.composeproject1.utils.CategoryName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject


data class HomeUiState(
    val username: String = "Anouar",
    val level: Int = 1,
    val scores: Map<CategoryName, Int>  = CategoryName.entries.associateWith { 0 },
    val points: Map<CategoryName, Int>  = CategoryName.entries.associateWith { 0 },
    val recentWords: List<String> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val datastore: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                datastore.username,
                datastore.scores,
                datastore.points // assuming you added points: Flow<Map<CategoryName, Int>> in DataStore
            ) { username, scores, points ->
                HomeUiState(
                    username = username,
                    scores = CategoryName.entries.associateWith { scores[it] ?: 0 } ,
                    points = CategoryName.entries.associateWith { points[it] ?: 0 },
                    // merge scores with points or keep separate
                    // here we show points as score per category
                    level = 1,
                    recentWords = emptyList()
                )
            }.collect { _uiState.value = it }
            }
        }
}
