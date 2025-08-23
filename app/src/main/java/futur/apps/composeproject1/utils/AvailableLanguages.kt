package futur.apps.composeproject1.utils

enum class AvailableLanguages(val displayName: String) {
    ENGLISH("English"),
    FRENCH("French"),
    SPANISH("Spanish"),
    ARABIC("Arabic");

    companion object {
        val languages = AvailableLanguages.entries.map { it.displayName }
    }

}