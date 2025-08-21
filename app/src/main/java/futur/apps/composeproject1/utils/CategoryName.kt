package futur.apps.composeproject1.utils

import androidx.compose.ui.graphics.Color

enum class CategoryName(val displayName: String, val color: Color) {
    Verbs("Verbs", Color(0xFF4CAF50)),
    Sentences("Sentences", Color(0xFF2196F3)),
    PhrasalVerbs("Phrasal Verbs", Color(0xFFFF9800)),
    Nouns("Nouns", Color(0xFF9C27B0)),
    Adjectives("Adjectives", Color(0xFFE91E63)),
    Adverbs("Adverbs", Color(0xFF00BCD4)),
    Idioms("Idioms", Color(0xFF795548));

    companion object {
        fun fromDisplayName(name: String?): CategoryName? {
            return entries.find { it.displayName == name }
        }
    }
}