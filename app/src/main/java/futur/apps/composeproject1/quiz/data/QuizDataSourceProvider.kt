package futur.apps.composeproject1.quiz.data

import futur.apps.composeproject1.utils.QuizMode
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ------------------------------------------------------------
 * Provides the correct quiz data source depending on [QuizMode].
 * ------------------------------------------------------------
 */
@Singleton
class QuizDataSourceProvider @Inject constructor(
    private val builtIn: BuiltInQuizDataSource,
    private val userCreated: UserCreatedQuizDataSource
) {
    fun getDataSource(mode: QuizMode): QuizDataSource =
        when (mode) {
            QuizMode.DEFAULT -> builtIn
            QuizMode.CUSTOM -> userCreated
        }
}
