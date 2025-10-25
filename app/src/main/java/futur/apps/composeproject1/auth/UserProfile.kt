package futur.apps.composeproject1.auth

data class UserProfile(
    val uid: String = "",
    val name: String? = null,
    val email: String? = null,
    val photoUrl: String? = null,
    val joinDate: Long = System.currentTimeMillis(),
    val totalPoints: Int = 0
)
