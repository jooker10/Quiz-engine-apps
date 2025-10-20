package futur.apps.composeproject1.RoomDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import futur.apps.composeproject1.RoomDatabase.Entities.PhrasalVerb
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the PhrasalVerb entity.
 */
@Dao
interface PhrasalVerbDao {

    /**
     * Retrieves all phrasal verbs from the database.
     *
     * @return A Flow emitting a list of all phrasal verbs.
     */
    @Query("SELECT  * FROM phrasal_verbs")
     fun getAll() : Flow<List<PhrasalVerb>>
}