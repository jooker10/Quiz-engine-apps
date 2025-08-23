package futur.apps.composeproject1.RoomDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import futur.apps.composeproject1.RoomDatabase.Daos.AdjectiveDao
import futur.apps.composeproject1.RoomDatabase.Daos.AdverbDao
import futur.apps.composeproject1.RoomDatabase.Daos.IdiomDao
import futur.apps.composeproject1.RoomDatabase.Daos.NounDao
import futur.apps.composeproject1.RoomDatabase.Daos.PhrasalVerbDao
import futur.apps.composeproject1.RoomDatabase.Daos.SentenceDao
import futur.apps.composeproject1.RoomDatabase.Daos.VerbDao
import futur.apps.composeproject1.RoomDatabase.Entities.Adjective
import futur.apps.composeproject1.RoomDatabase.Entities.Adverb
import futur.apps.composeproject1.RoomDatabase.Entities.Idiom
import futur.apps.composeproject1.RoomDatabase.Entities.Noun
import futur.apps.composeproject1.RoomDatabase.Entities.PhrasalVerb
import futur.apps.composeproject1.RoomDatabase.Entities.Sentence
import futur.apps.composeproject1.RoomDatabase.Entities.Verb

/**
 * Main database class for the application.
 *
 * This class defines the database configuration and provides access to the DAOs.
 */
@Database(entities = [Verb::class, Sentence ::class, PhrasalVerb::class, Noun::class, Adjective::class, Adverb::class, Idiom::class], version = 1, exportSchema = false )
abstract class MyDataBase  : RoomDatabase() {

    /**
     * Returns the DAO for [Verb] entities.
     */
    abstract fun verbDao() : VerbDao

    /**
     * Returns the DAO for [Sentence] entities.
     */
    abstract fun sentenceDao() : SentenceDao

    /**
     * Returns the DAO for [PhrasalVerb] entities.
     */
    abstract fun phrasalVerbDao() : PhrasalVerbDao

    /**
     * Returns the DAO for [Noun] entities.
     */
    abstract fun nounDao() : NounDao

    /**
     * Returns the DAO for [Adjective] entities.
     */
    abstract fun adjectiveDao() : AdjectiveDao

    /**
     * Returns the DAO for [Adverb] entities.
     */
    abstract fun adverbDao() : AdverbDao

    /**
     * Returns the DAO for [Idiom] entities.
     */
    abstract fun idiomDao() : IdiomDao
}