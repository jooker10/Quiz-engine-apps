package futur.apps.composeproject1.RoomDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import futur.apps.composeproject1.RoomDatabase.Entities.Idiom
import futur.apps.composeproject1.RoomDatabase.Entities.Verb
import kotlinx.coroutines.flow.Flow

@Dao
interface IdiomDao {

    @Query("SELECT  * FROM idioms")
     fun getAll() : Flow<List<Idiom>>
}