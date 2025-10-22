package futur.apps.composeproject1.RoomDatabase.userroom

import javax.inject.Inject

class UserQuizRepository @Inject constructor(
    private val dao: UserQuizDao
) {

    fun getAllCategories() = dao.getAllCategories()

    fun getQuestionsByCategory(categoryId: Int) = dao.getQuestionsByCategory(categoryId)

    suspend fun insertCategory(category: UserCategoryEntity): Long = dao.insertCategory(category)

    suspend fun insertQuestion(question: UserQuestionEntity) = dao.insertQuestion(question)

    suspend fun deleteCategory(category: UserCategoryEntity) = dao.deleteCategory(category)

    suspend fun deleteQuestion(question: UserQuestionEntity) = dao.deleteQuestion(question)

    suspend fun deleteAllCategories() = dao.deleteAllCategories()

    suspend fun deleteAllQuestions() = dao.deleteAllQuestions()

}
