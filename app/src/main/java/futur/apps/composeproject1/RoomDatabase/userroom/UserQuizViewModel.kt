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

    // ---------- Categories ----------
    val categories: StateFlow<List<UserCategoryEntity>> =
        repository.getAllCategories()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getCategoryById(id: Int) = repository.getCategoryById(id)

    fun addCategory(name: String, description: String? = null) = viewModelScope.launch {
        repository.insertCategory(UserCategoryEntity(name = name, description = description))
    }

    fun updateCategory(category: UserCategoryEntity, name: String, desc: String?) =
        viewModelScope.launch {
            repository.updateCategory(category.id, name, desc)
        }

    fun deleteCategory(category: UserCategoryEntity) = viewModelScope.launch {
        repository.deleteCategory(category)
    }


    // ---------- Questions ----------
    @OptIn(ExperimentalCoroutinesApi::class)
    val questionsByCategory: StateFlow<Map<Int, List<UserQuestionEntity>>> =
        repository.getAllCategories()
            .flatMapLatest { categories ->
                combine(categories.map { cat ->
                    repository.getQuestionsByCategory(cat.id)
                        .map { cat.id to it }
                }) { it.toMap() }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun getQuestions(categoryId: Int) = repository.getQuestionsByCategory(categoryId)

    fun addQuestion(categoryId: Int, text: String, opts: List<String>, correct: String) =
        viewModelScope.launch {
            repository.insertQuestion(
                UserQuestionEntity(
                    categoryId = categoryId,
                    questionText = text,
                    options = opts,
                    correctAnswer = correct
                )
            )
        }

    fun updateQuestion(q: UserQuestionEntity, newText: String, newOpts: List<String>, newCorrect: String) =
        viewModelScope.launch {
            repository.updateQuestion(q.id, newText, newOpts, newCorrect)
        }

    fun deleteQuestion(q: UserQuestionEntity) = viewModelScope.launch {
        repository.deleteQuestion(q)
    }


    // ---------- Reset ----------
    fun resetUserStats() = viewModelScope.launch {
        repository.deleteAllQuestions()
        repository.deleteAllCategories()
    }
}
