package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.DataStore.DataStoreManager
import futur.apps.composeproject1.utils.CategoryName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject


data class HomeUiState(
    val username: String = "Guest",
    val level: Int = 1,
    val points: Int = 0,
    val pointsList: Map<CategoryName, Int> = emptyMap(),
    val recentWords: List<String> = emptyList()
)


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState


    init {
        viewModelScope.launch {
            combine(
                dataStoreManager.username,
                dataStoreManager.setPointsList
            ) { username, scores ->
                HomeUiState(
                    username = username,
                    pointsList = scores
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun updateScore(category: CategoryName, score: Int) {
        viewModelScope.launch {
            val currentScores = _uiState.value.pointsList.toMutableMap()
            currentScores[category] = score.coerceAtMost(category.maxScore)
            dataStoreManager.setPointSList(currentScores) // حفظ في DataStore
            }
        }
}

