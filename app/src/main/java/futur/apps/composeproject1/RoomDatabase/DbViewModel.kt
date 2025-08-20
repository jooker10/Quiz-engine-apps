package futur.apps.composeproject1.RoomDatabase

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
    /* val verbs = repository.getAllVerbs()
         .stateIn(viewModelScope, SharingStarted.Lazily,emptyList())
 val sentences = repository.getAllSentences()
     .stateIn(viewModelScope, SharingStarted.Lazily,emptyList())
 val phrasalVerbs = repository.getAllPhrasalVerbs()
     .stateIn(viewModelScope, SharingStarted.Lazily,emptyList())
 val nouns = repository.getAllNouns()
     .stateIn(viewModelScope, SharingStarted.Lazily,emptyList())
 val adjectives = repository.getAllAdjectives()
     .stateIn(viewModelScope, SharingStarted.Lazily,emptyList())
 val adverbs = repository.getAllAdverbs()
     .stateIn(viewModelScope, SharingStarted.Lazily,emptyList())
 val idioms = repository.getAllIdioms()
     .stateIn(viewModelScope, SharingStarted.Lazily,emptyList())*/
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



}