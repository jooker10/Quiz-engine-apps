package futur.apps.composeproject1.RoomDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import futur.apps.composeproject1.RoomDatabase.Entities.Noun
import futur.apps.composeproject1.RoomDatabase.Entities.Verb
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Noun entity.
 */
@Dao
interface NounDao {

    /**
     * Retrieves all nouns from the database.
     *
     * @return A Flow emitting a list of all nouns.
     */
    @Query("SELECT  * FROM nouns")
     fun getAll() : Flow<List<Noun>>
}