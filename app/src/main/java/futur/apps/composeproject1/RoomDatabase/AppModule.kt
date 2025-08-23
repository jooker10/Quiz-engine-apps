package futur.apps.composeproject1.RoomDatabase

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import futur.apps.composeproject1.RoomDatabase.Daos.AdjectiveDao
import futur.apps.composeproject1.RoomDatabase.Daos.AdverbDao
import futur.apps.composeproject1.RoomDatabase.Daos.IdiomDao
import futur.apps.composeproject1.RoomDatabase.Daos.NounDao
import futur.apps.composeproject1.RoomDatabase.Daos.PhrasalVerbDao
import futur.apps.composeproject1.RoomDatabase.Daos.SentenceDao
import futur.apps.composeproject1.RoomDatabase.Daos.VerbDao
import futur.apps.composeproject1.RoomDatabase.Entities.PhrasalVerb
import javax.inject.Singleton

/**
 * Hilt module that provides instances of DAOs and the Room database.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the [MyDataBase] instance.
     * @param context The application context.
     * @return The [MyDataBase] instance.
     */
    @Provides
    @Singleton
    fun provideMyDataBase(@ApplicationContext context: Context): MyDataBase {

        return Room.databaseBuilder(
            context,
            MyDataBase::class.java,
            "english_learning"
        )
            .createFromAsset("databases/english_learning.db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    /**
     * Provides the [VerbDao] instance.
     * @param myDataBase The [MyDataBase] instance.
     * @return The [VerbDao] instance.
     */
    @Provides
    fun provideVerbDao(myDataBase: MyDataBase) : VerbDao = myDataBase.verbDao()

    /**
     * Provides the [SentenceDao] instance.
     * @param myDataBase The [MyDataBase] instance.
     * @return The [SentenceDao] instance.
     */
    @Provides
    fun provideSentenceDao(myDataBase: MyDataBase) : SentenceDao = myDataBase.sentenceDao()

    /**
     * Provides the [PhrasalVerbDao] instance.
     * @param myDataBase The [MyDataBase] instance.
     * @return The [PhrasalVerbDao] instance.
     */
    @Provides
    fun providePhrasalVerbDao(myDataBase: MyDataBase) : PhrasalVerbDao = myDataBase.phrasalVerbDao()

    /**
     * Provides the [NounDao] instance.
     * @param myDataBase The [MyDataBase] instance.
     * @return The [NounDao] instance.
     */
    @Provides
    fun provideNounDao(myDataBase: MyDataBase) : NounDao = myDataBase.nounDao()

    /**
     * Provides the [AdjectiveDao] instance.
     * @param myDataBase The [MyDataBase] instance.
     * @return The [AdjectiveDao] instance.
     */
    @Provides
    fun provideAdjectiveDao(myDataBase: MyDataBase) : AdjectiveDao = myDataBase.adjectiveDao()

    /**
     * Provides the [AdverbDao] instance.
     * @param myDataBase The [MyDataBase] instance.
     * @return The [AdverbDao] instance.
     */
    @Provides
    fun provideAdverbDao(myDataBase: MyDataBase) : AdverbDao = myDataBase.adverbDao()

    /**
     * Provides the [IdiomDao] instance.
     * @param myDataBase The [MyDataBase] instance.
     * @return The [IdiomDao] instance.
     */
    @Provides
    fun provideIdiomDao(myDataBase: MyDataBase) : IdiomDao = myDataBase.idiomDao()
}