package futur.apps.composeproject1.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.RoomDatabase.QuizRepository
import futur.apps.composeproject1.utils.DefaultCategory
import futur.apps.composeproject1.utils.DataEntity
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
      repository: QuizRepository,
) : ViewModel() {

    /**
     * A map of category -> StateFlow<List<Stats>>.
     * Each flow automatically stays active while the ViewModel is alive,
     * and replays the latest database state for efficient UI updates.
     */
    val categoryData: Map<DefaultCategory, StateFlow<List<DataEntity>>> = mapOf(
        DefaultCategory.Verbs to repository.getAllVerbs(),
        DefaultCategory.Sentences to repository.getAllSentences(),
        DefaultCategory.PhrasalVerbs to repository.getAllPhrasalVerbs(),
        DefaultCategory.Nouns to repository.getAllNouns(),
        DefaultCategory.Adjectives to repository.getAllAdjectives(),
        DefaultCategory.Adverbs to repository.getAllAdverbs(),
        DefaultCategory.Idioms to repository.getAllIdioms()
    ).mapValues { (_, flow) ->
        flow.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        }
}