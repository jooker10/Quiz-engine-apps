package futur.apps.composeproject1.RoomDatabase.userroom

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserQuizDao {

    // ---------- Categories ----------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: UserCategoryEntity): Long

    @Query("SELECT * FROM user_categories ORDER BY category_name ASC")
    fun getAllCategories(): Flow<List<UserCategoryEntity>>

    @Query("SELECT * FROM user_categories WHERE id = :categoryId LIMIT 1")
    fun getCategoryById(categoryId: Int): Flow<UserCategoryEntity?>

    @Query("""
        UPDATE user_categories 
        SET category_name = :newName, description = :newDesc 
        WHERE id = :categoryId
    """)
    suspend fun updateCategory(categoryId: Int, newName: String, newDesc: String?)

    @Delete
    suspend fun deleteCategory(category: UserCategoryEntity)

    @Query("DELETE FROM user_categories")
    suspend fun deleteAllCategories()


    // ---------- Questions ----------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: UserQuestionEntity): Long

    @Query("SELECT * FROM user_questions WHERE category_id = :categoryId")
    fun getQuestionsByCategory(categoryId: Int): Flow<List<UserQuestionEntity>>

    @Query("""
        UPDATE user_questions 
        SET question_text = :newText,
            options = :newOptions,
            correct_answer = :newCorrect
        WHERE id = :questionId
    """)
    suspend fun updateQuestion(
        questionId: Int,
        newText: String,
        newOptions: List<String>,
        newCorrect: String
    )

    @Delete
    suspend fun deleteQuestion(question: UserQuestionEntity)

    @Query("DELETE FROM user_questions")
    suspend fun deleteAllQuestions()
}
