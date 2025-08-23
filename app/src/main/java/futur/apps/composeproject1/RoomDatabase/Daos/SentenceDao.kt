package futur.apps.composeproject1.RoomDatabase.Daos

import androidx.room.Dao
import androidx.room.Query
import futur.apps.composeproject1.RoomDatabase.Entities.Idiom
import futur.apps.composeproject1.RoomDatabase.Entities.Sentence
import futur.apps.composeproject1.RoomDatabase.Entities.Verb
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the Sentence entity.
 */
@Dao
interface SentenceDao {

    /**
     * Retrieves all sentences from the database.
     *
     * @return A Flow emitting a list of all sentences.
     */
    @Query("SELECT  * FROM sentences")
     fun getAll() : Flow<List<Sentence>>
}