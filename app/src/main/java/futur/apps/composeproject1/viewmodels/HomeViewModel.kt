/*
package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.dataStore.AppDataStore
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
    private val appDataStore : AppDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        // Combine username and points from DataStore
        viewModelScope.launch {
            combine(
                appDataStore.username,     // Flow of username
                appDataStore.categoryPoints        // Flow of points by category
            ) { username, categoryPoints ->
                HomeUiState(
                    username = username,
                    pointsByCategory = categoryPoints
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
            appDataStore.saveCategoryPoints(currentPoints) // Save updated points to DataStore
            }
        }
}
*/
package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.dataStore.AppDataStore
import futur.apps.composeproject1.utils.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

// -------------------- Home UI State --------------------
data class HomeUiState(
    val username: String = "Guest",                      // User name
    val level: Int = 1,                                  // Optional: user level system
    val totalPoints: Int = 0,                            // Total points (sum of all categories)
    val pointsByCategory: Map<Category, Int> = emptyMap(), // Individual category points
    val recentWords: List<String> = emptyList()          // Recently viewed words
)

// -------------------- Home ViewModel --------------------
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appDataStore: AppDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        // Combine username and category points from DataStore
        viewModelScope.launch {
            combine(
                appDataStore.username,
                appDataStore.categoryPoints
            ) { username, categoryPoints ->
                val totalPoints = categoryPoints.values.sum() // Total points = sum of all category scores
                HomeUiState(
                    username = username,
                    pointsByCategory = categoryPoints,
                    totalPoints = totalPoints
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    // -------------------- Update Points for a Category --------------------
    fun updatePoints(category: Category, newPoints: Int) {
        viewModelScope.launch {
            val currentPoints = _uiState.value.pointsByCategory.toMutableMap()
            val updatedPoints = newPoints.coerceAtMost(category.maxPoints) // Limit to max allowed

            // Update map
            currentPoints[category] = updatedPoints

            // Save to DataStore
            appDataStore.saveCategoryPoints(currentPoints)

            // Recalculate total immediately (for instant UI feedback)
            val totalPoints = currentPoints.values.sum()
            _uiState.value = _uiState.value.copy(
                pointsByCategory = currentPoints,
                totalPoints = totalPoints
            )
        }
    }
}
