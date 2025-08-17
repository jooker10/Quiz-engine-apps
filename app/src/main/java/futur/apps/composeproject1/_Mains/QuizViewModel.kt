package futur.apps.composeproject1._Mains

import androidx.compose.runtime.State
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map
import kotlin.collections.plus
import kotlin.collections.shuffled

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val dataStore: DataStoreManager,
    private val repository: DataRepository
) : ViewModel() {

    private val _category = MutableStateFlow<CategoryName?>(null)
    val category: StateFlow<CategoryName?> = _category.asStateFlow()
    var showNavigationBar by mutableStateOf(true)
        private set
    var showFab by mutableStateOf(true)
        private set

    private val _events = MutableSharedFlow<EffectsEvent>()
    val events = _events

    private val _questions = mutableStateOf<List<Question>>(emptyList())
    val questions: State<List<Question>> = _questions

    private val _isAnswerChecked = mutableStateOf(false)
    val isAnswerChecked: State<Boolean> = _isAnswerChecked

    private val _currentIndex = mutableStateOf(0)
    val currentIndex: State<Int> = _currentIndex

    private val _score = mutableStateOf(0)
    val score: State<Int> = _score

    private val _selectedOption = mutableStateOf<Int?>(null)
    val selectedOption: State<Int?> = _selectedOption

    private val _selectedOptionText = mutableStateOf<String?>(null)
    val selectedOptionText: State<String?> = _selectedOptionText
    val maxTime = 15
    private val _timeLeft = mutableStateOf(maxTime) // 15 secondes for each quiz
    val timeLeft: State<Int> = _timeLeft

    private var timerJob: Job? = null

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

    init {
        startNewQuiz(categoryName = _category.value ?: CategoryName.Verbs)
    }


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
        _timeLeft.value = maxTime
        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1000)
                _timeLeft.value -= 1
            }
            onTimeUp()
        }
    }

    fun stopTimer() {
        timerJob?.cancel()
    }

    private fun onTimeUp() {
        nextQuestion()
    }

    fun selectOption(index: Int) {
        _selectedOption.value = index
    }
    fun selectOptionText(optionText: String) {
        _selectedOptionText.value = optionText
    }

    fun setCategory(categoryName: CategoryName?) {
        _category.value = categoryName
        startNewQuiz(categoryName)
    }
    fun startNewQuiz(categoryName: CategoryName?) {
        viewModelScope.launch {
            val items = getCategoryFlow(category = categoryName).first()
           // val allVerbs = repository.getAllVerbs().first()
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
            _questions.value = newQuestions
            _currentIndex.value = 0
            _score.value = 0
            _selectedOption.value = null
            _selectedOptionText.value = null
            _isAnswerChecked.value = false
            startTimer()
        }
    }
    fun confirmOrNext() {
        if (!_isAnswerChecked.value) {
            val question = questions.value[_currentIndex.value]
            val chosenText = _selectedOptionText.value
            val isCorrect = chosenText == question.correctAnswer

            if (isCorrect) {
                _score.value += 1
            }
            playEffect(isCorrect = isCorrect)

            _isAnswerChecked.value = true
            stopTimer()
        } else {
            if (_currentIndex.value < questions.value.size - 1) {
                _currentIndex.value++
                _selectedOption.value = null
                _selectedOptionText.value = null
                _isAnswerChecked.value = false
                startTimer()
            } else {
                timerJob?.cancel()
                // quiz finished
            }
        }
    }

    private fun nextQuestion() {
        if (_currentIndex.value < questions.value.size - 1) {
            _currentIndex.value++
            _selectedOption.value = null
            startTimer()
        } else {
            // Test finished
            timerJob?.cancel()
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

    private fun getCategoryFlow(category: CategoryName?) = when(category) {
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

