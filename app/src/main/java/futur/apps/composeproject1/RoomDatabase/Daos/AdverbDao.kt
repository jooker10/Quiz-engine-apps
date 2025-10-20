package futur.apps.composeproject1.RoomDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import futur.apps.composeproject1.RoomDatabase.Entities.Adverb
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Adverb entity.
 */
@Dao
interface AdverbDao {

    /**
     * Retrieves all adverbs from the database.
     *
     * @return A Flow emitting a list of all adverbs.
     */
    @Query("SELECT  * FROM adverbs")
     fun getAll() : Flow<List<Adverb>>
}