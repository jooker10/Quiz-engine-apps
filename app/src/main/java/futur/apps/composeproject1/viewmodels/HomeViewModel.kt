package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.dataStore.UserPreferencesManager
import futur.apps.composeproject1.utils.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

// -------------------- Home UI State --------------------
data class HomeUiState(
    val username: String = "Guest",                     // Username of the user
    val level: Int = 1,                                 // User level (optional feature)
    val pointsByCategory: Map<Category, Int> = emptyMap(), // Points for each category
    val recentWords: List<String> = emptyList()        // Recently viewed words
)

// -------------------- Home ViewModel --------------------
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        // Combine username and points from DataStore
        viewModelScope.launch {
            combine(
                preferencesManager.username,     // Flow of username
                preferencesManager.categoryPoints        // Flow of points by category
            ) { username, pointsMap ->
                HomeUiState(
                    username = username,
                    pointsByCategory = pointsMap
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    // -------------------- Update Points --------------------
    fun updatePoints(category: Category, points: Int) {
        viewModelScope.launch {
            val currentPoints = _uiState.value.pointsByCategory.toMutableMap()
            currentPoints[category] = points.coerceAtMost(category.maxPoints)
            preferencesManager.saveCategoryPoints(currentPoints) // Save updated points to DataStore
            }
        }
}
