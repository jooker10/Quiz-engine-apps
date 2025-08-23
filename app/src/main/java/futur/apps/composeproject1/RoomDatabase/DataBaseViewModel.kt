package futur.apps.composeproject1.RoomDatabase
/*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.utils.CategoryType
import futur.apps.composeproject1.utils.TableCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DbViewModel @Inject constructor(
    private val repository: DataRepository,
) : ViewModel() {

    val categoryFlows: Map<TableCategory, StateFlow<List<CategoryType>>> = mapOf(
        TableCategory.Verbs to repository.getAllVerbs(),
        TableCategory.Sentences to repository.getAllSentences(),
        TableCategory.PhrasalVerbs to repository.getAllPhrasalVerbs(),
        TableCategory.Nouns to repository.getAllNouns(),
        TableCategory.Adjectives to repository.getAllAdjectives(),
        TableCategory.Adverbs to repository.getAllAdverbs(),
        TableCategory.Idioms to repository.getAllIdioms()
    ).mapValues { (_, flow) ->
        flow.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }



}*/


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.utils.Category
import futur.apps.composeproject1.utils.QuizItem
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject

/**
 * ViewModel that provides reactive access to all category data.
 *
 * Instead of exposing individual Flows for each category (verbs, nouns, etc.),
 * this implementation aggregates them into a single map.
 *
 * Benefits:
 * - Clean and scalable design
 * - Easily accessible from the UI by querying the map with a TableCategory key
 */
@HiltViewModel
class DatabaseViewModel @Inject constructor(
    private val repository: QuizRepository,
) : ViewModel() {

    /**
     * A map of category -> StateFlow<List<QuizItem>>.
     * Each flow automatically stays active while the ViewModel is alive,
     * and replays the latest database state for efficient UI updates.
     */
    val categoryData: Map<Category, StateFlow<List<QuizItem>>> = mapOf(
        Category.Verbs to repository.getAllVerbs(),
        Category.Sentences to repository.getAllSentences(),
        Category.PhrasalVerbs to repository.getAllPhrasalVerbs(),
        Category.Nouns to repository.getAllNouns(),
        Category.Adjectives to repository.getAllAdjectives(),
        Category.Adverbs to repository.getAllAdverbs(),
        Category.Idioms to repository.getAllIdioms()
    ).mapValues { (_, flow) ->
        flow.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        }
}