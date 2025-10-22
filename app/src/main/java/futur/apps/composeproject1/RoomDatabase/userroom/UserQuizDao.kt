package futur.apps.composeproject1.RoomDatabase.userroom


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserQuizDao {

    // --- Categories ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: UserCategoryEntity): Long

    @Query("SELECT * FROM user_categories")
    fun getAllCategories(): Flow<List<UserCategoryEntity>>

    @Delete
    suspend fun deleteCategory(category: UserCategoryEntity)

    // --- Questions ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: UserQuestionEntity)

    @Query("SELECT * FROM user_questions WHERE category_id = :categoryId")
    fun getQuestionsByCategory(categoryId: Int): Flow<List<UserQuestionEntity>>

    @Delete
    suspend fun deleteQuestion(question: UserQuestionEntity)

    @Query("DELETE FROM user_categories")
    suspend fun deleteAllCategories()

    @Query("DELETE FROM user_questions")
    suspend fun deleteAllQuestions()

}
