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
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuizDao
import futur.apps.composeproject1.RoomDatabase.userroom.UserQuizDatabase
import javax.inject.Singleton

/**
 * Hilt module that provides instances of DAOs and the Room database.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- MAIN DATABASE (read-only preloaded data)
    @Provides
    @Singleton
    fun provideMyDataBase(@ApplicationContext context: Context): MyDataBase {
        return Room.databaseBuilder(
            context,
            MyDataBase::class.java,
            "english_learning"
        )
            .createFromAsset("databases/english_learning.db") // preload data
            .fallbackToDestructiveMigration(false)
            .build()
    }

    // --- USER QUIZ DATABASE (editable data)
    @Provides
    @Singleton
    fun provideUserQuizDatabase(@ApplicationContext context: Context): UserQuizDatabase {
        return Room.databaseBuilder(
            context,
            UserQuizDatabase::class.java,
            "user_quiz.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // --- DAOs ---
    @Provides fun provideVerbDao(db: MyDataBase) = db.verbDao()
    @Provides fun provideSentenceDao(db: MyDataBase) = db.sentenceDao()
    @Provides fun providePhrasalVerbDao(db: MyDataBase) = db.phrasalVerbDao()
    @Provides fun provideNounDao(db: MyDataBase) = db.nounDao()
    @Provides fun provideAdjectiveDao(db: MyDataBase) = db.adjectiveDao()
    @Provides fun provideAdverbDao(db: MyDataBase) = db.adverbDao()
    @Provides fun provideIdiomDao(db: MyDataBase) = db.idiomDao()

    // --- USER QUIZ DAO ---
    @Provides
    fun provideUserQuizDao(db: UserQuizDatabase): UserQuizDao = db.userQuizDao()
}

/*
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    */
/**
     * Provides the [MyDataBase] instance.
     * @param context The application context.
     * @return The [MyDataBase] instance.
     *//*

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

    */
/**
     * Provides the [VerbDao] instance.
     * @param myDataBase The [MyDataBase] instance.
     * @return The [VerbDao] instance.
     *//*

    @Provides
    fun provideVerbDao(myDataBase: MyDataBase) : VerbDao = myDataBase.verbDao()

    */
/**
     * Provides the [SentenceDao] instance.
     * @param myDataBase The [MyDataBase] instance.
     * @return The [SentenceDao] instance.
     *//*

    @Provides
    fun provideSentenceDao(myDataBase: MyDataBase) : SentenceDao = myDataBase.sentenceDao()

    */
/**
     * Provides the [PhrasalVerbDao] instance.
     * @param myDataBase The [MyDataBase] instance.
     * @return The [PhrasalVerbDao] instance.
     *//*

    @Provides
    fun providePhrasalVerbDao(myDataBase: MyDataBase) : PhrasalVerbDao = myDataBase.phrasalVerbDao()

    */
/**
     * Provides the [NounDao] instance.
     * @param myDataBase The [MyDataBase] instance.
     * @return The [NounDao] instance.
     *//*

    @Provides
    fun provideNounDao(myDataBase: MyDataBase) : NounDao = myDataBase.nounDao()

    */
/**
     * Provides the [AdjectiveDao] instance.
     * @param myDataBase The [MyDataBase] instance.
     * @return The [AdjectiveDao] instance.
     *//*

    @Provides
    fun provideAdjectiveDao(myDataBase: MyDataBase) : AdjectiveDao = myDataBase.adjectiveDao()

    */
/**
     * Provides the [AdverbDao] instance.
     * @param myDataBase The [MyDataBase] instance.
     * @return The [AdverbDao] instance.
     *//*

    @Provides
    fun provideAdverbDao(myDataBase: MyDataBase) : AdverbDao = myDataBase.adverbDao()

    */
/**
     * Provides the [IdiomDao] instance.
     * @param myDataBase The [MyDataBase] instance.
     * @return The [IdiomDao] instance.
     *//*

    @Provides
    fun provideIdiomDao(myDataBase: MyDataBase) : IdiomDao = myDataBase.idiomDao()


    @Provides
    @Singleton
    fun provideUserQuizDatabase(@ApplicationContext context: Context): UserQuizDatabase {
        return Room.databaseBuilder(
            context,
            UserQuizDatabase::class.java,
            "user_quiz.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserQuizDao(userQuizDatabase: UserQuizDatabase): UserQuizDao =
        userQuizDatabase.userQuizDao()

}*/
