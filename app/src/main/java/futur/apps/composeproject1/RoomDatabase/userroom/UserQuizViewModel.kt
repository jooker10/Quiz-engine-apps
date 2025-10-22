package futur.apps.composeproject1.RoomDatabase.userroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserQuizViewModel @Inject constructor(
    private val repository: UserQuizRepository
) : ViewModel() {

    // 🔹 Expose categories as StateFlow for Compose
    val categories: StateFlow<List<UserCategoryEntity>> =
        repository.getAllCategories()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 🔹 Expose questions grouped by categoryId
    @OptIn(ExperimentalCoroutinesApi::class)
    val questionsByCategory: StateFlow<Map<Int, List<UserQuestionEntity>>> =
        repository.getAllCategories()
            .flatMapLatest { categories ->
                combine(categories.map { cat ->
                    repository.getQuestionsByCategory(cat.id)
                        .map { questions -> cat.id to questions }
                }) { pairs ->
                    pairs.toMap()
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // 🔹 Add new category
    fun addCategory(name: String, description: String? = null) {
        viewModelScope.launch {
            repository.insertCategory(UserCategoryEntity(name = name, description = description))
        }
    }

    // 🔹 Add new question
    fun addQuestion(categoryId: Int, questionText: String, options: List<String>, correctAnswer: String) {
        viewModelScope.launch {
            repository.insertQuestion(
                UserQuestionEntity(
                    categoryId = categoryId,
                    questionText = questionText,
                    options = options,
                    correctAnswer = correctAnswer
                )
            )
        }
    }

    // 🔹 Delete category
    fun deleteCategory(category: UserCategoryEntity) {
        viewModelScope.launch { repository.deleteCategory(category) }
    }
    // UserQuizViewModel.kt  (make sure this function exists)
    fun getQuestions(categoryId: Int) = repository.getQuestionsByCategory(categoryId)


    // 🔹 Delete question
    fun deleteQuestion(question: UserQuestionEntity) {
        viewModelScope.launch { repository.deleteQuestion(question) }
    }

    // 🔹 Reset all user quizzes (categories + questions)
    fun resetUserStats() {
        viewModelScope.launch {
            repository.deleteAllQuestions()
            repository.deleteAllCategories()
        }
    }

}
