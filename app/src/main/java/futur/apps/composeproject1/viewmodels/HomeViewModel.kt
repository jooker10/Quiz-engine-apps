package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.dataStore.AppDataStore
import futur.apps.composeproject1.utils.BuildInCategory
import futur.apps.composeproject1.utils.QuizMode
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// -------------------- Home UI State --------------------
data class HomeUiState(
    val isLoading: Boolean = true,
    val username: String = "Guest",
    val level: Int = 1,
    val totalPoints: Int = 0,
    val mode: QuizMode = QuizMode.BUILT_IN,
    val builtInPoints: Map<BuildInCategory, Int> = emptyMap(),
    val userPoints: Map<String, Int> = emptyMap()
) {
    /** Returns points of the current mode */
    val activePointsMap: Map<*, Int>
        get() = if (mode == QuizMode.BUILT_IN) builtInPoints else userPoints

    /** Returns active total */
    val activeTotal: Int
        get() = if (mode == QuizMode.BUILT_IN)
            builtInPoints.values.sum()
        else userPoints.values.sum()
}

// -------------------- Home ViewModel --------------------
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appDataStore: AppDataStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                appDataStore.username,
                appDataStore.builtInCategoryPoints,
                appDataStore.userCategoryPoints,
                appDataStore.globalQuizMode
            ) { username, builtPoints, userPoints, mode ->
                HomeUiState(
                    username = username,
                    builtInPoints = builtPoints,
                    userPoints = userPoints,
                    mode = mode,
                    totalPoints = if (mode == QuizMode.BUILT_IN)
                        builtPoints.values.sum()
                    else userPoints.values.sum(),
                    isLoading = false
                )
            }.collect { _uiState.value = it }
        }
    }

    // -------------------- Update Points --------------------
    fun updatePoints(category: Any, newPoints: Int) {
        viewModelScope.launch {
            when (_uiState.value.mode) {
                QuizMode.BUILT_IN -> {
                    if (category !is BuildInCategory) return@launch
                    val current = _uiState.value.builtInPoints.toMutableMap()
                    current[category] = newPoints.coerceAtLeast(0)
                    appDataStore.saveBuiltInCategoryPoints(current)
                }
                QuizMode.USER_CREATED -> {
                    if (category !is String) return@launch
                    val current = _uiState.value.userPoints.toMutableMap()
                    current[category] = newPoints.coerceAtLeast(0)
                    appDataStore.saveUserCategoryPoints(current)
                }
            }
        }
    }

    // -------------------- Reset All Points --------------------
    fun resetAllPoints() {
        viewModelScope.launch {
            appDataStore.resetAllCategoryPoints()
            _uiState.update { it.copy(builtInPoints = emptyMap(), userPoints = emptyMap(), totalPoints = 0) }
        }
    }
}
