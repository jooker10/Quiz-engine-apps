package futur.apps.composeproject1.quizsystem.viewmodels

import futur.apps.composeproject1.utils.Question
import futur.apps.composeproject1.quizsystem.ui.components.ReviewAnswer
import futur.apps.composeproject1.quizsystem.core.QuizConfig
import futur.apps.composeproject1.quizsystem.data.demo.Data
import futur.apps.composeproject1.utils.Category
import java.util.Locale

/**
 * ============================================================
 * QuizUiState.kt
 *
 * 🔹 Purpose:
 * Immutable state holder for the Quiz Screen.
 *
 * - Acts as the SINGLE SOURCE OF TRUTH for the UI.
 * - Contains quiz progress, scoring, timer, UI options, sounds, etc.
 * - Initialized with defaults from [QuizConfig].
 * - Updated only by [QuizViewModel].
 * - Pure data holder (no business logic here).
 *
 * 🔹 Buyers Notes:
 * - All quiz behavior (timer, scoring, sounds, shuffle, etc.)
 *   can be enabled/disabled here or via [QuizConfig].
 * - Use this file to **customize default UI state** on app start.
 * ============================================================
 */


data class QuizUiState(

    // ---------------------------------------------------------
    // 📘 Category & General Data
    // ---------------------------------------------------------
    val category: Category? = null,                       // Active quiz category
    val pointsByCategory: Map<Category, Int> = emptyMap(), // Points per category

    // ---------------------------------------------------------
    // 📌 Core Quiz Progress
    // ---------------------------------------------------------
    val questions: List<Question> = emptyList(),           // All loaded quiz questions
    val reviewAnswers: List<ReviewAnswer> = emptyList(),   // Answers for review screen
    val currentIndex: Int = 0,                             // Current question index
    val correctScore: Int = 0,                             // Number of correct answers
    val wrongScore: Int = 0,                               // Number of wrong answers
    val selectedOptionText: String? = null,                // Option chosen by user
    val isAnswerChecked: Boolean = false,                  // Has user confirmed answer?
    val isFinished: Boolean = false,                       // Is quiz completed?
    val isProcessing: Boolean = false,                     // Prevents multiple clicks

    // ---------------------------------------------------------
    // 🏆 Scoring
    // ---------------------------------------------------------
    val earnedPoints: Int = 0,                             // Points earned so far
    val completionPercent: Int = 0,                        // % progress (0–100)
    val enableNegativeScoring: Boolean = QuizConfig.ENABLE_NEGATIVE_SCORING,

    // ---------------------------------------------------------
    // ⏱️ Timer
    // ---------------------------------------------------------
    val timeLeft: Int = QuizConfig.QUESTION_TIME_LIMIT,     // Current countdown value
    val maxTime: Int = QuizConfig.QUESTION_TIME_LIMIT,      // Initial countdown value
    val showTimer: Boolean = QuizConfig.SHOW_TIMER,         // Show/hide timer
    val timerTickInterval: Long = QuizConfig.TIMER_TICK_INTERVAL,
    val autoNextOnTimeout: Boolean = QuizConfig.AUTO_NEXT_ON_TIMEOUT, // Auto-move after timeout
    val autoNextDelay: Long = QuizConfig.AUTO_NEXT_DELAY,             // Delay before auto-move
    val timerCriticalThreshold: Int = QuizConfig.TIMER_CRITICAL_THRESHOLD, // Red alert threshold

    // ---------------------------------------------------------
    // 🎨 UI Options
    // ---------------------------------------------------------
    val showProgressBar: Boolean = QuizConfig.SHOW_PROGRESS_BAR,
    val showQuestionIndex: Boolean = QuizConfig.SHOW_QUESTION_INDEX,
    val enableReviewScreen: Boolean = QuizConfig.ENABLE_REVIEW_SCREEN,

    // ---------------------------------------------------------
    // 🔊 Sounds & Effects
    // ---------------------------------------------------------
    val enableSounds: Boolean = QuizConfig.ENABLE_SOUNDS,
    val soundCorrect: Int = QuizConfig.SOUND_CORRECT,
    val soundWrong: Int = QuizConfig.SOUND_WRONG,
    val soundTimerTick: Int = QuizConfig.SOUND_TIMER_TICK,
    val soundTimerUrgent: Int = QuizConfig.SOUND_TIMER_URGENT,
    val enableTTS: Boolean = QuizConfig.ENABLE_TTS,
    val enableTTSOnTimeOut: Boolean = QuizConfig.ENABLE_TTS_ON_TIMEOUT,

    // ---------------------------------------------------------
    // ❓ Question Behavior
    // ---------------------------------------------------------
    val shuffleQuestions: Boolean = QuizConfig.SHUFFLE_QUESTIONS,
    val shuffleOptions: Boolean = QuizConfig.SHUFFLE_OPTIONS,
    val maxQuestions: Int = QuizConfig.MAX_QUESTIONS_PER_QUIZ,

    // ---------------------------------------------------------
    // 📊 Result Screen Config
    // ---------------------------------------------------------
    val resultStatsConfig: List<QuizConfig.StatItemConfig> = QuizConfig.resultStatsConfig,
    val resultActionsConfig: List<QuizConfig.ActionItemConfig> = QuizConfig.resultActionsConfig,

    // ---------------------------------------------------------
    // ⏳ Loading & Error
    // ---------------------------------------------------------
    val isLoading: Boolean = true,        // Keep true as in old version (better UX)
    val error: String? = null             // Error message
) {

    // ---------------------------------------------------------
    // 🔹 Derived Properties (auto-calculated)
    // ---------------------------------------------------------
    val totalQuestions: Int get() = questions.size
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val isLastQuestion: Boolean get() = currentIndex == questions.lastIndex

    // ---------------------------------------------------------
    // 🌍 TTS Language
    // ---------------------------------------------------------
    val ttsLanguage: Locale
        get() = if (QuizConfig.TTS_USE_DEVICE_LANGUAGE)
            Locale.getDefault()
        else
            QuizConfig.TTS_FIXED_LANGUAGE
}

/*
data class QuizUiState(

    // ---------------------------------------------------------
    // 📌 Core Quiz Progress
    // ---------------------------------------------------------
    val questions: List<Question> = emptyList(),     // All loaded quiz questions
    val reviewAnswers: List<ReviewAnswer> = emptyList(), // Stores answers for review screen
    val currentIndex: Int = 0,                       // Current question index
    val correctAnswers: Int = 0,                     // Number of correct answers
    val wrongAnswers: Int = 0,                       // Number of wrong answers
    val selectedOptionText: String? = null,          // The option chosen by user
    val isAnswerChecked: Boolean = false,            // Has user confirmed answer?
    val isFinished: Boolean = false,                 // Is quiz completed?

    */
/** Prevents multiple button clicks / double-submit *//*

    val isProcessing: Boolean = false,

    // ---------------------------------------------------------
    // 🏆 Scoring
    // ---------------------------------------------------------
    val earnedPoints: Int = 0,                       // Points earned so far
    val completionPercent: Int = 0,                  // % progress (0–100)
    val enableNegativeScoring: Boolean = QuizConfig.ENABLE_NEGATIVE_SCORING,

    // ---------------------------------------------------------
    // ⏱️ Timer
    // ---------------------------------------------------------
    val timeLeft: Int = QuizConfig.QUESTION_TIME_LIMIT,   // Current countdown value
    val maxTime: Int = QuizConfig.QUESTION_TIME_LIMIT,    // Initial countdown value
    val showTimer: Boolean = QuizConfig.SHOW_TIMER,       // Show/hide timer
    val timerTickInterval: Long = QuizConfig.TIMER_TICK_INTERVAL,
    val autoNextOnTimeout: Boolean = QuizConfig.AUTO_NEXT_ON_TIMEOUT, // Auto-move after timeout
    val autoNextDelay: Long = QuizConfig.AUTO_NEXT_DELAY,             // Delay before auto-move
    val timerCriticalThreshold: Int = QuizConfig.TIMER_CRITICAL_THRESHOLD, // Red alert threshold

    // ---------------------------------------------------------
    // 🎨 UI Options
    // ---------------------------------------------------------
    val showProgressBar: Boolean = QuizConfig.SHOW_PROGRESS_BAR,  // Show progress bar
    val showQuestionIndex: Boolean = QuizConfig.SHOW_QUESTION_INDEX, // Show "Q x of y"
    val enableReviewScreen: Boolean = QuizConfig.ENABLE_REVIEW_SCREEN, // Enable results review

    // ---------------------------------------------------------
    // 🔊 Sounds & Effects
    // ---------------------------------------------------------
    val enableSounds: Boolean = QuizConfig.ENABLE_SOUNDS, // Master switch for sounds
    val soundCorrect: Int = QuizConfig.SOUND_CORRECT,
    val soundWrong: Int = QuizConfig.SOUND_WRONG,
    val soundTimerTick: Int = QuizConfig.SOUND_TIMER_TICK,
    val soundTimerUrgent: Int = QuizConfig.SOUND_TIMER_URGENT,
    val enableTTS: Boolean = QuizConfig.ENABLE_TTS,       // Enable Text-to-Speech
    val enableTTSOnTimeOut: Boolean = QuizConfig.ENABLE_TTS_ON_TIMEOUT, // "No Answer" voice alert

    // ---------------------------------------------------------
    // ❓ Question Behavior
    // ---------------------------------------------------------
    val shuffleQuestions: Boolean = QuizConfig.SHUFFLE_QUESTIONS, // Randomize order of questions
    val shuffleOptions: Boolean = QuizConfig.SHUFFLE_OPTIONS,     // Randomize options inside a question
    val maxQuestions: Int = QuizConfig.MAX_QUESTIONS_PER_QUIZ,    // Limit max questions per session

    // ---------------------------------------------------------
    // 📊 Result Screen Config
    // ---------------------------------------------------------
    val resultStatsConfig: List<QuizConfig.StatItemConfig> = QuizConfig.resultStatsConfig,
    val resultActionsConfig: List<QuizConfig.ActionItemConfig> = QuizConfig.resultActionsConfig,

    // ---------------------------------------------------------
    // ⏳ Loading & Error
    // ---------------------------------------------------------
    val isLoading: Boolean = false,       // Show loading UI if fetching data
    val error: String? = null,            // Error message if something goes wrong
) {

    // ---------------------------------------------------------
    // 🔹 Derived Properties (auto-calculated)
    // ---------------------------------------------------------
    val totalQuestions: Int get() = questions.size   // Total questions loaded
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex) // Current active question
    val isLastQuestion: Boolean get() = currentIndex == questions.lastIndex  // Is this the last one?

    */
/**
     * 🔊 TTS Language selection:
     * - If TTS_USE_DEVICE_LANGUAGE = true → use device default language.
     * - Otherwise → force language defined in [QuizConfig.TTS_FIXED_LANGUAGE].
     *//*

    val ttsLanguage: Locale
        get() = if (QuizConfig.TTS_USE_DEVICE_LANGUAGE)
            Locale.getDefault()
        else
            QuizConfig.TTS_FIXED_LANGUAGE
}
*/
