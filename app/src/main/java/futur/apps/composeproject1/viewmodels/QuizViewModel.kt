package futur.apps.composeproject1.viewmodels

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.R
import futur.apps.composeproject1.RoomDatabase.QuizRepository
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuestionEntity
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuizRepository
import futur.apps.composeproject1.RoomDatabase.userroom.UserCategoryEntity
import futur.apps.composeproject1.ads.AdsManager
import futur.apps.composeproject1.dataStore.AppDataStore
import futur.apps.composeproject1.quizsystem.core.QuizConfig
import futur.apps.composeproject1.quizsystem.core.QuizEngine
import futur.apps.composeproject1.quizsystem.ui.components.ReviewAnswer
import futur.apps.composeproject1.utils.BuildInCategory
import futur.apps.composeproject1.utils.DataEntity
import futur.apps.composeproject1.utils.Question
import futur.apps.composeproject1.utils.QuizMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject
import kotlin.random.Random

// Used to unify both BuiltIn and UserCreated categories in one type
sealed class QuizCategory : Serializable {
    data class BuiltIn(val category: BuildInCategory) : QuizCategory()
    data class UserCreated(val name: String) : QuizCategory()
}

// -------------------- UI Effects & Events --------------------
sealed class QuizUiEffect {
    object PlayCorrectSound : QuizUiEffect()
    object PlayWrongSound : QuizUiEffect()
    object PlayTimerTick : QuizUiEffect()
    object PlayTimerUrgent : QuizUiEffect()
    object StopTimerSounds : QuizUiEffect()
    data class SpeakTextRes(val resId: Int) : QuizUiEffect()
    object None : QuizUiEffect()
}

sealed class QuizEvent {
    data class SelectOption(val optionText: String) : QuizEvent()
    object ConfirmOrNext : QuizEvent()
    object Retry : QuizEvent()
    object TimeUp : QuizEvent()
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val preferencesManager: AppDataStore,
    private val repository: QuizRepository,
    private val userRepository: UserQuizRepository,
    private val adsManager: AdsManager,
) : ViewModel() {

    // -------------------- Public state --------------------
    private val _quizUiState = MutableStateFlow(QuizUiState())
    val quizUiState: StateFlow<QuizUiState> = _quizUiState.asStateFlow()

    private val _settingsUiState = MutableStateFlow(SettingsUiState())
    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiState.asStateFlow()

    private val _statsUiState = MutableStateFlow(StatsUiState())
    val statsUiState: StateFlow<StatsUiState> = _statsUiState.asStateFlow()

    private val _quizUiEffect = MutableSharedFlow<QuizUiEffect>()
    val quizUiEffect: SharedFlow<QuizUiEffect> = _quizUiEffect.asSharedFlow()

    private val _events = MutableSharedFlow<Any>()
    val events = _events.asSharedFlow()

    private val _confirmOrNextEvent = MutableSharedFlow<Unit>()
    val confirmOrNextEvent = _confirmOrNextEvent.asSharedFlow()

    private val _currentQuizMode = MutableStateFlow(QuizMode.BUILT_IN)
    val currentQuizMode: StateFlow<QuizMode> = _currentQuizMode.asStateFlow()

    // Keep the selected category (unified) in the ViewModel
    private var _currentCategory: QuizCategory? = null

    val maxTime = QuizConfig.QUESTION_TIME_LIMIT

    // -------------------- Internal engine/timer --------------------
    private lateinit var quizEngine: QuizEngine

    private val timer = QuizTimer(
        scope = viewModelScope,
        tickInterval = QuizConfig.TIMER_TICK_INTERVAL,
        onTick = { newTime ->
            _quizUiState.update { it.copy(timeLeft = newTime) }
            if (_settingsUiState.value.soundEnabled) {
                if (newTime <= QuizConfig.TIMER_CRITICAL_THRESHOLD) emitEffect(QuizUiEffect.PlayTimerUrgent)
                else emitEffect(QuizUiEffect.PlayTimerTick)
            }
        },
        onFinish = { onEvent(QuizEvent.TimeUp) }
    )

    private var autoNextJob: Job? = null

    // -------------------- Init: collect DataStore & settings --------------------
    init {
        // category points (Map<BuildInCategory, Int>) goes into quizUiState.pointsByCategory
        viewModelScope.launch {
            preferencesManager.categoryPoints.collect { savedPoints ->
                _quizUiState.update { it.copy(pointsByCategory = savedPoints) }
            }
        }

        // persistent StatsUiState
        viewModelScope.launch {
            preferencesManager.statsFlow.collect { savedStats ->
                _statsUiState.value = savedStats.copy(isLoading = false)
            }
        }

        viewModelScope.launch { preferencesManager.isDarkThemeEnabled.collect { dark -> _settingsUiState.update { it.copy(isDarkMode = dark) } } }
        viewModelScope.launch { preferencesManager.selectedLanguage.collect { lang -> _settingsUiState.update { it.copy(language = lang) } } }
        viewModelScope.launch { preferencesManager.selectedPaletteName.collect { pal -> _settingsUiState.update { it.copy(selectedPalette = pal) } } }
        viewModelScope.launch { preferencesManager.autoNext.collect { auto -> _settingsUiState.update { it.copy(autoNext = auto) } } }
        viewModelScope.launch { preferencesManager.enableSounds.collect { s -> _settingsUiState.update { it.copy(soundEnabled = s) } } }
        viewModelScope.launch { preferencesManager.enableTTS.collect { t -> _settingsUiState.update { it.copy(ttsEnabled = t) } } }
        viewModelScope.launch { preferencesManager.maxQuestions.collect { m -> _settingsUiState.update { it.copy(maxQuestions = m) } } }

        viewModelScope.launch {
            preferencesManager.globalQuizMode.collectLatest { savedMode ->
                _currentQuizMode.value = savedMode
            }
        }
    }

    // -------------------- Public event entry --------------------
    fun onEvent(event: QuizEvent) {
        when (event) {
            is QuizEvent.SelectOption -> selectOption(event.optionText)
            QuizEvent.ConfirmOrNext -> confirmOrNext()
            QuizEvent.Retry -> {
                _currentCategory?.let { startNewQuiz(it) }
            }
            QuizEvent.TimeUp -> handleTimeUp()
        }
    }

    private fun selectOption(option: String) {
        if (::quizEngine.isInitialized) quizEngine.selectOption(option)
        _quizUiState.update { it.copy(selectedOptionText = option) }
    }

    // -------------------- Set / start new quiz --------------------
    /** Accepts either a built-in category or a user-created category */
    fun setCategory(activity: Activity?, category: QuizCategory?) {
        viewModelScope.launch {
            _currentCategory = category
            // store unified category in state (QuizUiState.category is QuizCategory?)
            _quizUiState.update { it.copy(category = category, isFinished = false, isLoading = true, error = null) }
            startNewQuiz(category)
        }
    }

    fun startNewQuiz(category: QuizCategory?) {
        stopTimer()
        viewModelScope.launch {
            // keep QuizUiState.category in sync (it is QuizCategory? in your QuizUiState)
            _quizUiState.value = _quizUiState.value.copy(isLoading = true, category = category, currentIndex = 0)

            try {
                // load items: both branches return List<*> (List<DataEntity> or List<UserQuestionEntity>)
                val items: List<Any> = when (category) {
                    is QuizCategory.BuiltIn -> {
                        // getCategoryFlow returns Flow<List<DataEntity>>
                        val builtInList = getCategoryFlow(category.category).first()
                        builtInList as List<Any>
                    }
                    is QuizCategory.UserCreated -> {
                        val userList = getUserQuestionsForCategoryName(category.name) // List<UserQuestionEntity>
                        userList as List<Any>
                    }
                    null -> emptyList()
                }


                var questions = generateQuestions(items)

                if (_quizUiState.value.shuffleQuestions) questions = questions.shuffled()
                if (_quizUiState.value.shuffleOptions) questions = questions.map { q -> q.copy(options = q.options.shuffled()) }

                questions = questions.take(_settingsUiState.value.maxQuestions)

                quizEngine = QuizEngine(
                    questions = questions,
                    enableNegativeScoring = _quizUiState.value.enableNegativeScoring,
                    enableSounds = _settingsUiState.value.soundEnabled
                )

                _quizUiState.value = _quizUiState.value.copy(
                    questions = questions,
                    timeLeft = maxTime,
                    currentIndex = 0,
                    selectedOptionText = null,
                    isAnswerChecked = false,
                    isFinished = false,
                    correctScore = 0,
                    wrongScore = 0,
                    earnedPoints = 0,
                    completionPercent = 0,
                    reviewAnswers = emptyList(),
                    isLoading = false
                )

                if (_quizUiState.value.showTimer) startTimer()
            } catch (e: Exception) {
                _quizUiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // helper: resolve user category name -> questions list using repository flows
    private suspend fun getUserQuestionsForCategoryName(categoryName: String): List<UserQuestionEntity> {
        // read categories (Flow<List<UserCategoryEntity>>) and find matching one
        val categories: List<UserCategoryEntity> = userRepository.getAllCategories().first()
        val cat = categories.find { it.name == categoryName } ?: return emptyList()
        return userRepository.getQuestionsByCategory(cat.id).first()
    }

    // -------------------- Confirm / Next --------------------
    private fun confirmOrNext(triggeredByTimeout: Boolean = false) {
        if (_quizUiState.value.isProcessing) return
        _quizUiState.update { it.copy(isProcessing = true) }
        try {
            emitEffect(QuizUiEffect.StopTimerSounds)
            stopTimer()
            val state = _quizUiState.value

            if (!state.isAnswerChecked) {
                val effects = if (::quizEngine.isInitialized) quizEngine.checkAnswer() else emptyList<QuizUiEffect>()
                val results = if (::quizEngine.isInitialized) quizEngine.getResults() else null

                val newReview = ReviewAnswer(
                    index = state.currentIndex,
                    userAnswer = state.selectedOptionText ?: "N/A",
                    correctAnswer = state.currentQuestion?.correctAnswer ?: "N/A"
                )

                _quizUiState.update {
                    it.copy(
                        isAnswerChecked = true,
                        correctScore = results?.correctAnswers ?: it.correctScore,
                        wrongScore = results?.wrongAnswers ?: it.wrongScore,
                        earnedPoints = results?.points ?: it.earnedPoints,
                        completionPercent = results?.completionPercent ?: it.completionPercent,
                        reviewAnswers = it.reviewAnswers + newReview
                    )
                }

                effects.forEach { emitEffect(it) }

                if (triggeredByTimeout && state.selectedOptionText == null &&
                    _settingsUiState.value.ttsEnabled && state.enableTTSOnTimeOut
                ) {
                    emitEffect(QuizUiEffect.SpeakTextRes(R.string.tts_no_answer))
                }
            } else {
                if (::quizEngine.isInitialized && quizEngine.goToNextQuestion()) {
                    _quizUiState.update {
                        it.copy(
                            currentIndex = quizEngine.currentIndex,
                            selectedOptionText = null,
                            isAnswerChecked = false,
                            timeLeft = it.maxTime
                        )
                    }
                    if (_quizUiState.value.showTimer) startTimer()
                } else {
                    finishQuiz(activity = null)
                }
            }
        } finally {
            _quizUiState.update { it.copy(isProcessing = false) }
        }
    }

    private fun handleTimeUp() {
        val state = _quizUiState.value
        if (state.isAnswerChecked || state.isFinished) return

        if (state.enableTTSOnTimeOut && _settingsUiState.value.ttsEnabled) {
            emitEffect(QuizUiEffect.SpeakTextRes(R.string.tts_no_answer))
        }

        emitEffect(QuizUiEffect.StopTimerSounds)
        confirmOrNext(triggeredByTimeout = true)

        if (_settingsUiState.value.autoNext) {
            autoNextJob?.cancel()
            val scheduledQuestion = _quizUiState.value.currentQuestion
            autoNextJob = viewModelScope.launch {
                delay(state.autoNextDelay)
                val currentState = _quizUiState.value
                if (!currentState.isFinished &&
                    currentState.isAnswerChecked &&
                    currentState.currentQuestion == scheduledQuestion
                ) confirmOrNext()
            }
        }
    }

    // -------------------- Finish quiz --------------------
    private fun finishQuiz(activity: Activity?) {
        emitEffect(QuizUiEffect.StopTimerSounds)
        stopTimer()

        val results = if (::quizEngine.isInitialized) quizEngine.getResults() else null

        _quizUiState.update {
            it.copy(
                isFinished = true,
                correctScore = results?.correctAnswers ?: it.correctScore,
                wrongScore = results?.wrongAnswers ?: it.wrongScore,
                earnedPoints = results?.points ?: it.earnedPoints,
                completionPercent = results?.completionPercent ?: it.completionPercent
            )
        }

        // --- Save points only for built-in categories (because DataStore.categoryPoints keys are BuildInCategory) ---
        viewModelScope.launch {
            val state = _quizUiState.value

            // If current selected category is built-in -> update category points map (Map<BuildInCategory, Int>)
            if (_currentCategory is QuizCategory.BuiltIn) {
                val build = (_currentCategory as QuizCategory.BuiltIn).category
                val newPoints = (state.pointsByCategory[build] ?: 0) + state.earnedPoints

                // Save to DataStore (expects Map<BuildInCategory, Int>)
                preferencesManager.saveCategoryPoints(state.pointsByCategory + (build to newPoints))

                // Update local UI state map
                _quizUiState.update { it.copy(pointsByCategory = it.pointsByCategory + (build to newPoints)) }
            }

            // For stats (both built-in and user-created) we use string keys in StatsUiState — that's OK
            val categoryKeyForStats: String = when (val cat = _currentCategory) {
                is QuizCategory.BuiltIn -> cat.category.name
                is QuizCategory.UserCreated -> cat.name
                else -> "Unknown"
            }

            val newTotalQuizzes = _statsUiState.value.totalQuizzes + 1
            val newQuizzesPerCategory = _statsUiState.value.quizzesPerCategory.toMutableMap()
            newQuizzesPerCategory[categoryKeyForStats] = (newQuizzesPerCategory[categoryKeyForStats] ?: 0) + 1

            val newTotalCorrect = _statsUiState.value.totalCorrectAnswers + state.correctScore
            val newTotalWrong = _statsUiState.value.totalWrongAnswers + state.wrongScore

            val newCorrectPerCategory = _statsUiState.value.correctPerCategory.toMutableMap()
            newCorrectPerCategory[categoryKeyForStats] = (newCorrectPerCategory[categoryKeyForStats] ?: 0) + state.correctScore

            val newWrongPerCategory = _statsUiState.value.wrongPerCategory.toMutableMap()
            newWrongPerCategory[categoryKeyForStats] = (newWrongPerCategory[categoryKeyForStats] ?: 0) + state.wrongScore

            _statsUiState.update {
                it.copy(
                    totalQuizzes = newTotalQuizzes,
                    totalCorrectAnswers = newTotalCorrect,
                    totalWrongAnswers = newTotalWrong,
                    quizzesPerCategory = newQuizzesPerCategory,
                    correctPerCategory = newCorrectPerCategory,
                    wrongPerCategory = newWrongPerCategory
                )
            }

            // persist stats
            preferencesManager.saveStats(_statsUiState.value)
        }


        // Optional ads (unchanged)
        if (activity != null) {
            val random = Random.nextBoolean()
            if (random) adsManager.showInterstitial(activity)
            else adsManager.showRewardedAd(activity) { Toast.makeText(activity, "ad rewarded!", Toast.LENGTH_SHORT).show() }
            adsManager.initializeAds(activity)
        }
    }

    // -------------------- Timer Controls --------------------
    fun startTimer() = timer.start(_quizUiState.value.maxTime)
    fun stopTimer() = timer.stop()
    fun pauseTimer() = timer.pause()
    fun resumeTimer() = timer.resume()

    private fun emitEffect(effect: QuizUiEffect) {
        viewModelScope.launch { _quizUiEffect.emit(effect) }
    }

    // -------------------- Helpers --------------------
    // returns Flow<List<DataEntity>>
    private suspend fun getCategoryFlow(category: BuildInCategory) =
        when (category) {
            BuildInCategory.Verbs -> repository.getAllVerbs()
            BuildInCategory.Sentences -> repository.getAllSentences()
            BuildInCategory.PhrasalVerbs -> repository.getAllPhrasalVerbs()
            BuildInCategory.Nouns -> repository.getAllNouns()
            BuildInCategory.Adjectives -> repository.getAllAdjectives()
            BuildInCategory.Adverbs -> repository.getAllAdverbs()
            BuildInCategory.Idioms -> repository.getAllIdioms()
        }

    private fun <T : Any> generateQuestions(items: List<T>): List<Question> {
        return when (items.firstOrNull()) {
            is DataEntity -> {
                val dataItems = items as List<DataEntity>
                dataItems.shuffled().take(_settingsUiState.value.maxQuestions).map { item ->
                    val options = (dataItems.filter { it.en != item.en }
                        .shuffled()
                        .take(QuizConfig.CHOICE_COUNT - 1)
                        .map { it.en } + item.en).shuffled()
                    Question(item.fr, options, item.en)
                }
            }
            is UserQuestionEntity -> {
                val userItems = items as List<UserQuestionEntity>
                userItems.shuffled().take(_settingsUiState.value.maxQuestions).map { item ->
                    Question(item.questionText, item.options, item.correctAnswer)
                }
            }
            else -> emptyList()
        }
    }

    // -------------------- Points & Stats utils --------------------
    fun getTotalPoints(): Int = _quizUiState.value.pointsByCategory.values.sum()

    fun resetAllPoints() {
        viewModelScope.launch {
            preferencesManager.saveCategoryPoints(emptyMap())
            _quizUiState.update { it.copy(pointsByCategory = emptyMap()) }
        }
    }

    fun resetStats() {
        viewModelScope.launch {
            _statsUiState.value = StatsUiState()
            preferencesManager.resetStats()
        }
    }

    fun saveGlobalQuizMode(mode: QuizMode) {
        viewModelScope.launch {
            preferencesManager.setGlobalQuizMode(mode)
            _currentQuizMode.value = mode
        }
    }

    fun setLocalQuizMode(mode: QuizMode) {
        _currentQuizMode.value = mode
    }
}



/*
package futur.apps.composeproject1.viewmodels

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.R
import futur.apps.composeproject1.RoomDatabase.QuizRepository
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuestionEntity
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuizRepository
import futur.apps.composeproject1.ads.AdsManager
import futur.apps.composeproject1.dataStore.AppDataStore
import futur.apps.composeproject1.quizsystem.core.QuizConfig
import futur.apps.composeproject1.quizsystem.core.QuizEngine
import futur.apps.composeproject1.quizsystem.ui.components.ReviewAnswer
import futur.apps.composeproject1.utils.BuildInCategory
import futur.apps.composeproject1.utils.DataEntity
import futur.apps.composeproject1.utils.Question
import futur.apps.composeproject1.utils.QuizMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

// --- Imports from "spacecode" world (the new QuizUiState & supporting classes) ---
// Make sure these imports match your project structure. The user indicated they already
// have the new QuizUiState (spacecode package) in their project.


// -------------------- One-time UI Effects (from MyViewModel) --------------------
sealed class QuizUiEffect {
    object PlayCorrectSound : QuizUiEffect()
    object PlayWrongSound : QuizUiEffect()
    object PlayTimerTick : QuizUiEffect()
    object PlayTimerUrgent : QuizUiEffect()
    object StopTimerSounds : QuizUiEffect()
    data class SpeakTextRes(val resId: Int) : QuizUiEffect()
    object None : QuizUiEffect()
}

// -------------------- Events (from MyViewModel) --------------------
sealed class QuizEvent {
    data class SelectOption(val optionText: String) : QuizEvent()
    object ConfirmOrNext : QuizEvent()
    object Retry : QuizEvent()
    object TimeUp : QuizEvent()
}

sealed class QuizCategory {
    data class BuiltIn(val category: BuildInCategory) : QuizCategory()
    data class UserCreated(val name: String) : QuizCategory()
}

// -------------------- Merged QuizViewModel --------------------
@HiltViewModel
class QuizViewModel @Inject constructor(
    private val preferencesManager: AppDataStore,
    private val repository: QuizRepository,
    private val userRepository: UserQuizRepository,
    private val adsManager: AdsManager,
) : ViewModel()
{

    // -------------------- State & Effects --------------------
    // Initialize with defaults from the spacecode QuizUiState
    private val _quizUiState = MutableStateFlow(QuizUiState())
    val quizUiState: StateFlow<QuizUiState> = _quizUiState.asStateFlow()

    // Add this inside QuizViewModel
    private val _settingsUiState = MutableStateFlow(SettingsUiState())
    val settingsUiState: StateFlow<SettingsUiState> = _settingsUiState.asStateFlow()


    // -------------------- Stats State --------------------
    private val _statsUiState = MutableStateFlow(StatsUiState())
    val statsUiState: StateFlow<StatsUiState> = _statsUiState.asStateFlow()


    private val _quizUiEffect = MutableSharedFlow<QuizUiEffect>()
    val quizUiEffect: SharedFlow<QuizUiEffect> = _quizUiEffect.asSharedFlow()

    // For compatibility with your previous UI code that relied on these "effect" flows
    private val _events = MutableSharedFlow<Any>()
    val events = _events.asSharedFlow()

    // Keep a small confirm/next trigger as in original file (some UI expected it).
    private val _confirmOrNextEvent = MutableSharedFlow<Unit>()
    val confirmOrNextEvent = _confirmOrNextEvent.asSharedFlow()

    // --- Global + Local Quiz Mode ---
    private val _currentQuizMode = MutableStateFlow(QuizMode.BUILT_IN)
    val currentQuizMode: StateFlow<QuizMode> = _currentQuizMode.asStateFlow()


    val maxTime = QuizConfig.QUESTION_TIME_LIMIT

    // -------------------- Internal quiz engine/timer --------------------
    private lateinit var quizEngine: QuizEngine

    // Use the QuizTimer (from MyViewModel) to handle tick/finish semantics.
    private val timer = QuizTimer(
        scope = viewModelScope,
        tickInterval = QuizConfig.TIMER_TICK_INTERVAL,
        onTick = { newTime ->
            _quizUiState.update { it.copy(timeLeft = newTime) }

            // Handle ticking sound effects (normal or urgent)
            val enableSounds = _settingsUiState.value.soundEnabled
            if (enableSounds) {
                if (newTime <= QuizConfig.TIMER_CRITICAL_THRESHOLD) {
                    emitEffect(QuizUiEffect.PlayTimerUrgent)
                } else {
                    emitEffect(QuizUiEffect.PlayTimerTick)
                }
            }
        },
        onFinish = {
            onEvent(QuizEvent.TimeUp)
        }
    )

    private var autoNextJob: Job? = null

    // -------------------- DataStore: load saved category points --------------------
    // ---------------------------------------------------------
    // 🧠 Load existing category points from DataStore
    // ---------------------------------------------------------


        // ✅ Observe settings from DataStore
            init {
                // -------------------- BuildInCategory Points --------------------
                viewModelScope.launch {
                    preferencesManager.categoryPoints.collect { savedPoints ->
                        _quizUiState.update { it.copy(pointsByCategory = savedPoints) }
                    }
                }

                // -------------------- Stats --------------------
            viewModelScope.launch {
                preferencesManager.statsFlow.collect { savedStats ->
                    _statsUiState.value = savedStats.copy(isLoading = false)
                }
            }


                // -------------------- Dark Mode --------------------
                viewModelScope.launch {
                    preferencesManager.isDarkThemeEnabled.collect { dark ->
                        _settingsUiState.update { it.copy(isDarkMode = dark) }
                    }
                }

                // -------------------- Language --------------------
                viewModelScope.launch {
                    preferencesManager.selectedLanguage.collect { lang ->
                        _settingsUiState.update { it.copy(language = lang) }
                    }
                }

                // -------------------- Palette --------------------
                viewModelScope.launch {
                    preferencesManager.selectedPaletteName.collect { palette ->
                        _settingsUiState.update { it.copy(selectedPalette = palette) }
                    }
                }

                // -------------------- Auto Next --------------------
                viewModelScope.launch {
                    preferencesManager.autoNext.collect { autoNext ->
                        _settingsUiState.update { it.copy(autoNext = autoNext) }
                    }
                }

                // -------------------- Sound --------------------
                viewModelScope.launch {
                    preferencesManager.enableSounds.collect { sound ->
                        _settingsUiState.update { it.copy(soundEnabled = sound) }
                    }
                }

                // -------------------- TTS --------------------
                viewModelScope.launch {
                    preferencesManager.enableTTS.collect { tts ->
                        _settingsUiState.update { it.copy(ttsEnabled = tts) }
                    }
                }

                // -------------------- Max Questions --------------------
                viewModelScope.launch {
                    preferencesManager.maxQuestions.collect { maxQ ->
                        _settingsUiState.update { it.copy(maxQuestions = maxQ) }
                    }
                }
            // -------------------- Quiz Mode --------------------
            viewModelScope.launch {
                preferencesManager.globalQuizMode.collectLatest { savedMode ->
                    _currentQuizMode.value = savedMode // keep global preference as default
                }
            }

        }


    // -------------------- Event handler (public) --------------------
    fun onEvent(event: QuizEvent) {
        when (event) {
            is QuizEvent.SelectOption -> selectOption(event.optionText)
            QuizEvent.ConfirmOrNext -> confirmOrNext()
            QuizEvent.Retry -> _quizUiState.value.category?.let { startNewQuiz(it) }
            QuizEvent.TimeUp -> handleTimeUp()
        }
    }

    // -------------------- Select Option --------------------
    private fun selectOption(option: String) {
        // Delegate to engine if available
        if (::quizEngine.isInitialized) quizEngine.selectOption(option)
        _quizUiState.update { it.copy(selectedOptionText = option) }
    }

    // -------------------- Start / Reset Quiz --------------------
    fun setCategory(activity: Activity?, category: BuildInCategory?) {
        viewModelScope.launch {
            _quizUiState.update {
                it.copy(
                    category = category,
                    isFinished = false,
                    isLoading = false,
                    error = null
                )
            }
            startNewQuiz(category)
        }
    }

    // -------------------- Start / Reset Quiz --------------------
    fun startNewQuiz(category: BuildInCategory?)
    {
        stopTimer()

        viewModelScope.launch {
            // show loader and set category (may be null if caller passed null)
            _quizUiState.value = _quizUiState.value.copy(
                isLoading = true,
                category = category,
                currentIndex = 0
            )

            try {

                // convert repository items into Question list (your helper)
                val items = getCategoryFlow(category).first()

                var questions = when (items) {
                    is List<*> -> when {
                        items.isNotEmpty() && items.first() is UserQuestionEntity -> {
                            generateQuestions(items.filterIsInstance<UserQuestionEntity>())
                        }
                        items.isNotEmpty() && items.first() is DataEntity -> {
                            generateQuestions(items.filterIsInstance<DataEntity>())
                        }
                        else -> emptyList()
                    }
                    else -> emptyList()
                }


                // apply UI preferences
                if (_quizUiState.value.shuffleQuestions) questions = questions.shuffled()
                if (_quizUiState.value.shuffleOptions) {
                    questions = questions.map { q -> q.copy(options = q.options.shuffled()) }
                }

                // limit questions
                questions = questions.take(_settingsUiState.value.maxQuestions)

                // init quiz engine
                quizEngine = QuizEngine(
                    questions = questions,
                    enableNegativeScoring = _quizUiState.value.enableNegativeScoring,
                    enableSounds = _settingsUiState.value.soundEnabled
                )

                // update full UI state for new quiz (stop loading)
                _quizUiState.value = _quizUiState.value.copy(
                    questions = questions,
                    timeLeft = maxTime,
                    currentIndex = 0,
                    selectedOptionText = null,
                    isAnswerChecked = false,
                    isFinished = false,
                    correctScore = 0,
                    wrongScore = 0,
                    earnedPoints = 0,
                    completionPercent = 0,
                    reviewAnswers = emptyList(),
                    isLoading = false
                )

                // start timer if enabled
                if (_quizUiState.value.showTimer) startTimer()

            } catch (e: Exception) {
                // make sure loader is hidden and error is set
                _quizUiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }


    // -------------------- Confirm Or Next --------------------
    private fun confirmOrNext(triggeredByTimeout: Boolean = false) {
        if (_quizUiState.value.isProcessing) return
        _quizUiState.update { it.copy(isProcessing = true) }

        try {
            emitEffect(QuizUiEffect.StopTimerSounds)
            stopTimer()
            val state = _quizUiState.value

            if (!state.isAnswerChecked) {
                // --- Check answer via engine ---
                val effects = if (::quizEngine.isInitialized) quizEngine.checkAnswer() else emptyList<QuizUiEffect>()
                val results = if (::quizEngine.isInitialized) quizEngine.getResults() else null

                // Build a ReviewAnswer entry
                val newReview = ReviewAnswer(
                    index = state.currentIndex,
                    userAnswer = state.selectedOptionText ?: "N/A",
                    correctAnswer = state.currentQuestion?.correctAnswer ?: "N/A"
                )

                _quizUiState.update {
                    it.copy(
                        isAnswerChecked = true,
                        correctScore = results?.correctAnswers ?: it.correctScore,
                        wrongScore = results?.wrongAnswers ?: it.wrongScore,
                        earnedPoints = results?.points ?: it.earnedPoints,
                        completionPercent = results?.completionPercent ?: it.completionPercent,
                        reviewAnswers = it.reviewAnswers + newReview
                    )
                }

                // Emit sound / TTS effects returned by engine (converted)
                effects.forEach { eff ->
                    // If engine returns QuizUiEffect instances from the same sealed class, emit them directly.
                    // Otherwise, adjust mapping from engine effect type to this QuizUiEffect.
                    emitEffect(eff)
                }

                // TTS: If triggered by timeout and no answer selected, optionally speak a "no answer" message
                if (triggeredByTimeout && state.selectedOptionText == null &&
                    _settingsUiState.value.ttsEnabled && state.enableTTSOnTimeOut
                ) {
                    // replace R.string.tts_no_answer with your actual string resource
                    emitEffect(QuizUiEffect.SpeakTextRes(android.R.string.unknownName))
                }

            } else {
                // Move to next question or finish quiz
                if (::quizEngine.isInitialized && quizEngine.goToNextQuestion()) {
                    _quizUiState.update {
                        it.copy(
                            currentIndex = quizEngine.currentIndex,
                            selectedOptionText = null,
                            isAnswerChecked = false,
                            timeLeft = it.maxTime
                        )
                    }
                    if (_quizUiState.value.showTimer) startTimer()
                } else {
                    finishQuiz(activity = null) // activity may be needed for ads; adapt when calling
                }
            }
        } finally {
            _quizUiState.update { it.copy(isProcessing = false) }
        }
    }

    // ---------- Handle time up ----------
    private fun handleTimeUp() {
        val state = _quizUiState.value
        if (state.isAnswerChecked || state.isFinished) return

        if (state.enableTTSOnTimeOut && _settingsUiState.value.ttsEnabled) {
            // replace with your actual string resource id for "time up"
            emitEffect(QuizUiEffect.SpeakTextRes(R.string.tts_no_answer))
        }

        emitEffect(QuizUiEffect.StopTimerSounds)
        confirmOrNext(triggeredByTimeout = true)

        // Auto-move to next question after delay if enabled
        if (_settingsUiState.value.autoNext) {
            autoNextJob?.cancel()
            val scheduledQuestion = _quizUiState.value.currentQuestion
            autoNextJob = viewModelScope.launch {
                delay(state.autoNextDelay)
                val currentState = _quizUiState.value
                if (!currentState.isFinished &&
                    currentState.isAnswerChecked &&
                    currentState.currentQuestion == scheduledQuestion
                ) confirmOrNext()
            }
        }
    }

    // -------------------- Finish quiz --------------------
    // Activity is optional here; if null we skip showing ads (callers can pass activity when needed)
    // ---------------------------------------------------------
    // 🏁 Finish quiz — Save points by category
    // ---------------------------------------------------------
    private fun finishQuiz(activity: Activity?) {
        emitEffect(QuizUiEffect.StopTimerSounds)
        stopTimer()

        val results = if (::quizEngine.isInitialized) quizEngine.getResults() else null

        _quizUiState.update {
            it.copy(
                isFinished = true,
                correctScore = results?.correctAnswers ?: it.correctScore,
                wrongScore = results?.wrongAnswers ?: it.wrongScore,
                earnedPoints = results?.points ?: it.earnedPoints,
                completionPercent = results?.completionPercent ?: it.completionPercent
            )
        }

        // ✅ Save new category points
        viewModelScope.launch {
            val state = _quizUiState.value
            val category = state.category ?: return@launch
            val newPoints = (state.pointsByCategory[category] ?: 0) + state.earnedPoints

            // Update both DataStore and local state
            preferencesManager.saveCategoryPoints(state.pointsByCategory + (category to newPoints))
            _quizUiState.update { it.copy(pointsByCategory = it.pointsByCategory + (category to newPoints)) }
        }
        // ----------------✅ Update Stats ----------------
        viewModelScope.launch {
            val state = _quizUiState.value
            val categoryName = state.category?.name ?: "Unknown"

            // --- Total quizzes ---
            val newTotalQuizzes = _statsUiState.value.totalQuizzes + 1

            // --- Quizzes per category ---
            val newQuizzesPerCategory = _statsUiState.value.quizzesPerCategory.toMutableMap()
            newQuizzesPerCategory[categoryName] = (newQuizzesPerCategory[categoryName] ?: 0) + 1

            // --- Total correct/wrong ---
            val newTotalCorrect = _statsUiState.value.totalCorrectAnswers + state.correctScore
            val newTotalWrong = _statsUiState.value.totalWrongAnswers + state.wrongScore

            // --- Correct/wrong per category ---
            val newCorrectPerCategory = _statsUiState.value.correctPerCategory.toMutableMap()
            newCorrectPerCategory[categoryName] = (newCorrectPerCategory[categoryName] ?: 0) + state.correctScore

            val newWrongPerCategory = _statsUiState.value.wrongPerCategory.toMutableMap()
            newWrongPerCategory[categoryName] = (newWrongPerCategory[categoryName] ?: 0) + state.wrongScore

            // --- Apply new stats ---
            _statsUiState.update {
                it.copy(
                    totalQuizzes = newTotalQuizzes,
                    totalCorrectAnswers = newTotalCorrect,
                    totalWrongAnswers = newTotalWrong,
                    quizzesPerCategory = newQuizzesPerCategory,
                    correctPerCategory = newCorrectPerCategory,
                    wrongPerCategory = newWrongPerCategory
                )
            }

            // Save stats persistently
            preferencesManager.saveStats(_statsUiState.value)

        }


        // Optional: show ad
        if (activity != null) {
            val random = Random.nextBoolean()
            if (random) adsManager.showInterstitial(activity)
            else adsManager.showRewardedAd(activity) {
                Toast.makeText(activity, "ad rewarded!", Toast.LENGTH_SHORT).show()
            }
            adsManager.initializeAds(activity)
        }
    }

  */
/*  private fun finishQuiz(activity: Activity?) {
        emitEffect(QuizUiEffect.StopTimerSounds)
        stopTimer()
        val results = if (::quizEngine.isInitialized) quizEngine.getResults() else null

        _quizUiState.update {
            it.copy(
                isFinished = true,
                correctScore = results?.correctAnswers ?: it.correctScore,
                wrongScore = results?.wrongAnswers ?: it.wrongScore,
                earnedPoints = results?.points ?: it.earnedPoints,
                completionPercent = results?.completionPercent ?: it.completionPercent
            )
        }



        // Show an ad if Activity is provided
        if (activity != null) {
            val random: Boolean = Random.nextBoolean()
            if (random) {
                adsManager.showInterstitial(activity = activity)
            } else {
                adsManager.showRewardedAd(activity) {
                    Toast.makeText(activity, "ad rewarded!", Toast.LENGTH_SHORT).show()
                }
            }
            adsManager.initializeAds(activity)
        }
    }*//*


    // -------------------- Timer Controls --------------------
    fun startTimer() = timer.start(_quizUiState.value.maxTime)
    fun stopTimer() = timer.stop()
    fun pauseTimer() = timer.pause()
    fun resumeTimer() = timer.resume()

    // -------------------- Emit Effects --------------------
    private fun emitEffect(effect: QuizUiEffect) {
        viewModelScope.launch { _quizUiEffect.emit(effect) }
    }

    // -------------------- Helpers --------------------



    // Repository helpers: fetch flows per BuildInCategory (as in your old ViewModel)
    private suspend fun getCategoryFlow(category: BuildInCategory?) =
        if (_currentQuizMode.value == QuizMode.USER_CREATED) {
            // 👇 Load user data from UserRoom tables
            val all = userRepository.getQuestionsByCategory(0)
            kotlinx.coroutines.flow.flowOf(all)
        } else {
            // 👇 Default app questions (from built-in Room)
            when (category) {
                BuildInCategory.Verbs -> repository.getAllVerbs()
                BuildInCategory.Sentences -> repository.getAllSentences()
                BuildInCategory.PhrasalVerbs -> repository.getAllPhrasalVerbs()
                BuildInCategory.Nouns -> repository.getAllNouns()
                BuildInCategory.Adjectives -> repository.getAllAdjectives()
                BuildInCategory.Adverbs -> repository.getAllAdverbs()
                BuildInCategory.Idioms -> repository.getAllIdioms()
                else -> repository.getAllVerbs()
            }
        }
    private fun <T : Any> generateQuestions(items: List<T>): List<Question> {
        return when (items.firstOrNull()) {
            is DataEntity -> {
                val dataItems = items as List<DataEntity>
                dataItems.shuffled().take(_settingsUiState.value.maxQuestions).map { item ->
                    val options = (dataItems.filter { it.en != item.en }
                        .shuffled()
                        .take(QuizConfig.CHOICE_COUNT - 1)
                        .map { it.en } + item.en).shuffled()

                    Question(
                        questionText = item.fr,
                        options = options,
                        correctAnswer = item.en
                    )
                }
            }
            is UserQuestionEntity -> {
                val userItems = items as List<UserQuestionEntity>
                userItems.shuffled().take(_settingsUiState.value.maxQuestions).map { item ->
                    Question(
                        questionText = item.questionText,
                        options = item.options,
                        correctAnswer = item.correctAnswer
                    )
                }
            }
            else -> emptyList()
        }
    }


    // ---------------------------------------------------------
    // 🧮 Points Utilities
    // ---------------------------------------------------------

    */
/** Returns the total points accumulated across all categories. *//*

    fun getTotalPoints(): Int {
        return _quizUiState.value.pointsByCategory.values.sum()
    }

    */
/** Resets all points in DataStore and UI state to zero. *//*

    fun resetAllPoints() {
        viewModelScope.launch {
            // Clear DataStore
            preferencesManager.saveCategoryPoints(emptyMap())

            // Update UI state
            _quizUiState.update { it.copy(pointsByCategory = emptyMap()) }
        }
    }
    fun resetStats() {
        viewModelScope.launch {
            _statsUiState.value = StatsUiState()
            preferencesManager.resetStats()
        }
    }

    */
/** Called from Settings to persist a new default mode globally. *//*

    fun saveGlobalQuizMode(mode: QuizMode) {
        viewModelScope.launch {
            preferencesManager.setGlobalQuizMode(mode)
            _currentQuizMode.value = mode
        }
    }

    */
/** Called from any screen to temporarily override the mode. *//*

    fun setLocalQuizMode(mode: QuizMode) {
        _currentQuizMode.value = mode
    }



}
*/
