package futur.apps.composeproject1.viewmodels

import futur.apps.composeproject1.quizsystem.core.QuizConfig
import futur.apps.composeproject1.quizsystem.ui.components.ReviewAnswer
import futur.apps.composeproject1.utils.BuildInCategory
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
 * - Initialized with defaults from [futur.apps.composeproject1.quizsystem.core.QuizConfig].
 * - Updated only by [QuizViewModel].
 * - Pure data holder (no business logic here).
 *
 * 🔹 Buyers Notes:
 * - All quiz behavior (timer, scoring, sounds, shuffle, etc.)
 *   can be enabled/disabled here or via [futur.apps.composeproject1.quizsystem.core.QuizConfig].
 * - Use this file to **customize default UI state** on app start.
 * ============================================================
 */


data class QuizUiState(

    // ---------------------------------------------------------
    // 📘 BuildInCategory & General Data
    // ---------------------------------------------------------
    val category: QuizCategory? = null,                       // Active quiz category
    val pointsByCategory: Map<BuildInCategory, Int> = emptyMap(), // Points per category

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
    val soundCorrect: Int = QuizConfig.SOUND_CORRECT,
    val soundWrong: Int = QuizConfig.SOUND_WRONG,
    val soundTimerTick: Int = QuizConfig.SOUND_TIMER_TICK,
    val soundTimerUrgent: Int = QuizConfig.SOUND_TIMER_URGENT,
    val enableTTSOnTimeOut: Boolean = QuizConfig.ENABLE_TTS_ON_TIMEOUT,

    // ---------------------------------------------------------
    // ❓ Question Behavior
    // ---------------------------------------------------------
    val shuffleQuestions: Boolean = QuizConfig.SHUFFLE_QUESTIONS,
    val shuffleOptions: Boolean = QuizConfig.SHUFFLE_OPTIONS,

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

