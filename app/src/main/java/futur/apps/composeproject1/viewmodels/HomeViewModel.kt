package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.dataStore.AppDataStore
import futur.apps.composeproject1.utils.DefaultCategory
import futur.apps.composeproject1.utils.QuizMode
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ============================================================
// 🧠 Home UI State
// ============================================================
data class HomeUiState(
    val isLoading: Boolean = true,
    val username: String = "Guest",
    val level: Int = 1,
    val mode: QuizMode = QuizMode.DEFAULT,
    val defaultPoints: Map<DefaultCategory, Int> = emptyMap(),
    val defaultTotalPoints: Int = 0,
    val userPoints: Map<String, Int> = emptyMap(),
    val userTotalPoints: Int = 0
) {
    /** Active points map based on current quiz mode */
    val activePointsMap: Map<*, Int>
        get() = if (mode == QuizMode.DEFAULT) defaultPoints else userPoints

    /** Total active points (Default or Custom) */
    val activeTotal: Int
        get() = if (mode == QuizMode.DEFAULT)
            defaultPoints.values.sum()
        else userPoints.values.sum()
}

// ============================================================
// 🧩 Home ViewModel
// ============================================================
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appDataStore: AppDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeDataStore()
    }

    // ------------------------------------------------------------
    // 🔄 Observe all relevant DataStore flows
    // ------------------------------------------------------------
    private fun observeDataStore() {
        viewModelScope.launch {
            combine(
                appDataStore.username,
                appDataStore.builtInCategoryPoints,
                appDataStore.userCategoryPoints,
                appDataStore.globalQuizMode
            ) { username, builtPoints, userPoints, mode ->

                val totalBuiltIn = builtPoints.values.sum()
                val totalUser = userPoints.values.sum()

                HomeUiState(
                    username = username,
                    defaultPoints = builtPoints,
                    userPoints = userPoints,
                    userTotalPoints = totalUser,
                    defaultTotalPoints = totalBuiltIn,
                    mode = mode,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onHomeOpened() {
        viewModelScope.launch {
            appDataStore.updateLastOpenTime()
        }
    }


    // ------------------------------------------------------------
    // 🎯 Update points per category
    // ------------------------------------------------------------
    fun updateBuiltInPoints(category: DefaultCategory, newPoints: Int) {
        viewModelScope.launch {
            val current = _uiState.value.defaultPoints.toMutableMap()
            val updated = newPoints.coerceIn(0, category.maxPoints)
            current[category] = updated
            appDataStore.saveBuiltInCategoryPoints(current)

            _uiState.update {
                it.copy(
                    defaultPoints = current,
                    defaultTotalPoints = current.values.sum()
                )
            }
        }
    }

    fun updateUserPoints(categoryName: String, newPoints: Int) {
        viewModelScope.launch {
            val current = _uiState.value.userPoints.toMutableMap()
            val updated = newPoints.coerceAtLeast(0)
            current[categoryName] = updated
            appDataStore.saveUserCategoryPoints(current)

            _uiState.update {
                it.copy(
                    userPoints = current,
                    userTotalPoints = current.values.sum()
                )
            }
        }
    }

    // ------------------------------------------------------------
    // ♻️ Reset Points
    // ------------------------------------------------------------
    fun resetBuiltInPoints() {
        viewModelScope.launch {
            appDataStore.saveBuiltInCategoryPoints(emptyMap())
            _uiState.update { it.copy(defaultPoints = emptyMap(), defaultTotalPoints = 0) }
        }
    }

    fun resetUserPoints() {
        viewModelScope.launch {
            appDataStore.saveUserCategoryPoints(emptyMap())
            _uiState.update { it.copy(userPoints = emptyMap(), userTotalPoints = 0) }
        }
    }

    /** Reset both built-in and user-created */
    fun resetAllPoints() {
        viewModelScope.launch {
            appDataStore.resetAllCategoryPoints()
            _uiState.update {
                it.copy(
                    defaultPoints = emptyMap(),
                    userPoints = emptyMap(),
                    defaultTotalPoints = 0,
                    userTotalPoints = 0
                )
            }
        }
    }
}
