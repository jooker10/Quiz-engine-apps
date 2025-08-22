package futur.apps.composeproject1.utils

import androidx.compose.ui.graphics.Color

enum class CategoryName(val displayName: String, val color: Color, val requiredPoints: Int) {
    Verbs("Verbs", Color(0xFF4CAF50),0),
    Sentences("Sentences", Color(0xFF2196F3),5),
    PhrasalVerbs("Phrasal Verbs", Color(0xFFFF9800),10),
    Nouns("Nouns", Color(0xFF9C27B0),15),
    Adjectives("Adjectives", Color(0xFFE91E63),20),
    Adverbs("Adverbs", Color(0xFF00BCD4),25),
    Idioms("Idioms", Color(0xFF795548),30);

    companion object {
        val entries = CategoryName.entries.toTypedArray()
        fun fromDisplayName(name: String?): CategoryName? {
            return entries.find { it.displayName == name }
        }
    }
}