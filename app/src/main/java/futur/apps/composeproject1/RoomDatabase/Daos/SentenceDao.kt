package futur.apps.composeproject1.RoomDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import futur.apps.composeproject1.RoomDatabase.Entities.Idiom
import futur.apps.composeproject1.RoomDatabase.Entities.Sentence
import futur.apps.composeproject1.RoomDatabase.Entities.Verb
import kotlinx.coroutines.flow.Flow

@Dao
interface SentenceDao {

    @Query("SELECT  * FROM sentences")
     fun getAll() : Flow<List<Sentence>>
}