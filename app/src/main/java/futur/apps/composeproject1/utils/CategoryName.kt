package futur.apps.composeproject1.utils

enum class CategoryName(val displayName : String) {
    Verbs("Verbs"),
    Sentences("Sentences"),
    PhrasalVerbs("Phrasal verbs"),
    Nouns("Nouns"),
    Adjectives("Adjectives"),
    Adverbs("Adverbs"),
    Idioms("Idioms");

    companion object {
        fun fromDisplayName(name: String?) : CategoryName? {
            return entries.find {
                it.displayName == name
            }
        }
    }
}