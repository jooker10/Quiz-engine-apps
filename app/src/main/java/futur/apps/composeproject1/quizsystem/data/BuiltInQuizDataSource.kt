package futur.apps.composeproject1.quizsystem.data

import futur.apps.composeproject1.RoomDatabase.QuizRepository
import futur.apps.composeproject1.quizsystem.core.QuizConfig
import futur.apps.composeproject1.utils.BuildInCategory
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
        BuildInCategory.entries.map { it.name }

    override suspend fun getQuestions(categoryName: String): List<Question> {
        val category = runCatching { BuildInCategory.valueOf(categoryName) }.getOrNull()
            ?: return emptyList()

        // 🔹 Step 1: Load the right data list (List<DataEntity>)
        val data: List<DataEntity> = when (category) {
            BuildInCategory.Verbs -> repository.getAllVerbs().first()
            BuildInCategory.Sentences -> repository.getAllSentences().first()
            BuildInCategory.PhrasalVerbs -> repository.getAllPhrasalVerbs().first()
            BuildInCategory.Nouns -> repository.getAllNouns().first()
            BuildInCategory.Adjectives -> repository.getAllAdjectives().first()
            BuildInCategory.Adverbs -> repository.getAllAdverbs().first()
            BuildInCategory.Idioms -> repository.getAllIdioms().first()
        }

        // 🔹 Step 2: Convert them into Question objects (like in QuizViewModel)
        return data.shuffled().take(QuizConfig.MAX_QUESTIONS_PER_QUIZ).map { entity ->
            // Build random options using English translations from other items
            val incorrectOptions = data.filter { it.en != entity.en }
                .shuffled()
                .take(QuizConfig.CHOICE_COUNT - 1)
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
