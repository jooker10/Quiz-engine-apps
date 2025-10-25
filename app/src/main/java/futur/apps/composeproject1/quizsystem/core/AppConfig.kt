package futur.apps.composeproject1.quizsystem.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import futur.apps.composeproject1.R
import java.util.Locale

/**
 * ============================================================
 * AppConfig.kt
 *
 * 🔹 Central configuration object for the Quiz App.
 *
 * Buyer Notes:
 * - All quiz-related settings are centralized here.
 * - You can control timer, scoring, UI, sounds, text-to-speech,
 *   question behavior, and result screen options.
 * - Change values here → the whole app will update automatically.
 * - Texts use strings.xml → Full multi-language support.
 * ============================================================
 */
object AppConfig {

    // Auth and Storage

    /** 🔌 Toggle Firestore integration globally */
    const val USE_FIRESTORE_SYNC = true

    /** 🔧 Optional: toggle Google Sign-In (if you want to make it optional too) */
    const val USE_GOOGLE_SIGN_IN = true

    // ---------------------------------------------------------
    // ⏱️ TIMER SETTINGS
    // ---------------------------------------------------------

    const val CHOICE_COUNT: Int = 4

    /** Default time per question (in seconds) */
    const val QUESTION_TIME_LIMIT: Int = 15

    /** Show countdown timer on the quiz screen */
    const val SHOW_TIMER: Boolean = true

    /** Interval between timer updates (in ms) */
    const val TIMER_TICK_INTERVAL: Long = 1000

    /** Automatically move to next question when time runs out */
    const val AUTO_NEXT_ON_TIMEOUT: Boolean = true

    /** Delay before auto-moving to next question after timeout (ms) */
    const val AUTO_NEXT_DELAY: Long = 2000L

    /** Timer text turns red when below this threshold (seconds) */
    const val TIMER_CRITICAL_THRESHOLD: Int = 5

    // ---------------------------------------------------------
    // 🏆 SCORING SYSTEM
    // ---------------------------------------------------------

    /** Points for different outcomes */
    const val POINTS_PERFECT_SCORE: Int = 50
    const val POINTS_GOOD_SCORE: Int = 20
    const val POINTS_PASSING_SCORE: Int = 10
    const val POINTS_FAILING_SCORE: Int = 0

    /** Enable negative scoring for wrong answers (set false to disable) */
    const val ENABLE_NEGATIVE_SCORING: Boolean = true

    // ---------------------------------------------------------
    // 🎨 UI SETTINGS
    // ---------------------------------------------------------

    /** Show progress bar above questions */
    const val SHOW_PROGRESS_BAR: Boolean = true

    /** Show question number (e.g., 3/10) */
    const val SHOW_QUESTION_INDEX: Boolean = true

    /** Enable review screen at the end of quiz */
    const val ENABLE_REVIEW_SCREEN: Boolean = true

    // ---------------------------------------------------------
    // 🔊 SOUND EFFECTS
    // ---------------------------------------------------------

    /** Master switch for sound effects */
    const val ENABLE_SOUNDS: Boolean = true

    /** Sound resources (replace with your own audio if you like) */
    val SOUND_CORRECT = R.raw.correct_sound  // Played on correct answer
    val SOUND_WRONG = R.raw.wrong_sound      // Played on wrong answer
    val SOUND_TIMER_TICK = R.raw.timer_tick  // Normal countdown tick
    val SOUND_TIMER_URGENT = R.raw.timer_urgent // Urgent tick (low time)

    // ---------------------------------------------------------
    // 🗣️ TEXT TO SPEECH (TTS)
    // ---------------------------------------------------------

    /** Enable Text-to-Speech support */
    const val ENABLE_TTS: Boolean = true

    /** Use device language for TTS */
    const val TTS_USE_DEVICE_LANGUAGE: Boolean = true

    /** If not using device language, force a fixed language */
    val TTS_FIXED_LANGUAGE: Locale = Locale.US

    /** Announce "no answer selected" when time runs out */
    const val ENABLE_TTS_ON_TIMEOUT: Boolean = true

    // ---------------------------------------------------------
    // ❓ QUESTION BEHAVIOR
    // ---------------------------------------------------------

    /** Maximum number of questions in one quiz session */
    const val MAX_QUESTIONS_PER_QUIZ: Int = 10

    /** Randomize order of questions */
    const val SHUFFLE_QUESTIONS: Boolean = true

    /** Randomize order of answer options */
    const val SHUFFLE_OPTIONS: Boolean = true

    // ---------------------------------------------------------
    // 📊 RESULT SCREEN CONFIG
    // ---------------------------------------------------------

    /** Score percentage thresholds */
    const val PERFECT_PERCENTAGE: Float = 100f
    const val GOOD_PERCENTAGE: Float = 75f
    const val PASSING_PERCENTAGE: Float = 50f

    /**
     * Returns localized string resource ID for result messages.
     * - Buyers can change strings in strings.xml (multi-language ready).
     */
    fun getResultMessageRes(scorePercentage: Float): Int {
        return when {
            scorePercentage >= PERFECT_PERCENTAGE -> R.string.result_message_perfect
            scorePercentage >= GOOD_PERCENTAGE -> R.string.result_message_excellent
            scorePercentage >= PASSING_PERCENTAGE -> R.string.result_message_good_try
            else -> R.string.result_message_keep_practicing
        }
    }

    // ---------------------------------------------------------
    // 📈 STATS CONFIG (Displayed in Result Screen)
    // ---------------------------------------------------------

    /** Represents one statistic item */
    data class StatItemConfig(
        val icon: ImageVector,   // Icon shown in result stats
        val labelRes: Int,       // Text label from strings.xml
        val useDynamicColor: Boolean = false // Highlight this stat?
    )

    /** List of stats displayed on result screen */
    val resultStatsConfig = listOf(
        StatItemConfig(Icons.Default.List, R.string.stat_total_questions),
        StatItemConfig(Icons.Default.CheckCircle, R.string.stat_correct),
        StatItemConfig(Icons.Default.Star, R.string.stat_points, useDynamicColor = true)
    )

    // ---------------------------------------------------------
    // 🎬 ACTION BUTTONS CONFIG (Result Screen)
    // ---------------------------------------------------------

    /** Represents one action button */
    data class ActionItemConfig(
        val icon: ImageVector, // Button icon
        val labelRes: Int      // Button text from strings.xml
    )

    /** Buttons displayed on result screen */
    val resultActionsConfig = listOf(
        ActionItemConfig(Icons.Default.Refresh, R.string.action_retry), // Retry quiz
        ActionItemConfig(Icons.Default.Home, R.string.action_home),     // Go home
        ActionItemConfig(Icons.Default.Share, R.string.action_share)    // Share results
    )
}
