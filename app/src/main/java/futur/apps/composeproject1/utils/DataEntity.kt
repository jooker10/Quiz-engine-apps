package futur.apps.composeproject1.utils

/**
 * Interface representing a generic table in the database. item (Verbs, sentence, Nouns, etc.).
 *
 * Each item supports multiple languages, which makes the quiz multilingual:
 * - English (en)
 * - French (fr)
 * - Spanish (sp)
 * - Arabic (ar)
 *
 * Classes representing specific categories (e.g., VerbEntity, NounEntity)
 * should implement this interface.
 */
interface DataEntity {
    var id: Int
    var en: String
    var fr: String
    var sp: String
    var ar: String
}