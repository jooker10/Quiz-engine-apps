package futur.apps.composeproject1.RoomDatabase.userroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserQuizViewModel @Inject constructor(
    private val repository: UserQuizRepository
) : ViewModel() {

    val categories = repository.getAllCategories()

    fun getQuestions(categoryId: Int) = repository.getQuestionsByCategory(categoryId)

    fun addCategory(name: String, description: String? = null) {
        viewModelScope.launch {
            repository.insertCategory(UserCategoryEntity(name = name, description = description))
        }
    }

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

    fun deleteCategory(category: UserCategoryEntity) {
        viewModelScope.launch { repository.deleteCategory(category) }
    }

    fun deleteQuestion(question: UserQuestionEntity) {
        viewModelScope.launch { repository.deleteQuestion(question) }
    }
}
