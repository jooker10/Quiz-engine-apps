package futur.apps.composeproject1.quizCreator

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.utils.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class Category(
    val name: String,
    val questions: MutableList<Question> = mutableListOf()
)

@HiltViewModel
class QuizCreatorViewModel @Inject constructor() : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    // ------------------- Category Functions -------------------
    fun addCategory(name: String) {
        if (name.isBlank()) return
        val updated = _categories.value.toMutableList()
        updated.add(Category(name))
        _categories.value = updated
    }

    fun removeCategory(category: Category) {
        val updated = _categories.value.toMutableList()
        updated.remove(category)
        _categories.value = updated
    }

    // ------------------- Question Functions -------------------
    fun addQuestion(categoryName: String, question: Question) {
        val updated = _categories.value.toMutableList()
        val category = updated.find { it.name == categoryName }
        category?.questions?.add(question)
        _categories.value = updated
    }

    fun removeQuestion(categoryName: String, question: Question) {
        val updated = _categories.value.toMutableList()
        val category = updated.find { it.name == categoryName }
        category?.questions?.remove(question)
        _categories.value = updated
    }

    fun reorderQuestions(categoryName: String, fromIndex: Int, toIndex: Int) {
        val updated = _categories.value.toMutableList()
        val category = updated.find { it.name == categoryName } ?: return
        val questions = category.questions
        val item = questions.removeAt(fromIndex)
        questions.add(toIndex, item)
        _categories.value = updated
    }
}
