package futur.apps.composeproject1.quiz.data

import futur.apps.composeproject1.utils.Question

/**
 * ------------------------------------------------------------
 * 🎯 QuizDataSource
 * ------------------------------------------------------------
 * A unified abstraction for loading quiz data from any source:
 * - Default (Room entities or assets)
 * - Custom (User room tables)
 *
 * Each implementation defines how to:
 * - Retrieve available categories
 * - Retrieve questions for a given category
 * ------------------------------------------------------------
 */
interface QuizDataSource {
    suspend fun getCategories(): List<String>
    suspend fun getQuestions(categoryName: String): List<Question>
}
