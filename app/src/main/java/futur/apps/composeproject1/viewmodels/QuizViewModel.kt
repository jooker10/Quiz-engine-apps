package futur.apps.composeproject1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.DataStore.DataStoreManager
import futur.apps.composeproject1.QuizFiles.Question
import futur.apps.composeproject1.RoomDatabase.DataRepository
import futur.apps.composeproject1.utils.CategoryName
import futur.apps.composeproject1.utils.CategoryType
import futur.apps.composeproject1.utils.QuizUiState
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

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val dataStore: DataStoreManager,
    private val repository: DataRepository,
) : ViewModel() {

    // ---------- UI State ----------
    private val zeroScores: Map<CategoryName, Int> = CategoryName.entries.associateWith { 0 }

    private val _quizUiState =
        MutableStateFlow(QuizUiState(isLoading = false, scoresByCategory = zeroScores))
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
        SharingStarted.Companion.Eagerly,
        "User"
    )

    init {
        // Load saved scores
        viewModelScope.launch {
            dataStore.scores.collect { savedScores ->
                _quizUiState.update { it.copy(scoresByCategory = zeroScores + savedScores) }
            }
        }
    }
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
        val isCorrect =
            state.selectedOptionText == state.questions[state.currentIndex].correctAnswer

        // Update score
        val updatedScores = if (isCorrect) {
            state.scoresByCategory + (category to (state.scoresByCategory[category]!! + 1))
        } else state.scoresByCategory

        _quizUiState.update {
            it.copy(
                isAnswerChecked = true,
                score = if (isCorrect) it.score + 1 else it.score,
                scoresByCategory = updatedScores
            )
        }

        viewModelScope.launch { dataStore.setScores(updatedScores) }

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
            _quizUiState.update { it.copy(isFinished = true) }
            stopTimer()
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

    // ---------- Reset ----------
    fun resetAllScores() {
        _quizUiState.update { it.copy(scoresByCategory = zeroScores) }
        viewModelScope.launch { dataStore.setScores(zeroScores) }
    }
}