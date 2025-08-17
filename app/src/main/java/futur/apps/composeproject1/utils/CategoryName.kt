package futur.apps.composeproject1.utils

import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.StateFlow

enum class CategoryName(val displayName : String) : StateFlow<CategoryName?> {
    Verbs("Verbs"),
    Sentences("Sentences"),
    PhrasalVerbs("Phrasal verbs"),
    Nouns("Nouns"),
    Adjectives("Adjectives"),
    Adverbs("Adverbs"),
    Idioms("Idioms");

    override val value: CategoryName?
        get() = TODO("Not yet implemented")
    override val replayCache: List<CategoryName?>
        get() = TODO("Not yet implemented")

    override suspend fun collect(collector: FlowCollector<CategoryName?>): Nothing {
        TODO("Not yet implemented")
    }

    companion object {
        fun fromDisplayName(name: String?) : CategoryName? {
            return entries.find {
                it.displayName == name
            }
        }
    }
}