package futur.apps.composeproject1.viewmodels

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
// -------------------- Data class for UI state --------------------
data class HomeUiState(
    val username: String = "Anouar",
    val level: Int = 1,
    val points: Int = 0,
    val scores: Map<String, Int> = mapOf(
        "Verbs" to 10,
        "Adjectives" to 5,
        "Phrases" to 8
    ),
    val recentWords: List<String> = listOf("Hello", "World", "Compose", "Kotlin")
)

// -------------------- ViewModel --------------------
@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Example: method to update points
    fun addPoints(newPoints: Int) {
        viewModelScope.launch {
            _uiState.update { current ->
                current.copy(points = current.points + newPoints)
            }
        }
    }

    // Example: method to update scores
    fun updateScore(category: String, score: Int) {
        viewModelScope.launch {
            _uiState.update { current ->
                current.copy(scores = current.scores + (category to score))
            }
        }
    }

    // Example: add a new recent word
    fun addRecentWord(word: String) {
        viewModelScope.launch {
            _uiState.update { current ->
                current.copy(recentWords = (listOf(word) + current.recentWords).take(10))
            }
            }
        }
}*/


data class HomeUiState(
    val username: String = "Anouar",
    val level: Int = 1,
    val points: Int = 0,
    val scores: Map<String, Int> = emptyMap(),
    val recentWords: List<String> = emptyList()
)

// -------------------- Category Model --------------------
data class Category(
    val displayName: String,
    val color: Color
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // تعريف 7 فئات افتراضية
    private val categories = listOf(
        Category("Verbs", Color(0xFF42A5F5)),
        Category("Nouns", Color(0xFF66BB6A)),
        Category("Adjectives", Color(0xFFFFCA28)),
        Category("Phrases", Color(0xFFAB47BC)),
        Category("Expressions", Color(0xFF26C6DA)),
        Category("Idioms", Color(0xFFEF5350)),
        Category("Grammar", Color(0xFF8D6E63))
    )

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Scores تجريبية لكل فئة
            val scores = categories.associate { it.displayName to (10..100).random() }

            // Recent Words تجريبية
            val recentWords = listOf("Hello", "World", "Compose", "Kotlin", "Android", "Jetpack", "Quiz")

            // تحديث UI State
            _uiState.value = HomeUiState(
                username = "Anouar",
                level = 3,
                points = scores.values.sum(),
                scores = scores,
                recentWords = recentWords
            )
        }
    }

    // يمكن لاحقًا إضافة دوال لتحديث Scores عند الاجابة على Quiz
    fun updateScore(categoryName: String, newScore: Int) {
        val updatedScores = _uiState.value.scores.toMutableMap()
        updatedScores[categoryName] = newScore
        _uiState.value = _uiState.value.copy(
            scores = updatedScores,
            points = updatedScores.values.sum()
        )
    }

    // الحصول على الفئات (لاستخدامها في HomeScreen)
    fun getCategories(): List<Category> = categories
}