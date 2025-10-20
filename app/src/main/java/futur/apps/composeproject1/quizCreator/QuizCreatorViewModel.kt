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

    // ------------------- BuildInCategory Functions -------------------
    fun addCategory(name: String) {
        if (name.isBlank()) return
        val updated = _categories.value.toMutableList()
        updated.add(Category(name))
        _categories.value = updated
    }

    fun removeCategory(categoryName: String) {
        val updated = _categories.value.toMutableList()
        updated.removeAll { it.name == categoryName }
        _categories.value = updated
    }


    // ------------------- Question Functions -------------------
    fun addQuestion(categoryName: String, question: Question) {
        val updated = _categories.value.map { cat ->
            if (cat.name == categoryName) {
                cat.copy(questions = cat.questions.toMutableList().also { it.add(question) })
            } else cat
        }
        _categories.value = updated
    }



    fun removeQuestion(categoryName: String, question: Question) {
        val updated = _categories.value.map { cat ->
            if (cat.name == categoryName) {
                cat.copy(questions = cat.questions.toMutableList().also { it.remove(question) })
            } else cat
        }
        _categories.value = updated
    }

    fun reorderQuestions(categoryName: String, fromIndex: Int, toIndex: Int) {
        val updated = _categories.value.map { cat ->
            if (cat.name == categoryName) {
                val newQuestions = cat.questions.toMutableList()
                val item = newQuestions.removeAt(fromIndex)
                newQuestions.add(toIndex, item)
                cat.copy(questions = newQuestions)
            } else cat
        }
        _categories.value = updated
    }
}
