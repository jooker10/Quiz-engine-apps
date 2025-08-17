package futur.apps.composeproject1._Mains

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.DataStore.DataStoreManager
import futur.apps.composeproject1.QuizFiles.Question
import futur.apps.composeproject1.RoomDatabase.DataRepository
import futur.apps.composeproject1.utils.CategoryName
import futur.apps.composeproject1.utils.QuizUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val dataStore: DataStoreManager,
    private val repository: DataRepository
) : ViewModel() {

    private val _quizUiState = MutableStateFlow(QuizUiState())
    val quizUiState: StateFlow<QuizUiState> = _quizUiState.asStateFlow()
    var showNavigationBar by mutableStateOf(true)
        private set

    var showFab by mutableStateOf(true)
        private set
    private val _events = MutableSharedFlow<EffectsEvent>()

    val events = _events

    private var timerJob: Job? = null
    val maxTime = 15


    // dataStore items
    val isDarkMode: StateFlow<Boolean> = dataStore.isDarkTheme.stateIn(
        viewModelScope,
        SharingStarted.Companion.Eagerly, false
    )
    val langue: StateFlow<String> = dataStore.langue.stateIn(
        viewModelScope,
        SharingStarted.Companion.Eagerly, "English"
    )
    val username: StateFlow<String> = dataStore.username.stateIn(
        viewModelScope,
        SharingStarted.Companion.Eagerly, "User"
    )

    fun changeTheme(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.setDarkTheme(enabled)
        }
    }

    fun changeLanguage(lang: String) {
        viewModelScope.launch {
            dataStore.setLanguage(lang)
        }
    }

    fun changeUserName(username: String) {
        viewModelScope.launch {
            dataStore.setLanguage(username)
        }
    }

    fun setNavigationBarVisibility(visible: Boolean) {
        showNavigationBar = visible
    }

    fun setFabVisibility(visible: Boolean) {
        showFab = visible
    }


    fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_quizUiState.value.timeLeft > 0) {
                delay(1000)
                _quizUiState.update { it.copy(timeLeft = it.timeLeft - 1) }                //_timeLeft.value -= 1
            }
            onTimeUp()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    private fun onTimeUp() {
        confirmOrNext()
    }

    fun selectOption(index: Int,text : String) {
        _quizUiState.update { it.copy(selectedOption = index, selectedOptionText = text) }
    }



    fun setCategory(category: CategoryName?) {
        _quizUiState.update { it.copy(category = category) }
        startNewQuiz(category)

    }

    fun startNewQuiz(categoryName: CategoryName?) {
        viewModelScope.launch {
            try {
                _quizUiState.update { it.copy(isLoading = true, error = null) }

            val items = getCategoryFlow(category = categoryName).first()
            val newQuestions = items.map { item ->
                val otherOptions = items.filter { it.en != item.en }
                    .shuffled()
                    .take(2)
                    .map { it.en }
                val options = (otherOptions + item.en).shuffled()
                Question(
                    questionText = item.fr,
                    options = options,
                    correctAnswer = item.en
                )
            }.shuffled()
                .take(10)    // chose only 10 questions
            _quizUiState.update {
                it.copy(
                    isLoading = false,
                    questions = newQuestions,
                    currentIndex = 0,
                    score = 0,
                    selectedOption = null,
                    selectedOptionText = null,
                    isAnswerChecked = false,
                    timeLeft = maxTime,
                    isFinished = false
                )
            }
            startTimer()
        }
            catch (e : Exception) {
                _quizUiState.update { it.copy(isLoading = false, error = e.message) }
            }
    }
    }

    fun confirmOrNext() {
        val state = _quizUiState.value
        if (!state.isAnswerChecked) {
            val question = state.questions[state.currentIndex]
            val chosenText = state.selectedOptionText
            val isCorrect = chosenText == question.correctAnswer

            _quizUiState.update {
                it.copy(
                    isAnswerChecked = true,
                    score = if (isCorrect) it.score + 1 else it.score,
                )
            }


            playEffect(isCorrect = isCorrect)

            stopTimer()
        } else {
            if (state.currentIndex < state.questions.size-1) {
                _quizUiState.update {
                    it.copy(
                        currentIndex = it.currentIndex + 1,
                        selectedOption = null,
                        selectedOptionText = null,
                        isAnswerChecked = false,
                        timeLeft = maxTime
                    )
                }
                startTimer()
            } else {
                stopTimer()
                _quizUiState.update {
                    it.copy(isFinished = true)
                }
            }
        }
    }


    fun playEffect(isCorrect: Boolean) {
        viewModelScope.launch {
            if (isCorrect) {
                _events.emit(EffectsEvent.CorrectAnswer)
            } else {
                _events.emit(EffectsEvent.WrongAnswer)
            }
        }
    }

    private fun getCategoryFlow(category: CategoryName?) = when (category) {
        CategoryName.Verbs -> repository.getAllVerbs()
        CategoryName.Sentences -> repository.getAllSentences()
        CategoryName.PhrasalVerbs -> repository.getAllPhrasalVerbs()
        CategoryName.Nouns -> repository.getAllNouns()
        CategoryName.Adjectives -> repository.getAllAdjectives()
        CategoryName.Adverbs -> repository.getAllAdverbs()
        CategoryName.Idioms -> repository.getAllIdioms()
        else -> repository.getAllVerbs()
    }

    sealed class EffectsEvent {
        object CorrectAnswer : EffectsEvent()
        object WrongAnswer : EffectsEvent()
    }
}

