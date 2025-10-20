package futur.apps.composeproject1.RoomDatabase.userroom


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.ColumnInfo

@Entity(tableName = "user_categories")
data class UserCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "category_name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String? = null
)

@Entity(
    tableName = "user_questions",
    foreignKeys = [
        ForeignKey(
            entity = UserCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "category_id", index = true)
    val categoryId: Int,

    @ColumnInfo(name = "question_text")
    val questionText: String,

    @ColumnInfo(name = "options")
    val options: List<String>,

    @ColumnInfo(name = "correct_answer")
    val correctAnswer: String
)
