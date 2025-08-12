package futur.apps.composeproject1.QuizFiles

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.RoomDatabase.DataRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    repository : DataRepository
) : ViewModel() {

    val questions : StateFlow<List<Question>> = repository.getAllVerbs()
        .map { verbs -> verbs.map { verb ->
            val otherOptions = verbs.filter { it.en != verb.en }
                .shuffled()
                .take(2)
                .map{it.en}
            val options = (otherOptions + verb.en).shuffled()
            Question(
                questionText = verb.fr,
                options = options,
                correctAnswer = verb.en
            )
        }.shuffled()

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList()
        )


    private val _currentIndex = mutableIntStateOf(0)
    val currentIndex: State<Int> = _currentIndex

    private val _score = mutableIntStateOf(0)
    val score: State<Int> = _score

    private val _selectedOption = mutableStateOf<Int?>(null)
    val selectedOption: State<Int?> = _selectedOption

    private val _selectedOptionText = mutableStateOf<String?>(null)
    val selectedOptionText: State<String?> = _selectedOptionText

    private val _timeLeft = mutableIntStateOf(15) // 15 secondes for each quiz
    val timeLeft: State<Int> = _timeLeft

    private var timerJob: Job? = null

    init {
        // dummy data
      //  _questions.value =
        startTimer()
    }


    fun startTimer() {
        timerJob?.cancel()
        _timeLeft.intValue = 10
        timerJob = viewModelScope.launch {
            while (_timeLeft.intValue > 0) {
                delay(1000)
                _timeLeft.intValue -= 1
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

    fun confirmAnswer() {
        val question = questions.value[_currentIndex.intValue]
        val chosen = _selectedOption.value
        val chosenText = when (chosen) {
            0 -> question.options[0]
            1 -> question.options[1]
            2 -> question.options[2]
            else -> null
        }
        if (chosenText == question.correctAnswer) {
            _score.intValue += 1
        }
        timerJob?.cancel() // stop timer when button confirm was clicked
        nextQuestion()
    }

    private fun nextQuestion() {
        if (_currentIndex.intValue < questions.value.size - 1) {
            _currentIndex.intValue++
            _selectedOption.value = null
            startTimer()
        } else {
            // Test finished
            timerJob?.cancel()
        }
    }

    fun selectOptionText(optionText: String) {
        _selectedOptionText.value = optionText
    }
}