package futur.apps.composeproject1.RoomDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import futur.apps.composeproject1.RoomDatabase.Entities.Adjective
import futur.apps.composeproject1.RoomDatabase.Entities.Verb
import kotlinx.coroutines.flow.Flow

@Dao
interface AdjectiveDao {

    @Query("SELECT  * FROM adjectives")
     fun getAll() : Flow<List<Adjective>>
}