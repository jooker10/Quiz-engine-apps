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

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMyDataBase(@ApplicationContext context: Context): MyDataBase {

        return Room.databaseBuilder(
            context,
            MyDataBase::class.java,
            "english_learning"
            )
            .createFromAsset("databases/english_learning.db")
            .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    fun provideVerbDao(myDataBase: MyDataBase) : VerbDao = myDataBase.verbDao()
    @Provides
    fun provideSentenceDao(myDataBase: MyDataBase) : SentenceDao = myDataBase.sentenceDao()
    @Provides
    fun providePhrasalVerbDao(myDataBase: MyDataBase) : PhrasalVerbDao = myDataBase.phrasalVerbDao()
    @Provides
    fun provideNounDao(myDataBase: MyDataBase) : NounDao = myDataBase.nounDao()
    @Provides
    fun provideAdjectiveDao(myDataBase: MyDataBase) : AdjectiveDao = myDataBase.adjectiveDao()
    @Provides
    fun provideAdverbDao(myDataBase: MyDataBase) : AdverbDao = myDataBase.adverbDao()
    @Provides
    fun provideIdiomDao(myDataBase: MyDataBase) : IdiomDao = myDataBase.idiomDao()
}