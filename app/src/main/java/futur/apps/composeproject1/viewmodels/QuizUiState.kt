package futur.apps.composeproject1.viewmodels

import futur.apps.composeproject1.quiz.core.AppConfig
import futur.apps.composeproject1.quiz.ui.components.ReviewAnswer
import futur.apps.composeproject1.utils.Question
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
 * - Initialized with defaults from [futur.apps.composeproject1.quiz.core.AppConfig].
 * - Updated only by [QuizViewModel].
 * - Pure data holder (no business logic here).
 *
 * 🔹 Buyers Notes:
 * - All quiz behavior (timer, scoring, sounds, shuffle, etc.)
 *   can be enabled/disabled here or via [futur.apps.composeproject1.quiz.core.AppConfig].
 * - Use this file to **customize default UI state** on app start.
 * ============================================================
 */


data class QuizUiState(

    // ---------------------------------------------------------
    // 📘 DefaultCategory & General Data
    // ---------------------------------------------------------
    val category: QuizCategory? = null,                       // Active quiz category
    val pointsByCategory: Map<Any, Int> = emptyMap(),


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
    val enableNegativeScoring: Boolean = AppConfig.ENABLE_NEGATIVE_SCORING,

    // ---------------------------------------------------------
    // ⏱️ Timer
    // ---------------------------------------------------------
    val timeLeft: Int = AppConfig.QUESTION_TIME_LIMIT,     // Current countdown value
    val maxTime: Int = AppConfig.QUESTION_TIME_LIMIT,      // Initial countdown value
    val showTimer: Boolean = AppConfig.SHOW_TIMER,         // Show/hide timer
    val timerTickInterval: Long = AppConfig.TIMER_TICK_INTERVAL,
    val autoNextDelay: Long = AppConfig.AUTO_NEXT_DELAY,             // Delay before auto-move
    val timerCriticalThreshold: Int = AppConfig.TIMER_CRITICAL_THRESHOLD, // Red alert threshold

    // ---------------------------------------------------------
    // 🎨 UI Options
    // ---------------------------------------------------------
    val showProgressBar: Boolean = AppConfig.SHOW_PROGRESS_BAR,
    val showQuestionIndex: Boolean = AppConfig.SHOW_QUESTION_INDEX,
    val enableReviewScreen: Boolean = AppConfig.ENABLE_REVIEW_SCREEN,

    // ---------------------------------------------------------
    // 🔊 Sounds & Effects
    // ---------------------------------------------------------
    val soundCorrect: Int = AppConfig.SOUND_CORRECT,
    val soundWrong: Int = AppConfig.SOUND_WRONG,
    val soundTimerTick: Int = AppConfig.SOUND_TIMER_TICK,
    val soundTimerUrgent: Int = AppConfig.SOUND_TIMER_URGENT,
    val enableTTSOnTimeOut: Boolean = AppConfig.ENABLE_TTS_ON_TIMEOUT,

    // ---------------------------------------------------------
    // ❓ Question Behavior
    // ---------------------------------------------------------
    val shuffleQuestions: Boolean = AppConfig.SHUFFLE_QUESTIONS,
    val shuffleOptions: Boolean = AppConfig.SHUFFLE_OPTIONS,

    // ---------------------------------------------------------
    // 📊 Result Screen Config
    // ---------------------------------------------------------
    val resultStatsConfig: List<AppConfig.StatItemConfig> = AppConfig.resultStatsConfig,
    val resultActionsConfig: List<AppConfig.ActionItemConfig> = AppConfig.resultActionsConfig,

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
        get() = if (AppConfig.TTS_USE_DEVICE_LANGUAGE)
            Locale.getDefault()
        else
            AppConfig.TTS_FIXED_LANGUAGE
}

