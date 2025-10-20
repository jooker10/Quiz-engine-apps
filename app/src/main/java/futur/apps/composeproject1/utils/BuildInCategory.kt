package futur.apps.composeproject1.utils

import androidx.compose.ui.graphics.Color

/**
 * Enum class representing all quiz categories.
 *
 * Each category has:
 * - A display name (used in the UI)
 * - A color (used for styling)
 * - A maximum score (points required to unlock the next category)
 */
enum class BuildInCategory(
    val displayName: String,
    val themeColor: Color,
    val maxPoints: Int = 20
) {
    Verbs("Verbs", Color(0xFF81D4FA)),
    Sentences("Sentences", Color(0xFF90CAF9)),
    PhrasalVerbs("Phrasal Verbs", Color(0xFFCE93D8)),
    Nouns("Nouns", Color(0xFFA5D6A7)),
    Adjectives("Adjectives", Color(0xFFFFCC80)),
    Adverbs("Adverbs", Color(0xFFFFC107)),
    Idioms("Idioms", Color(0xFFFF8A65))
}
