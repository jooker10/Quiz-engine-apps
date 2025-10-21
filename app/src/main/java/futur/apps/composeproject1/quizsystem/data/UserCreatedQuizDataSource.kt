package futur.apps.composeproject1.quizsystem.data

import futur.apps.composeproject1.RoomDatabase.userroom.UserQuizRepository
import futur.apps.composeproject1.utils.Question
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class UserCreatedQuizDataSource @Inject constructor(
    private val repository: UserQuizRepository
) : QuizDataSource {

    override suspend fun getCategories(): List<String> =
        repository.getAllCategories().first().map { it.name }

    override suspend fun getQuestions(categoryName: String): List<Question> {
        val categories = repository.getAllCategories().first()
        val category = categories.find { it.name == categoryName } ?: return emptyList()

        return repository.getQuestionsByCategory(category.id)
            .first()
            .map { Question(it.questionText, it.options, it.correctAnswer) }
    }
}
