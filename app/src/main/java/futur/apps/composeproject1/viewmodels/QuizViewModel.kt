package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.DataStore.DataStoreManager
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
    private val zeroScores: Map<CategoryName, Int> = CategoryName.entries.associateWith { 0 }

    private val _quizUiState =
        MutableStateFlow(QuizUiState(isLoading = false, pointsByCategory = zeroScores))
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
        // Load saved scores
        viewModelScope.launch {
            dataStore.setPointsList.collect { savedScores ->
                _quizUiState.update { it.copy(pointsByCategory = zeroScores + savedScores) }
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

        val updatedScores = if (isCorrect) {
            state.pointsByCategory + (category to (state.pointsByCategory[category]!! + 1))
        } else state.pointsByCategory

        _quizUiState.update {
            it.copy(
                isAnswerChecked = true,
                score = if (isCorrect) it.score + 1 else it.score,
                pointsByCategory = updatedScores
            )
        }

        viewModelScope.launch { dataStore.setPointSList(updatedScores) }
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

        // حساب النقاط المكتسبة
        val correctAnswers = state.score
        val totalQuestions = state.questions.size
        val percent = if (totalQuestions > 0) (correctAnswers * 100 / totalQuestions) else 0

        val earnedPoints = when {
            percent == 100 -> 5
            percent >= 70 -> 2
            percent >= 50 -> 1
            else -> 0
        }

        // تحديث الـ UI State لعرض ResultSheet
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
           // dataStore.setPointsList(updatedPoints)
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
    fun resetAllScores() {
        _quizUiState.update { it.copy(pointsByCategory = zeroScores) }
        viewModelScope.launch { dataStore.setPointSList(zeroScores) }
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
}
