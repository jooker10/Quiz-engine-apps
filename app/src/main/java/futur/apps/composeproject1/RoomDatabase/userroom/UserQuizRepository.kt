package futur.apps.composeproject1.RoomDatabase.userroom

import javax.inject.Inject

class UserQuizRepository @Inject constructor(
    private val dao: UserQuizDao
) {
    // ---------- Categories ----------
    fun getAllCategories() = dao.getAllCategories()
    fun getCategoryById(id: Int) = dao.getCategoryById(id)
    suspend fun insertCategory(category: UserCategoryEntity) = dao.insertCategory(category)
    suspend fun updateCategory(id: Int, name: String, desc: String?) =
        dao.updateCategory(id, name, desc)
    suspend fun deleteCategory(category: UserCategoryEntity) = dao.deleteCategory(category)
    suspend fun deleteAllCategories() = dao.deleteAllCategories()

    // ---------- Questions ----------
    fun getQuestionsByCategory(categoryId: Int) = dao.getQuestionsByCategory(categoryId)
    suspend fun insertQuestion(question: UserQuestionEntity) = dao.insertQuestion(question)
    suspend fun updateQuestion(id: Int, text: String, opts: List<String>, correct: String) =
        dao.updateQuestion(id, text, opts, correct)
    suspend fun deleteQuestion(question: UserQuestionEntity) = dao.deleteQuestion(question)
    suspend fun deleteAllQuestions() = dao.deleteAllQuestions()
}
