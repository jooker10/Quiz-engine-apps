package futur.apps.composeproject1.quiz.data

import futur.apps.composeproject1.RoomDatabase.QuizRepository
import futur.apps.composeproject1.quiz.core.AppConfig
import futur.apps.composeproject1.utils.DefaultCategory
import futur.apps.composeproject1.utils.DataEntity
import futur.apps.composeproject1.utils.Question
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuiltInQuizDataSource @Inject constructor(
    private val repository: QuizRepository
) : QuizDataSource {

    override suspend fun getCategories(): List<String> =
        DefaultCategory.entries.map { it.name }

    override suspend fun getQuestions(categoryName: String): List<Question> {
        val category = runCatching { DefaultCategory.valueOf(categoryName) }.getOrNull()
            ?: return emptyList()

        // 🔹 Step 1: Load the right data list (List<DataEntity>)
        val data: List<DataEntity> = when (category) {
            DefaultCategory.Verbs -> repository.getAllVerbs().first()
            DefaultCategory.Sentences -> repository.getAllSentences().first()
            DefaultCategory.PhrasalVerbs -> repository.getAllPhrasalVerbs().first()
            DefaultCategory.Nouns -> repository.getAllNouns().first()
            DefaultCategory.Adjectives -> repository.getAllAdjectives().first()
            DefaultCategory.Adverbs -> repository.getAllAdverbs().first()
            DefaultCategory.Idioms -> repository.getAllIdioms().first()
        }

        // 🔹 Step 2: Convert them into Question objects (like in QuizViewModel)
        return data.shuffled().take(AppConfig.MAX_QUESTIONS_PER_QUIZ).map { entity ->
            // Build random options using English translations from other items
            val incorrectOptions = data.filter { it.en != entity.en }
                .shuffled()
                .take(AppConfig.CHOICE_COUNT - 1)
                .map { it.en }

            val options = (incorrectOptions + entity.en).shuffled()

            Question(
                questionText = entity.fr, // 🇫🇷 shown as the question
                options = options,
                correctAnswer = entity.en  // 🇬🇧 correct translation
            )
        }
    }
}
