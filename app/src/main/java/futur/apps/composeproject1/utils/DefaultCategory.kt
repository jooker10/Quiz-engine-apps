package futur.apps.composeproject1.utils


/**
 * Enum class representing all quiz categories.
 *
 * Each category has:
 * - A display name (used in the UI)
 * - A color (used for styling)
 * - A maximum score (points required to unlock the next category)
 */
enum class DefaultCategory(
    val displayName: String,
    val maxPoints: Int = 20
) {
    Verbs("Verbs"),
    Sentences("Sentences"),
    PhrasalVerbs("Phrasal Verbs"),
    Nouns("Nouns", ),
    Adjectives("Adjectives"),
    Adverbs("Adverbs"),
    Idioms("Idioms")
}
