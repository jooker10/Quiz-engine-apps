package futur.apps.composeproject1.utils

import futur.apps.composeproject1.QuizFiles.Question

data class QuizUiState(
    val isLoading: Boolean = true,
    val category : CategoryName? = null,
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val score: Int = 0,
    val selectedOption: Int? = null,
    val selectedOptionText: String? = null,
    val isAnswerChecked: Boolean = false,
    val timeLeft: Int = 15,
    val error: String? = null,
    val isFinished: Boolean = false
)
