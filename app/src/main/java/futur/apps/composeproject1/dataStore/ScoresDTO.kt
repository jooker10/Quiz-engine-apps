package futur.apps.composeproject1.dataStore

import kotlinx.serialization.Serializable

@Serializable
data class ScoresDTO(
    val map : Map<String, Int>
)
