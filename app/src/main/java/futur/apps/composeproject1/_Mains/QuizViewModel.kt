package futur.apps.composeproject1._Mains

/*@HiltViewModel
class QuizViewModel @Inject constructor(
    private val dataStore: DataStoreManager,
    private val repository: DataRepository
) : ViewModel() {

    private val zeroScores: Map<CategoryName, Int> = CategoryName.entries.associateWith { 0 }
    private val _quizUiState =
        MutableStateFlow(QuizUiState(isLoading = false, scoresByCategory = zeroScores))
    val quizUiState: StateFlow<QuizUiState> = _quizUiState.asStateFlow()

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

    init {
        viewModelScope.launch {
            dataStore.scores.collect { savedScores ->
                _quizUiState.update { it.copy(scoresByCategory = zeroScores + savedScores) }
            }
        }
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
            dataStore.setUserName(username)
        }
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

    fun selectOption(text: String) {
        _quizUiState.update { it.copy(selectedOptionText = text) }
    }


    fun setCategory(category: CategoryName?) {
        if (category == null) {
            _quizUiState.update { it.copy(error = "Invalid category") }
            return
        }
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
                        selectedOptionText = null,
                        isAnswerChecked = false,
                        timeLeft = maxTime,
                        isFinished = false
                    )
                }
                startTimer()
            } catch (e: Exception) {
                _quizUiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun confirmOrNext() {
        val state = _quizUiState.value
        val category = state.category ?: return
        if (!state.isAnswerChecked) {
            val question = state.questions[state.currentIndex]
            val chosenText = state.selectedOptionText
            val isCorrect = chosenText == question.correctAnswer
            val updatedScores = if(isCorrect) {
                state.scoresByCategory + (state.category to (state.scoresByCategory[state.category]!! + 1))
            } else {
                state.scoresByCategory
            }

            _quizUiState.update {
                it.copy(
                    isAnswerChecked = true,
                    score = if (isCorrect) it.score + 1 else it.score,
                    // scoresByCategory = it.scoresByCategory.increment(category, if (isCorrect) 1 else 0)
                    scoresByCategory = updatedScores
                )
            }

viewModelScope.launch {
    dataStore.setScores(updatedScores)
}

            playEffect(isCorrect = isCorrect)

            stopTimer()
        } else {
            goToNextQuestion()
        }
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
            stopTimer()
            _quizUiState.update {
                it.copy(isFinished = true)
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


    fun resetAllScores() {
        _quizUiState.update { it.copy(scoresByCategory = zeroScores) }
        viewModelScope.launch {
            dataStore.setScores(zeroScores)
        }
    }

    private fun Map<CategoryName, Int>.increment(key: CategoryName, by: Int = 1):
            Map<CategoryName, Int> {
        val current = this[key] ?: 0
        return this + (key to current + by)
    }

    sealed class EffectsEvent {
        object CorrectAnswer : EffectsEvent()
        object WrongAnswer : EffectsEvent()
    }
}*/

