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
    val points: Int = 0,
    val scores: Map<CategoryName, Int> = emptyMap(),
    val recentWords: List<String> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val datastore: DataStoreManager) : ViewModel() {

        private val _uiState = MutableStateFlow(HomeUiState())
        val uiState : StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                datastore.username,
                datastore.scores
            ) {
                username, scores ->
                HomeUiState(
                    username = username,
                    scores = scores
                )
            }.collect {
                _uiState.value = it
            }
        }
    }
}