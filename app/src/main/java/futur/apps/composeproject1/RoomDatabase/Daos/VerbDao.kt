package futur.apps.composeproject1.RoomDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import futur.apps.composeproject1.RoomDatabase.Entities.Verb
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Verb entity.
 */
@Dao
interface VerbDao {

    /**
     * Retrieves all verbs from the database.
     *
     * @return A Flow emitting a list of all verbs.
     */
    @Query("SELECT  * FROM verbs")
     fun getAll() : Flow<List<Verb>>
}