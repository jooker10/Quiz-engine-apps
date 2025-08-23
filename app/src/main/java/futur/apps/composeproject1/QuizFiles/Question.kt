package futur.apps.composeproject1.QuizFiles

/**
 * Represents a quiz question.
 *
 * @param questionText The text of the question.
 * @param options A list of possible answers to the question.
 * @param correctAnswer The correct answer to the question.
 */
data class Question(
    val questionText : String,
    val options : List<String>,
    val correctAnswer : String
)
