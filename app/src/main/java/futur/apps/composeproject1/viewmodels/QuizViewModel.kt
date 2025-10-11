package futur.apps.composeproject1.viewmodels

import android.app.Activity
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.utils.Question
import futur.apps.composeproject1.R
import futur.apps.composeproject1.RoomDatabase.QuizRepository
import futur.apps.composeproject1.ads.AdsManager
import futur.apps.composeproject1.dataStore.AppDataStore
import futur.apps.composeproject1.quizsystem.core.QuizConfig
import futur.apps.composeproject1.quizsystem.core.QuizEngine
import futur.apps.composeproject1.quizsystem.ui.components.ReviewAnswer
import futur.apps.composeproject1.quizsystem.viewmodels.QuizTimer
import futur.apps.composeproject1.quizsystem.viewmodels.QuizUiState
import futur.apps.composeproject1.quizsystem.viewmodels.StatsUiState
import futur.apps.composeproject1.utils.Category
import futur.apps.composeproject1.utils.Table
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

// -------------------- Merged QuizViewModel --------------------
@HiltViewModel
class QuizViewModel @Inject constructor(
    private val preferencesManager: AppDataStore,
    private val repository: QuizRepository,
    private val adsManager: AdsManager,
) : ViewModel()
{

    // -------------------- State & Effects --------------------
    // Initialize with defaults from the spacecode QuizUiState
    private val _quizUiState = MutableStateFlow(QuizUiState())
    val quizUiState: StateFlow<QuizUiState> = _quizUiState.asStateFlow()

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
            val enableSounds = _quizUiState.value.enableSounds
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
    init {
        viewModelScope.launch {
            preferencesManager.categoryPoints.collect { savedPoints ->
                _quizUiState.update { it.copy(pointsByCategory = savedPoints) }
            }
        }

        viewModelScope.launch {
            preferencesManager.statsFlow.collect { savedStats ->
                _statsUiState.value = savedStats
            }
        }

    }
  /*  init {
        viewModelScope.launch {
            preferencesManager.categoryPoints.collect { savedPoints ->
                // your existing code
            }
        }

    }*/


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
    fun setCategory(activity: Activity?, category: Category?) {
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
    fun startNewQuiz(category: Category?) {
        stopTimer()

        viewModelScope.launch {
            // show loader and set category (may be null if caller passed null)
            _quizUiState.value = _quizUiState.value.copy(
                isLoading = true,
                category = category,
                currentIndex = 0
            )

            try {
                // fetch items for the requested category (getCategoryFlow handles null -> fallback)
                val items = getCategoryFlow(category).first()

                // convert repository items into Question list (your helper)
                var questions = generateQuestions(items)

                // apply UI preferences
                if (_quizUiState.value.shuffleQuestions) questions = questions.shuffled()
                if (_quizUiState.value.shuffleOptions) {
                    questions = questions.map { q -> q.copy(options = q.options.shuffled()) }
                }

                // limit questions
                questions = questions.take(_quizUiState.value.maxQuestions)

                // init quiz engine
                quizEngine = QuizEngine(
                    questions = questions,
                    enableNegativeScoring = _quizUiState.value.enableNegativeScoring,
                    enableSounds = _quizUiState.value.enableSounds
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
                    state.enableTTS && state.enableTTSOnTimeOut
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

        if (state.enableTTSOnTimeOut && state.enableTTS) {
            // replace with your actual string resource id for "time up"
            emitEffect(QuizUiEffect.SpeakTextRes(R.string.tts_no_answer))
        }

        emitEffect(QuizUiEffect.StopTimerSounds)
        confirmOrNext(triggeredByTimeout = true)

        // Auto-move to next question after delay if enabled
        if (state.autoNextOnTimeout) {
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
    }*/

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



    // Repository helpers: fetch flows per Category (as in your old ViewModel)
    private fun getCategoryFlow(category: Category?) = when (category) {
        Category.Verbs -> repository.getAllVerbs()
        Category.Sentences -> repository.getAllSentences()
        Category.PhrasalVerbs -> repository.getAllPhrasalVerbs()
        Category.Nouns -> repository.getAllNouns()
        Category.Adjectives -> repository.getAllAdjectives()
        Category.Adverbs -> repository.getAllAdverbs()
        Category.Idioms -> repository.getAllIdioms()
        else -> repository.getAllVerbs()
    }
    private fun <T : Table> generateQuestions(items: List<T>): List<Question> {
        return items.shuffled().take(quizUiState.value.maxQuestions).map { item ->
            val options = (items.filter { it.en != item.en }
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

    // ---------------------------------------------------------
    // 🧮 Points Utilities
    // ---------------------------------------------------------

    /** Returns the total points accumulated across all categories. */
    fun getTotalPoints(): Int {
        return _quizUiState.value.pointsByCategory.values.sum()
    }

    /** Resets all points in DataStore and UI state to zero. */
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


}
