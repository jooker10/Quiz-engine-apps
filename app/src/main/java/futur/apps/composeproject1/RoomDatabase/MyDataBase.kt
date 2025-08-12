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

@Database(entities = [Verb::class, Sentence ::class, PhrasalVerb::class, Noun::class, Adjective::class, Adverb::class, Idiom::class], version = 1, exportSchema = false )
abstract class MyDataBase  : RoomDatabase() {

    abstract fun verbDao() : VerbDao
    abstract fun sentenceDao() : SentenceDao
    abstract fun phrasalVerbDao() : PhrasalVerbDao
    abstract fun nounDao() : NounDao
    abstract fun adjectiveDao() : AdjectiveDao
    abstract fun adverbDao() : AdverbDao
    abstract fun idiomDao() : IdiomDao
}