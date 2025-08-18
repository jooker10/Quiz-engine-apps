package futur.apps.composeproject1.DataStore

import kotlinx.serialization.Serializable

@Serializable
data class ScoresDTO(
    val map : Map<String, Int>
)
