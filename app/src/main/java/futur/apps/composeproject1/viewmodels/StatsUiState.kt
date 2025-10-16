package futur.apps.composeproject1.viewmodels

import kotlinx.serialization.Serializable

@Serializable
data class StatsUiState(
    val isLoading: Boolean = true,
    val totalQuizzes: Int = 0,
    val totalCorrectAnswers: Int = 0,
    val totalWrongAnswers: Int = 0,
    val quizzesPerCategory: Map<String, Int> = emptyMap(),
    val correctPerCategory: Map<String, Int> = emptyMap(),
    val wrongPerCategory: Map<String, Int> = emptyMap()
)