package futur.apps.composeproject1.RoomDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import futur.apps.composeproject1.RoomDatabase.Entities.Noun
import futur.apps.composeproject1.RoomDatabase.Entities.Verb
import kotlinx.coroutines.flow.Flow

@Dao
interface NounDao {

    @Query("SELECT  * FROM nouns")
     fun getAll() : Flow<List<Noun>>
}