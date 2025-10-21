package futur.apps.composeproject1.quizsystem.data

import futur.apps.composeproject1.utils.Question

/**
 * ------------------------------------------------------------
 * 🎯 QuizDataSource
 * ------------------------------------------------------------
 * A unified abstraction for loading quiz data from any source:
 * - BuiltIn (Room entities or assets)
 * - UserCreated (User room tables)
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
