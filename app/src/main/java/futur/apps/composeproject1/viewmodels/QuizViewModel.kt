package futur.apps.composeproject1.viewmodels

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.RoomDatabase.QuizRepository
import futur.apps.composeproject1.dataStore.AppDataStore
import futur.apps.composeproject1.utils.Category
import futur.apps.composeproject1.utils.Table
import futur.apps.composeproject1.QuizFiles.Question
import futur.apps.composeproject1.ads.AdsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

// -------------------- Quiz UI State --------------------
data class QuizUiState(
    val isLoading: Boolean = true,
    val category: Category? = null,
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val correctScore: Int = 0,
    val wrongScore: Int = 0,
    val earnedPoints: Int = 0,
    val pointsByCategory: Map<Category, Int> = emptyMap(),
    val selectedOptionText: String? = null,
    val isAnswerChecked: Boolean = false,
    val maxTime: Int = 15,
    val timeLeft: Int = 15,
    val error: String? = null,
    val isFinished: Boolean = false,
)

// -------------------- Quiz ViewModel --------------------
@HiltViewModel
class QuizViewModel @Inject constructor(
    private val preferencesManager: AppDataStore,
    private val repository: QuizRepository,
    private val adsManager: AdsManager,
) : ViewModel() {

    private val emptyPoints: Map<Category, Int> = Category.entries.associateWith { 0 }

    private val _quizUiState =
        MutableStateFlow(QuizUiState(isLoading = false, pointsByCategory = emptyPoints))
    val quizUiState: StateFlow<QuizUiState> = _quizUiState.asStateFlow()

    private val _events = MutableSharedFlow<QuizEffect>()
    val events = _events.asSharedFlow()

    private val _confirmOrNextEvent = MutableSharedFlow<Unit>()
    val confirmOrNextEvent = _confirmOrNextEvent.asSharedFlow()

    private var timerJob: Job? = null
    private val maxTime = 15

    init {
        // Load saved points from DataStore
        viewModelScope.launch {
            preferencesManager.categoryPoints.collect { savedPoints ->
                _quizUiState.update { it.copy(pointsByCategory = emptyPoints + savedPoints) }
            }
        }
    }

    // -------------------- Quiz Logic --------------------
    fun setCategory(activity: Activity, category: Category?) {
        if (category == null) {
            _quizUiState.update { it.copy(error = "Invalid category") }
            return
        }
        _quizUiState.update { it.copy(category = category) }
        startNewQuiz(activity = activity, category = category)
    }

    fun startNewQuiz(activity: Activity, category: Category) {

        viewModelScope.launch {
            _quizUiState.update { it.copy(isLoading = true, error = null) }
            try {
                val items = getCategoryFlow(category).first()
                val newQuestions = generateQuestions(items)
                _quizUiState.update {
                    it.copy(
                        questions = newQuestions,
                        currentIndex = 0,
                        correctScore = 0,
                        wrongScore = 0,
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

    private fun generateQuestions(items: List<Table>): List<Question> {
        return items.shuffled().take(10).map { item ->
            val options = (items.filter { it.en != item.en }.shuffled().take(2)
                .map { it.en } + item.en).shuffled()
            Question(questionText = item.fr, options = options, correctAnswer = item.en)
        }
    }

    fun selectOption(optionText: String) {
        _quizUiState.update { it.copy(selectedOptionText = optionText) }
    }

    fun confirmOrNext(activity: Activity) {
        val state = _quizUiState.value
        if (!state.isAnswerChecked) checkAnswer(activity)
        else goToNextQuestion(activity)
    }

    fun confirmOrNextEventTrigger() {
        viewModelScope.launch {
            _confirmOrNextEvent.emit(Unit)
        }
    }

    private fun checkAnswer(activity: Activity) {
        val state = _quizUiState.value
        val category = state.category ?: return
        val isCorrect =
            state.selectedOptionText == state.questions[state.currentIndex].correctAnswer

        val updatedPoints =
            if (isCorrect) state.pointsByCategory + (category to (state.pointsByCategory[category]!! + 1)) else state.pointsByCategory

        _quizUiState.update {
            it.copy(
                isAnswerChecked = true,
                correctScore = if (isCorrect) {
                    it.correctScore + 1
                } else {
                    it.correctScore
                },
                wrongScore = if (!isCorrect) {
                    it.wrongScore + 1
                } else {
                    it.wrongScore
                },
                pointsByCategory = updatedPoints
            )
        }

        viewModelScope.launch { preferencesManager.saveCategoryPoints(updatedPoints) }
        playEffect(isCorrect)
        stopTimer()
    }

    private fun goToNextQuestion(activity: Activity) {
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
            finishQuiz(activity)
        }
    }

    private fun finishQuiz(activity: Activity) {

        stopTimer()

        val state = _quizUiState.value
        val category = state.category ?: return

        val correctAnswers = state.correctScore
        val totalQuestions = state.questions.size
        val percent = if (totalQuestions > 0) (correctAnswers * 100 / totalQuestions) else 0

        val earnedPoints = when {
            percent == 100 -> 5
            percent >= 70 -> 2
            percent >= 50 -> 1
            else -> 0
        }

        _quizUiState.update {
            it.copy(isFinished = true, earnedPoints = earnedPoints)
        }

        viewModelScope.launch {
            val currentPoints = preferencesManager.categoryPoints.first()
            val updatedPoints = currentPoints.toMutableMap()
            updatedPoints[category] = (updatedPoints[category] ?: 0) + earnedPoints
            preferencesManager.saveCategoryPoints(updatedPoints)
        }

        val random: Boolean = Random.nextBoolean()
        if (random) {
            // Show interstitial ad when quiz finishes
            adsManager.showInterstitial(activity = activity)
        } else {
            // Show rewarded ad when quiz finishes
            adsManager.showRewardedAd(activity, {
                Toast.makeText(activity, "ad rewarded!", Toast.LENGTH_SHORT).show()
            })
        }

// Initialize ads if not already done
        adsManager.initializeAds(activity)
    }

    fun watchRewardedVideo(activity: Activity, onReward: () -> Unit) {
        adsManager.showRewardedAd(activity) { reward ->
            val state = _quizUiState.value
            val category = state.category ?: return@showRewardedAd
            val updatedPoints =
                state.pointsByCategory + (category to ((state.pointsByCategory[category]
                    ?: 0) + reward.amount))
            _quizUiState.update { it.copy(pointsByCategory = updatedPoints) }
            viewModelScope.launch { preferencesManager.saveCategoryPoints(updatedPoints) }
            onReward()
        }
    }

    // -------------------- Timer --------------------
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

    private fun onTimeUp() {
        confirmOrNextEventTrigger()
    }

    // -------------------- Effects --------------------
    fun playEffect(isCorrect: Boolean) = viewModelScope.launch {
        _events.emit(if (isCorrect) QuizEffect.CorrectAnswer else QuizEffect.WrongAnswer)
    }

    sealed class QuizEffect {
        object CorrectAnswer : QuizEffect()
        object WrongAnswer : QuizEffect()
    }

    // -------------------- Reset / Retry --------------------
    fun resetAllPoints() {
        _quizUiState.update { it.copy(pointsByCategory = emptyPoints) }
        viewModelScope.launch { preferencesManager.saveCategoryPoints(emptyPoints) }
    }

    fun retryQuiz() {
        val state = _quizUiState.value
        _quizUiState.update {
            it.copy(
                currentIndex = 0,
                correctScore = 0,
                wrongScore = 0,
                selectedOptionText = null,
                isAnswerChecked = false,
                isFinished = false,
                earnedPoints = 0,
                timeLeft = maxTime
            )
        }
        startTimer()
    }

    // -------------------- Repository Helpers --------------------
    private fun getCategoryFlow(category: Category) = when (category) {
        Category.Verbs -> repository.getAllVerbs()
        Category.Sentences -> repository.getAllSentences()
        Category.PhrasalVerbs -> repository.getAllPhrasalVerbs()
        Category.Nouns -> repository.getAllNouns()
        Category.Adjectives -> repository.getAllAdjectives()
        Category.Adverbs -> repository.getAllAdverbs()
        Category.Idioms -> repository.getAllIdioms()
    }
}
