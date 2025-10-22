package futur.apps.composeproject1.viewmodels

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
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
import futur.apps.composeproject1.utils.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

// ============================================================
// 🔹 Unified Category Abstraction
// ============================================================
sealed class QuizCategory : java.io.Serializable {
    data class Default(val category: DefaultCategory) : QuizCategory()
    data class Custom(val name: String) : QuizCategory()
}

// ============================================================
// 🔹 UI Effects & Events
// ============================================================
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

// ============================================================
// 🧠 ViewModel
// ============================================================
@HiltViewModel
class QuizViewModel @Inject constructor(
    private val preferences: AppDataStore,
    private val repository: QuizRepository,
    private val userRepository: UserQuizRepository,
    private val ads: AdsManager,
    private val savedState: SavedStateHandle
) : ViewModel() {

    private val _ui = MutableStateFlow(QuizUiState())
    val ui: StateFlow<QuizUiState> = _ui.asStateFlow()

    private val _settings = MutableStateFlow(SettingsUiState())
    val settings: StateFlow<SettingsUiState> = _settings.asStateFlow()

    private val _stats = MutableStateFlow(StatsUiState())
    val stats: StateFlow<StatsUiState> = _stats.asStateFlow()

    private val _effect = MutableSharedFlow<QuizUiEffect>()
    val effect: SharedFlow<QuizUiEffect> = _effect.asSharedFlow()

    private val _mode = MutableStateFlow(QuizMode.DEFAULT)
    val mode: StateFlow<QuizMode> = _mode.asStateFlow()

    private var currentCategory: QuizCategory? = null
    private lateinit var engine: QuizEngine
    private var autoNextJob: Job? = null

    private val timer = QuizTimer(
        scope = viewModelScope,
        tickInterval = QuizConfig.TIMER_TICK_INTERVAL,
        onTick = { time ->
            _ui.update { it.copy(timeLeft = time) }
            if (_settings.value.soundEnabled) {
                if (time <= QuizConfig.TIMER_CRITICAL_THRESHOLD)
                    emitEffect(QuizUiEffect.PlayTimerUrgent)
                else emitEffect(QuizUiEffect.PlayTimerTick)
            }
        },
        onFinish = { onEvent(QuizEvent.TimeUp) }
    )

    // ---------------------------------------------------------
    // ⚙️ Initialization
    // ---------------------------------------------------------
    init {
        collectSettings()
    }

    private fun collectSettings() {
        fun <T> collect(flow: Flow<T>, update: (SettingsUiState, T) -> SettingsUiState) {
            viewModelScope.launch {
                flow.collect { v -> _settings.update { update(it, v) } }
            }
        }

        // A tiny holder so we can destructure cleanly in collect
        data class Snapshot(
            val builtPoints: Map<DefaultCategory, Int>,
            val userPoints: Map<String, Int>,
            val builtStats: StatsUiState,
            val userStats: StatsUiState,
            val mode: QuizMode
        )

        // ------------------------------------------------------------
        // 🧩 Collect Points, Stats, and Mode together (mode-aware)
        // ------------------------------------------------------------
        viewModelScope.launch {
            combine(
                preferences.builtInCategoryPoints,
                preferences.userCategoryPoints,
                preferences.builtInStats,
                preferences.userStats,
                preferences.globalQuizMode
            ) { builtPts, userPts, builtSt, userSt, mode ->
                Snapshot(builtPts, userPts, builtSt, userSt, mode)
            }.collect { (builtPoints, userPoints, builtStats, userStats, mode) ->

                // keep the VM's mode in sync with global mode
                _mode.value = mode

                // ------------------------------------------------------------
                // 🧩 Points: only load the active mode’s points
                // ------------------------------------------------------------
                val activePointsAny: Map<Any, Int> = when (mode) {
                    QuizMode.DEFAULT -> builtPoints.entries.associate { (k, v) -> k as Any to v }
                    QuizMode.CUSTOM  -> userPoints.entries.associate { (k, v) -> k as Any to v }
                }
                _ui.update { it.copy(pointsByCategory = activePointsAny) }

                // ------------------------------------------------------------
                // 📊 Stats: only load one source, never mix them
                // ------------------------------------------------------------
                _stats.value = when (mode) {
                    QuizMode.DEFAULT -> builtStats.copy(isLoading = false)
                    QuizMode.CUSTOM  -> userStats.copy(isLoading = false)
                }
            }
        }


        // ------------------------------------------------------------
        // 🎨 Collect UI & Behavior Settings
        // ------------------------------------------------------------
        collect(preferences.isDarkThemeEnabled) { s, v -> s.copy(isDarkMode = v) }
        collect(preferences.selectedLanguage)    { s, v -> s.copy(language = v) }
        collect(preferences.selectedPaletteName) { s, v -> s.copy(selectedPalette = v) }
        collect(preferences.autoNext)            { s, v -> s.copy(autoNext = v) }
        collect(preferences.enableSounds)        { s, v -> s.copy(soundEnabled = v) }
        collect(preferences.enableTTS)           { s, v -> s.copy(ttsEnabled = v) }
        collect(preferences.maxQuestions)        { s, v -> s.copy(maxQuestions = v) }

        // Note: we already combine globalQuizMode above, so no separate collector needed.
    }


    // ---------------------------------------------------------
    // 🚀 Public API
    // ---------------------------------------------------------
    fun initializeQuiz(category: QuizCategory, mode: QuizMode) {
        _mode.value = mode
        setCategory(category)
    }

    fun onEvent(e: QuizEvent) {
        when (e) {
            is QuizEvent.SelectOption -> selectOption(e.optionText)
            QuizEvent.ConfirmOrNext -> confirmOrNext()
            QuizEvent.Retry -> currentCategory?.let { startNewQuiz(it) }
            QuizEvent.TimeUp -> handleTimeUp()
        }
    }

    fun getTotalPoints(): Int = _ui.value.pointsByCategory.values.sum()

    fun resetAllPoints() {
        viewModelScope.launch {
            preferences.resetAllCategoryPoints()
            _ui.update { it.copy(pointsByCategory = emptyMap()) }
        }
    }

    fun resetStats() {
        viewModelScope.launch {
            _stats.value = StatsUiState()
            preferences.resetStats()
        }
    }

    fun saveGlobalQuizMode(mode: QuizMode) {
        viewModelScope.launch {
            preferences.setGlobalQuizMode(mode)
            _mode.value = mode
        }
    }

    // ---------------------------------------------------------
    // 🧩 Core Quiz Lifecycle
    // ---------------------------------------------------------
    private fun selectOption(opt: String) {
        if (::engine.isInitialized) engine.selectOption(opt)
        _ui.update { it.copy(selectedOptionText = opt) }
    }

    fun setCategory(category: QuizCategory) {
        currentCategory = category
        _ui.update { it.copy(category = category, isFinished = false, isLoading = true, error = null) }
        startNewQuiz(category)
    }

    private fun startNewQuiz(category: QuizCategory) {
        stopTimer()
        viewModelScope.launch {
            _ui.update { it.copy(isLoading = true, currentIndex = 0) }
            try {
                val items: List<Any> = when (category) {
                    is QuizCategory.Default -> getCategoryFlow(category.category).first()
                    is QuizCategory.Custom -> getUserQuestions(category.name)
                }

                var qs = generateQuestions(items)
                if (_ui.value.shuffleQuestions) qs = qs.shuffled()
                if (_ui.value.shuffleOptions) qs = qs.map { it.copy(options = it.options.shuffled()) }
                qs = qs.take(_settings.value.maxQuestions)

                engine = QuizEngine(qs, _ui.value.enableNegativeScoring, _settings.value.soundEnabled)

                _ui.value = _ui.value.copy(
                    questions = qs,
                    currentIndex = 0,
                    timeLeft = _ui.value.maxTime,
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

                if (_ui.value.showTimer) startTimer()
            } catch (e: Exception) {
                _ui.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private suspend fun getUserQuestions(name: String): List<UserQuestionEntity> {
        val categories = userRepository.getAllCategories().first()
        val cat = categories.find { it.name == name } ?: return emptyList()
        return userRepository.getQuestionsByCategory(cat.id).first()
    }

    // ---------------------------------------------------------
    // ⚡ Confirm or Next
    // ---------------------------------------------------------
    private fun confirmOrNext(fromTimeout: Boolean = false) {
        if (_ui.value.isProcessing) return
        _ui.update { it.copy(isProcessing = true) }

        try {
            emitEffect(QuizUiEffect.StopTimerSounds)
            stopTimer()
            val state = _ui.value

            if (!state.isAnswerChecked) {
                val effects = if (::engine.isInitialized) engine.checkAnswer() else emptyList()
                val res = if (::engine.isInitialized) engine.getResults() else null

                val review = ReviewAnswer(
                    index = state.currentIndex,
                    userAnswer = state.selectedOptionText ?: "N/A",
                    correctAnswer = state.currentQuestion?.correctAnswer ?: "N/A"
                )

                _ui.update {
                    it.copy(
                        isAnswerChecked = true,
                        correctScore = res?.correctAnswers ?: it.correctScore,
                        wrongScore = res?.wrongAnswers ?: it.wrongScore,
                        earnedPoints = res?.points ?: it.earnedPoints,
                        completionPercent = res?.completionPercent ?: it.completionPercent,
                        reviewAnswers = it.reviewAnswers + review
                    )
                }

                effects.forEach(::emitEffect)
                if (fromTimeout && state.selectedOptionText == null &&
                    _settings.value.ttsEnabled && state.enableTTSOnTimeOut
                ) emitEffect(QuizUiEffect.SpeakTextRes(R.string.tts_no_answer))
            } else {
                if (::engine.isInitialized && engine.goToNextQuestion()) {
                    _ui.update {
                        it.copy(
                            currentIndex = engine.currentIndex,
                            selectedOptionText = null,
                            isAnswerChecked = false,
                            timeLeft = it.maxTime
                        )
                    }
                    if (_ui.value.showTimer) startTimer()
                } else {
                    finishQuiz(null)
                }
            }
        } finally {
            _ui.update { it.copy(isProcessing = false) }
        }
    }
    // ---------------------------------------------------------
// ⏰ Time Up
// ---------------------------------------------------------
    private fun handleTimeUp() {
        val s = _ui.value
        if (s.isAnswerChecked || s.isFinished) return

        // Optional TTS announcement
        if (s.enableTTSOnTimeOut && _settings.value.ttsEnabled)
            emitEffect(QuizUiEffect.SpeakTextRes(R.string.tts_no_answer))

        // Stop timer sound and confirm answer
        emitEffect(QuizUiEffect.StopTimerSounds)
        confirmOrNext(fromTimeout = true)

        // Auto move to next question after delay (if enabled)
        if (_settings.value.autoNext) {
            autoNextJob?.cancel()
            val current = s.currentQuestion
            autoNextJob = viewModelScope.launch {
                delay(s.autoNextDelay)
                val newState = _ui.value
                if (!newState.isFinished &&
                    newState.isAnswerChecked &&
                    newState.currentQuestion == current
                ) confirmOrNext()
            }
        }
    }

    // ---------------------------------------------------------
    // 🏁 Finish Quiz
    // ---------------------------------------------------------
    private fun finishQuiz(activity: Activity?) {
        emitEffect(QuizUiEffect.StopTimerSounds)
        stopTimer()
        updateResults()
        savePointsAndStats()
        showAds(activity)
    }

    private fun updateResults() {
        val r = if (::engine.isInitialized) engine.getResults() else null
        _ui.update {
            it.copy(
                isFinished = true,
                correctScore = r?.correctAnswers ?: it.correctScore,
                wrongScore = r?.wrongAnswers ?: it.wrongScore,
                earnedPoints = r?.points ?: it.earnedPoints,
                completionPercent = r?.completionPercent ?: it.completionPercent
            )
        }
    }

    // ---------------------------------------------------------
    // 💾 Save Points & Stats — mode-aware
    // ---------------------------------------------------------
    // ---------------------------------------------------------
// 💾 Save Points & Stats — mode-aware (Fixed)
// ---------------------------------------------------------
    private fun savePointsAndStats() {
        viewModelScope.launch {
            val s = _ui.value
            val cat = currentCategory
            val currentMode = _mode.value

            // =========================
            // 🔹 Save Points
            // =========================
            when (cat) {
                is QuizCategory.Default -> {
                    val key = cat.category
                    val currentMap = preferences.builtInCategoryPoints.first()
                    val currentPts = currentMap[key] ?: 0
                    val newPts = (currentPts + s.earnedPoints).coerceIn(0, key.maxPoints)
                    val updatedMap = currentMap + (key to newPts)

                    preferences.saveBuiltInCategoryPoints(updatedMap)
                    _ui.update {
                        it.copy(pointsByCategory = updatedMap.entries.associate { (k, v) -> k as Any to v })
                    }
                }

                is QuizCategory.Custom -> {
                    val key = cat.name
                    val currentMap = preferences.userCategoryPoints.first()
                    val currentPts = currentMap[key] ?: 0
                    val newPts = (currentPts + s.earnedPoints).coerceAtLeast(0)
                    val updatedMap = currentMap + (key to newPts)

                    preferences.saveUserCategoryPoints(updatedMap)
                    _ui.update {
                        it.copy(pointsByCategory = updatedMap.entries.associate { (k, v) -> k as Any to v })
                    }
                }

                else -> Unit
            }

            // =========================
            // 🔹 Save Stats
            // =========================
            val key = when (cat) {
                is QuizCategory.Default -> cat.category.name
                is QuizCategory.Custom -> cat.name
                else -> "Unknown"
            }

            val ns = _stats.value.copy(
                totalQuizzes = _stats.value.totalQuizzes + 1,
                totalCorrectAnswers = _stats.value.totalCorrectAnswers + s.correctScore,
                totalWrongAnswers = _stats.value.totalWrongAnswers + s.wrongScore,
                quizzesPerCategory = _stats.value.quizzesPerCategory.toMutableMap()
                    .apply { this[key] = (this[key] ?: 0) + 1 },
                correctPerCategory = _stats.value.correctPerCategory.toMutableMap()
                    .apply { this[key] = (this[key] ?: 0) + s.correctScore },
                wrongPerCategory = _stats.value.wrongPerCategory.toMutableMap()
                    .apply { this[key] = (this[key] ?: 0) + s.wrongScore }
            )

            _stats.value = ns
            when (currentMode) {
                QuizMode.DEFAULT -> preferences.saveBuiltInStats(ns)
                QuizMode.CUSTOM -> preferences.saveUserStats(ns)
            }
        }
    }


    // ---------------------------------------------------------
    // 🕒 Timer Controls & Helpers
    // ---------------------------------------------------------
    fun startTimer() = timer.start(_ui.value.maxTime)
    fun stopTimer() = timer.stop()
    fun pauseTimer() = timer.pause()
    fun resumeTimer() = timer.resume()
    private fun emitEffect(e: QuizUiEffect) { viewModelScope.launch { _effect.emit(e) } }

    private suspend fun getCategoryFlow(cat: DefaultCategory) = when (cat) {
        DefaultCategory.Verbs -> repository.getAllVerbs()
        DefaultCategory.Sentences -> repository.getAllSentences()
        DefaultCategory.PhrasalVerbs -> repository.getAllPhrasalVerbs()
        DefaultCategory.Nouns -> repository.getAllNouns()
        DefaultCategory.Adjectives -> repository.getAllAdjectives()
        DefaultCategory.Adverbs -> repository.getAllAdverbs()
        DefaultCategory.Idioms -> repository.getAllIdioms()
    }

    private fun <T : Any> generateQuestions(items: List<T>): List<Question> = when (items.firstOrNull()) {
        is DataEntity -> {
            val data = items as List<DataEntity>
            data.shuffled().take(_settings.value.maxQuestions).map { d ->
                val options = (data.filter { it.en != d.en }
                    .shuffled().take(QuizConfig.CHOICE_COUNT - 1)
                    .map { it.en } + d.en).shuffled()
                Question(d.fr, options, d.en)
            }
        }
        is UserQuestionEntity -> {
            val user = items as List<UserQuestionEntity>
            user.shuffled().take(_settings.value.maxQuestions).map { q ->
                Question(q.questionText, q.options, q.correctAnswer)
            }
        }
        else -> emptyList()
    }

    private fun showAds(activity: Activity?) {
        activity?.let {
            val random = Random.nextBoolean()
            if (random) ads.showInterstitial(it)
            else ads.showRewardedAd(it) {
                Toast.makeText(activity, "ad rewarded!", Toast.LENGTH_SHORT).show()
            }
            ads.initializeAds(it)
        }
    }

    // --- Add inside QuizViewModel ---
    fun resetBuiltInStats() {
        viewModelScope.launch {
            val empty = StatsUiState(isLoading = false)
            _stats.value = empty
            preferences.saveBuiltInStats(empty)
        }
    }

    fun resetUserStats() {
        viewModelScope.launch {
            val empty = StatsUiState(isLoading = false)
            _stats.value = empty
            preferences.saveUserStats(empty)
        }
    }

    fun resetStatsFor(mode: QuizMode) { if (mode == QuizMode.DEFAULT) resetBuiltInStats() else resetUserStats() }


}
