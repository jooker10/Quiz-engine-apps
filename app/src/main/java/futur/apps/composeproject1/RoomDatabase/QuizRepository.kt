package futur.apps.composeproject1.RoomDatabase

import futur.apps.composeproject1.RoomDatabase.Daos.*
import futur.apps.composeproject1.RoomDatabase.Entities.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that provides access to all category-related DAOs.
 *
 * This class acts as a single source of truth for retrieving
 * quiz items from the Room database.
 *
 * By exposing data as Flows, it allows reactive UI updates
 * when the underlying database changes.
 */
@Singleton
class QuizRepository @Inject constructor(
    private val verbDao: VerbDao,
    private val sentenceDao: SentenceDao,
    private val phrasalVerbDao: PhrasalVerbDao,
    private val nounDao: NounDao,
    private val adjectiveDao: AdjectiveDao,
    private val adverbDao: AdverbDao,
    private val idiomDao: IdiomDao
) {
    fun getAllVerbs(): Flow<List<Verb>> = verbDao.getAll()
    fun getAllSentences(): Flow<List<Sentence>> = sentenceDao.getAll()
    fun getAllPhrasalVerbs(): Flow<List<PhrasalVerb>> = phrasalVerbDao.getAll()
    fun getAllNouns(): Flow<List<Noun>> = nounDao.getAll()
    fun getAllAdjectives(): Flow<List<Adjective>> = adjectiveDao.getAll()
    fun getAllAdverbs(): Flow<List<Adverb>> = adverbDao.getAll()
    fun getAllIdioms(): Flow<List<Idiom>> = idiomDao.getAll()
}
/*

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
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val verbDao : VerbDao,
    private val sentenceDao : SentenceDao,
    private val phrasalVerbDao : PhrasalVerbDao,
    private val nounDao : NounDao,
    private val adjectiveDao: AdjectiveDao,
    private val adverbDao: AdverbDao,
    private val idiomDao: IdiomDao

    ) {
    fun getAllVerbs() : Flow<List<Verb>>  = verbDao.getAll()
    fun getAllSentences() : Flow<List<Sentence>>  = sentenceDao.getAll()
    fun getAllPhrasalVerbs() : Flow<List<PhrasalVerb>>  = phrasalVerbDao.getAll()
    fun getAllNouns() : Flow<List<Noun>>  = nounDao.getAll()
    fun getAllAdjectives() : Flow<List<Adjective>>  = adjectiveDao.getAll()
    fun getAllAdverbs() : Flow<List<Adverb>>  = adverbDao.getAll()
    fun getAllIdioms() : Flow<List<Idiom>>  = idiomDao.getAll()

}*/
