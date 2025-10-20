package futur.apps.composeproject1.RoomDatabase.userroom

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [UserCategoryEntity::class, UserQuestionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class UserQuizDatabase : RoomDatabase() {
    abstract fun userQuizDao(): UserQuizDao
}
