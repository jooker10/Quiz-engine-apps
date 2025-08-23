package futur.apps.composeproject1.RoomDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import futur.apps.composeproject1.RoomDatabase.Entities.Adjective
import futur.apps.composeproject1.RoomDatabase.Entities.Verb
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Adjective entity.
 */
@Dao
interface AdjectiveDao {

    /**
     * Retrieves all adjectives from the database.
     *
     * @return A Flow emitting a list of all adjectives.
     */
    @Query("SELECT  * FROM adjectives")
     fun getAll() : Flow<List<Adjective>>
}