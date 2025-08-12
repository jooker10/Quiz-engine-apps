package futur.apps.composeproject1.RoomDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import futur.apps.composeproject1.RoomDatabase.Entities.PhrasalVerb
import futur.apps.composeproject1.RoomDatabase.Entities.Verb
import kotlinx.coroutines.flow.Flow

@Dao
interface PhrasalVerbDao {

    @Query("SELECT  * FROM phrasal_verbs")
     fun getAll() : Flow<List<PhrasalVerb>>
}