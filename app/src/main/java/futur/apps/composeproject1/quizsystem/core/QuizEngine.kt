package futur.apps.composeproject1.quizsystem.core

import futur.apps.composeproject1.utils.Question
import futur.apps.composeproject1.R
import futur.apps.composeproject1.viewmodels.QuizUiEffect
import kotlin.collections.getOrNull

/**
 * ---------------------------------------------------------
 * QuizEngine.kt
 * ---------------------------------------------------------
 * This class is the CORE ENGINE of the Quiz System.
 *
 * Responsibilities:
 * - Keeps track of the current question
 * - Handles user option selection
 * - Validates answers and counts correct/wrong attempts
 * - Calculates final score and performance %
 * - Returns UI Effects (sounds, TTS, etc.)
 *
 * ✅ Pure Kotlin logic (no Android dependencies)
 * ✅ Easy to reuse across different apps
 * ✅ Keeps UI & logic fully separated (MVVM friendly)
 */
class QuizEngine(
    private val questions: List<Question>,   // List of quiz questions
    private val enableNegativeScoring: Boolean, // If true → wrong answers are penalized
    private val enableSounds: Boolean          // If true → play sound feedback
) {

    // -------------------- State Tracking --------------------

    var currentIndex = 0  // Current question index (mutable to track progress)
    private var correctAnswers = 0 // Number of correct answers
    private var wrongAnswers = 0   // Number of wrong answers

    private var selectedOptionText: String? = null // Stores currently selected option
    private var isAnswerChecked = false            // Prevents re-checking same question

    // -------------------- Read-Only Properties --------------------

    val currentQuestion: Question? get() = questions.getOrNull(currentIndex) // Current active question
    val totalQuestions: Int get() = questions.size                           // Total quiz size
    val hasNextQuestion: Boolean get() = currentIndex < questions.lastIndex  // Check if more questions exist

    val completionPercent: Int
        get() = if (totalQuestions > 0) (correctAnswers * 100 / totalQuestions) else 0
    // Suggested rename: quizCompletionPercent

    // -------------------- User Actions --------------------

    /**
     * Called when the user selects an option.
     * Stores the selected option text.
     */
    fun selectOption(option: String) {
        selectedOptionText = option
    }

    /**
     * Called when the user confirms their answer.
     *
     * - Checks correctness
     * - Updates counters (correct/wrong)
     * - Triggers UI effects (sound + TTS)
     *
     * @return List of [QuizUiEffect] to be handled by the UI
     */
    fun checkAnswer(): List<QuizUiEffect> {
        val isCorrect = selectedOptionText == currentQuestion?.correctAnswer
        isAnswerChecked = true

        // Update counters
        if (isCorrect) {
            correctAnswers++
        } else if (enableNegativeScoring) {
            wrongAnswers++
        }

        val effects = mutableListOf<QuizUiEffect>()

        // Sound feedback
        if (enableSounds) {
            effects.add(if (isCorrect) QuizUiEffect.PlayCorrectSound else QuizUiEffect.PlayWrongSound)
        }

        // TTS feedback (always triggered)
        val ttsText = if (isCorrect) R.string.tts_correct_answer else R.string.tts_wrong_answer
        effects.add(QuizUiEffect.SpeakTextRes(ttsText))

        return effects
    }

    /**
     * Moves to the next question (if available).
     *
     * @return true if moved successfully, false if quiz is finished.
     */
    fun goToNextQuestion(): Boolean {
        return if (hasNextQuestion) {
            currentIndex++
            selectedOptionText = null
            isAnswerChecked = false
            true
        } else {
            false
        }
    }

    /**
     * Returns the final quiz results once the quiz is finished.
     *
     * @return [QuizResults] containing correct answers, wrong answers, points, and completion %
     */
    fun getResults(): QuizResults {
        return QuizResults(
            correctAnswers = correctAnswers,
            wrongAnswers = wrongAnswers,
            points = calculatePoints(),
            completionPercent = completionPercent
        )
    }

    // -------------------- Scoring Logic --------------------

    /**
     * Calculates score points based on completion percentage.
     * Uses thresholds from [AppConfig].
     */
    private fun calculatePoints(): Int {
        return when {
            completionPercent == AppConfig.PERFECT_PERCENTAGE.toInt() -> AppConfig.POINTS_PERFECT_SCORE
            completionPercent >= AppConfig.GOOD_PERCENTAGE.toInt() -> AppConfig.POINTS_GOOD_SCORE
            completionPercent >= AppConfig.PASSING_PERCENTAGE.toInt() -> AppConfig.POINTS_PASSING_SCORE
            else -> AppConfig.POINTS_FAILING_SCORE
        }
    }
}

/**
 * ---------------------------------------------------------
 * QuizResults.kt
 * ---------------------------------------------------------
 * Data class that stores the final quiz results.
 *
 * Includes:
 * - Number of correct answers
 * - Number of wrong answers
 * - Final score (points)
 * - Completion percentage
 */
data class QuizResults(
    val correctAnswers: Int,    // Total correct answers
    val wrongAnswers: Int,      // Total wrong answers
    val points: Int,            // Final score points
    val completionPercent: Int  // Percentage of correct answers
)
