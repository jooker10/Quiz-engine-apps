/*
package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.dataStore.DataStoreManager
import futur.apps.composeproject1.QuizFiles.Question
import futur.apps.composeproject1.RoomDatabase.DataRepository
import futur.apps.composeproject1.utils.CategoryName
import futur.apps.composeproject1.utils.CategoryType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class QuizUiState(
    val isLoading: Boolean = true,
    val category: CategoryName? = null,
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val score: Int = 0,
    val earnedPoints: Int = 0,
    val pointsByCategory: Map<CategoryName, Int> = emptyMap(),
    val selectedOptionText: String? = null,
    val isAnswerChecked: Boolean = false,
    val timeLeft: Int = 15,
    val timeLimit: Int = 10,
    val error: String? = null,
    val isFinished: Boolean = false
)

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val dataStore: DataStoreManager,
    private val repository: DataRepository,
) : ViewModel() {

    // ---------- UI State ----------
    private val zeroPoints: Map<CategoryName, Int> = CategoryName.entries.associateWith { 0 }

    private val _quizUiState =
        MutableStateFlow(QuizUiState(isLoading = false, pointsByCategory = zeroPoints))
    val quizUiState: StateFlow<QuizUiState> = _quizUiState.asStateFlow()

    // ---------- Events ----------
    private val _events = MutableSharedFlow<EffectsEvent>()
    val events = _events.asSharedFlow()

    // ---------- Timer ----------
    private var timerJob: Job? = null
    private val maxTime = 15

    // ---------- DataStore ----------
    val username: StateFlow<String> = dataStore.username.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        "User"
    )

    init {
        // Load saved points from DataStore
        viewModelScope.launch {
            dataStore.points.collect { savedPoints ->
                _quizUiState.update { it.copy(pointsByCategory = zeroPoints + savedPoints) }
            }
        }
    }

    // ---------- Quiz Logic ----------
    fun setCategory(category: CategoryName?) {
        if (category == null) {
            _quizUiState.update { it.copy(error = "Invalid category") }
            return
        }
        _quizUiState.update { it.copy(category = category) }
        startNewQuiz(category)
    }

    fun startNewQuiz(category: CategoryName) {
        viewModelScope.launch {
            _quizUiState.update { it.copy(isLoading = true, error = null) }
            try {
                val items = getCategoryFlow(category).first()
                val newQuestions = generateQuestions(items)
                _quizUiState.update {
                    it.copy(
                        questions = newQuestions,
                        currentIndex = 0,
                        score = 0,
                        selectedOptionText = null,
                        isAnswerChecked = false,
                        isFinished = false,
                        earnedPoints = 0,
                        timeLeft = maxTime,
                        isLoading = false
                    )
                }
                startTimer()
            } catch (e: Exception) {
                _quizUiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun generateQuestions(items: List<CategoryType>): List<Question> {
        return items.shuffled().take(10).map { item ->
            val options = (items.filter { it.en != item.en }.shuffled().take(2)
                .map { it.en } + item.en).shuffled()
            Question(
                questionText = item.fr,
                options = options,
                correctAnswer = item.en
            )
        }
    }

    fun selectOption(text: String) {
        _quizUiState.update { it.copy(selectedOptionText = text) }
    }

    fun confirmOrNext() {
        val state = _quizUiState.value
        if (!state.isAnswerChecked) checkAnswer()
        else goToNextQuestion()
    }

    private fun checkAnswer() {
        val state = _quizUiState.value
        val category = state.category ?: return
        val isCorrect = state.selectedOptionText == state.questions[state.currentIndex].correctAnswer

        val updatedPoints = if (isCorrect) {
            state.pointsByCategory + (category to (state.pointsByCategory[category]!! + 1))
        } else state.pointsByCategory

        _quizUiState.update {
            it.copy(
                isAnswerChecked = true,
                score = if (isCorrect) it.score + 1 else it.score,
                pointsByCategory = updatedPoints
            )
        }

        viewModelScope.launch { dataStore.setPoints(updatedPoints) }
        playEffect(isCorrect)
        stopTimer()
    }

    private fun goToNextQuestion() {
        val state = _quizUiState.value
        if (state.currentIndex < state.questions.lastIndex) {
            _quizUiState.update {
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    selectedOptionText = null,
                    isAnswerChecked = false,
                    timeLeft = maxTime
                )
            }
            startTimer()
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        stopTimer()
        val state = _quizUiState.value
        val category = state.category ?: return

        val correctAnswers = state.score
        val totalQuestions = state.questions.size
        val percent = if (totalQuestions > 0) (correctAnswers * 100 / totalQuestions) else 0

        val earnedPoints = when {
            percent == 100 -> 5
            percent >= 70 -> 2
            percent >= 50 -> 1
            else -> 0
        }

        _quizUiState.update {
            it.copy(
                isFinished = true,
                earnedPoints = earnedPoints
            )
        }

        viewModelScope.launch {
            val currentPoints = dataStore.points.first()
            val updatedPoints = currentPoints.toMutableMap()
            updatedPoints[category] = (updatedPoints[category] ?: 0) + earnedPoints
            dataStore.setPoints(updatedPoints)
        }
    }

    private fun getCategoryFlow(category: CategoryName) = when (category) {
        CategoryName.Verbs -> repository.getAllVerbs()
        CategoryName.Sentences -> repository.getAllSentences()
        CategoryName.PhrasalVerbs -> repository.getAllPhrasalVerbs()
        CategoryName.Nouns -> repository.getAllNouns()
        CategoryName.Adjectives -> repository.getAllAdjectives()
        CategoryName.Adverbs -> repository.getAllAdverbs()
        CategoryName.Idioms -> repository.getAllIdioms()
    }

    // ---------- Timer ----------
    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_quizUiState.value.timeLeft > 0) {
                delay(1000)
                _quizUiState.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
            onTimeUp()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    private fun onTimeUp() = confirmOrNext()

    // ---------- Effects ----------
    fun playEffect(isCorrect: Boolean) = viewModelScope.launch {
        _events.emit(if (isCorrect) EffectsEvent.CorrectAnswer else EffectsEvent.WrongAnswer)
    }

    sealed class EffectsEvent {
        object CorrectAnswer : EffectsEvent()
        object WrongAnswer : EffectsEvent()
    }

    // ---------- Reset / Retry ----------
    fun resetAllPoints() {
        _quizUiState.update { it.copy(pointsByCategory = zeroPoints) }
        viewModelScope.launch { dataStore.setPoints(zeroPoints) }
    }

    fun retryQuiz() {
        val state = _quizUiState.value
        _quizUiState.update {
            it.copy(
                currentIndex = 0,
                score = 0,
                selectedOptionText = null,
                isAnswerChecked = false,
                isFinished = false,
                earnedPoints = 0,
                timeLeft = maxTime
            )
        }
        startTimer()
        }
}*/

package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.QuizFiles.Question
import futur.apps.composeproject1.RoomDatabase.QuizRepository
import futur.apps.composeproject1.dataStore.UserPreferencesManager
import futur.apps.composeproject1.utils.Category
import futur.apps.composeproject1.utils.QuizItem

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state that represents the quiz screen.
 *
 * It holds all necessary information to render the quiz,
 * including current question, score, timer, points by category,
 * and whether the quiz is finished or not.
 */
data class QuizUiState(
    val isLoading: Boolean = true,
    val category: Category? = null,
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val score: Int = 0,
    val earnedPoints: Int = 0,
    val pointsByCategory: Map<Category, Int> = emptyMap(),
    val selectedOptionText: String? = null,
    val isAnswerChecked: Boolean = false,
    val timeLeft: Int = 15,
    val timeLimit: Int = 10,
    val error: String? = null,
    val isFinished: Boolean = false
)

/**
 * QuizViewModel is responsible for managing quiz logic and state.
 *
 * Responsibilities:
 * - Load quiz questions from repository
 * - Manage user answers and scoring
 * - Handle timer countdown
 * - Persist points using DataStore
 * - Expose immutable StateFlow for the UI
 */
@HiltViewModel
class QuizViewModel @Inject constructor(
    private val preferencesManager: UserPreferencesManager,
    private val repository: QuizRepository,
) : ViewModel() {

    // ---------- UI State ----------
    private val emptyPoints: Map<Category, Int> = Category.entries.associateWith { 0 }

    private val _quizUiState =
        MutableStateFlow(QuizUiState(isLoading = false, pointsByCategory = emptyPoints))
    val quizUiState: StateFlow<QuizUiState> = _quizUiState.asStateFlow()

    // ---------- Events (for one-time effects like animations/sounds) ----------
    private val _events = MutableSharedFlow<QuizEffect>()
    val events = _events.asSharedFlow()

    // ---------- Timer ----------
    private var timerJob: Job? = null
    private val maxTime = 15

    // ---------- User Preferences ----------
    val username: StateFlow<String> = preferencesManager.username.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        "User"
    )

    init {
        // Load saved points when ViewModel is created
        viewModelScope.launch {
            preferencesManager.categoryPoints.collect { savedPoints ->
                _quizUiState.update { it.copy(pointsByCategory = emptyPoints + savedPoints) }
            }
        }
    }

    // ---------- Quiz Logic ----------

    /** Sets the selected category and starts a new quiz. */
    fun setCategory(category: Category?) {
        if (category == null) {
            _quizUiState.update { it.copy(error = "Invalid category") }
            return
        }
        _quizUiState.update { it.copy(category = category) }
        startNewQuiz(category)
    }

    /** Starts a new quiz for the given category. */
    fun startNewQuiz(category: Category) {
        viewModelScope.launch {
            _quizUiState.update { it.copy(isLoading = true, error = null) }
            try {
                val items = getCategoryFlow(category).first()
                val newQuestions = generateQuestions(items)
                _quizUiState.update {
                    it.copy(
                        questions = newQuestions,
                        currentIndex = 0,
                        score = 0,
                        selectedOptionText = null,
                        isAnswerChecked = false,
                        isFinished = false,
                        earnedPoints = 0,
                        timeLeft = maxTime,
                        isLoading = false
                    )
                }
                startTimer()
            } catch (e: Exception) {
                _quizUiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    /** Generates a randomized list of multiple-choice questions. */
    private fun generateQuestions(items: List<QuizItem>): List<Question> {
        return items.shuffled().take(10).map { item ->
            val options = (items.filter { it.en != item.en }.shuffled().take(2)
                .map { it.en } + item.en).shuffled()
            Question(
                questionText = item.fr,
                options = options,
                correctAnswer = item.en
            )
        }
    }

    /** Called when the user selects an option. */
    fun selectOption(optionText: String) {
        _quizUiState.update { it.copy(selectedOptionText = optionText) }
    }

    /** Confirms the selected answer or moves to the next question. */
    fun confirmOrNext() {
        val state = _quizUiState.value
        if (!state.isAnswerChecked) checkAnswer()
        else goToNextQuestion()
    }

    /** Checks whether the selected answer is correct and updates points. */
    private fun checkAnswer() {
        val state = _quizUiState.value
        val category = state.category ?: return
        val isCorrect = state.selectedOptionText == state.questions[state.currentIndex].correctAnswer

        val updatedPoints = if (isCorrect) {
            state.pointsByCategory + (category to (state.pointsByCategory[category]!! + 1))
        } else state.pointsByCategory

        _quizUiState.update {
            it.copy(
                isAnswerChecked = true,
                score = if (isCorrect) it.score + 1 else it.score,
                pointsByCategory = updatedPoints
            )
        }

        viewModelScope.launch { preferencesManager.saveCategoryPoints(updatedPoints) }
        playEffect(isCorrect)
        stopTimer()
    }

    /** Moves to the next question or finishes the quiz if last question is reached. */
    private fun goToNextQuestion() {
        val state = _quizUiState.value
        if (state.currentIndex < state.questions.lastIndex) {
            _quizUiState.update {
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    selectedOptionText = null,
                    isAnswerChecked = false,
                    timeLeft = maxTime
                )
            }
            startTimer()
        } else {
            finishQuiz()
        }
    }

    /** Marks the quiz as finished and calculates earned points. */
    private fun finishQuiz() {
        stopTimer()
        val state = _quizUiState.value
        val category = state.category ?: return

        val correctAnswers = state.score
        val totalQuestions = state.questions.size
        val percent = if (totalQuestions > 0) (correctAnswers * 100 / totalQuestions) else 0

        val earnedPoints = when {
            percent == 100 -> 5
            percent >= 70 -> 2
            percent >= 50 -> 1
            else -> 0
        }

        _quizUiState.update {
            it.copy(
                isFinished = true,
                earnedPoints = earnedPoints
            )
        }

        viewModelScope.launch {
            val currentPoints = preferencesManager.categoryPoints.first()
            val updatedPoints = currentPoints.toMutableMap()
            updatedPoints[category] = (updatedPoints[category] ?: 0) + earnedPoints
            preferencesManager.saveCategoryPoints(updatedPoints)
        }
    }

    /** Returns the corresponding Flow for the given category from repository. */
    private fun getCategoryFlow(category: Category) = when (category) {
        Category.Verbs -> repository.getAllVerbs()
        Category.Sentences -> repository.getAllSentences()
        Category.PhrasalVerbs -> repository.getAllPhrasalVerbs()
        Category.Nouns -> repository.getAllNouns()
        Category.Adjectives -> repository.getAllAdjectives()
        Category.Adverbs -> repository.getAllAdverbs()
        Category.Idioms -> repository.getAllIdioms()
    }

    // ---------- Timer ----------

    /** Starts the countdown timer for the current question. */
    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_quizUiState.value.timeLeft > 0) {
                delay(1000)
                _quizUiState.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
            onTimeUp()
        }
    }

    /** Stops the countdown timer. */
    fun stopTimer() {
        timerJob?.cancel()
    }

    /** Called when the timer runs out. */
    private fun onTimeUp() = confirmOrNext()

    // ---------- Effects ----------

    /** Emits one-time effects (e.g., sound or animation for correct/wrong answers). */
    fun playEffect(isCorrect: Boolean) = viewModelScope.launch {
        _events.emit(if (isCorrect) QuizEffect.CorrectAnswer else QuizEffect.WrongAnswer)
    }

    sealed class QuizEffect {
        object CorrectAnswer : QuizEffect()
        object WrongAnswer : QuizEffect()
    }

    // ---------- Reset / Retry ----------

    /** Resets all stored points to zero. */
    fun resetAllPoints() {
        _quizUiState.update { it.copy(pointsByCategory = emptyPoints) }
        viewModelScope.launch { preferencesManager.saveCategoryPoints(emptyPoints) }
    }

    /** Restarts the quiz with the same category. */
    fun retryQuiz() {
        val state = _quizUiState.value
        _quizUiState.update {
            it.copy(
                currentIndex = 0,
                score = 0,
                selectedOptionText = null,
                isAnswerChecked = false,
                isFinished = false,
                earnedPoints = 0,
                timeLeft = maxTime
            )
        }
        startTimer()
        }
}