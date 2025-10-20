package futur.apps.composeproject1.RoomDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import futur.apps.composeproject1.RoomDatabase.Entities.Idiom
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Idiom entity.
 */
@Dao
interface IdiomDao {

    /**
     * Retrieves all idioms from the database.
     *
     * @return A Flow emitting a list of all idioms.
     */
    @Query("SELECT  * FROM idioms")
     fun getAll() : Flow<List<Idiom>>
}