package futur.apps.composeproject1.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import futur.apps.composeproject1.dataStore.AppDataStore
import futur.apps.composeproject1.quizsystem.core.AppConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * ============================================================
 * 🔐 AuthViewModel.kt
 * ------------------------------------------------------------
 * Handles authentication and Firestore (optional sync).
 * Works fully offline when [AppConfig.USE_FIRESTORE_SYNC] = false.
 * ============================================================
 */
@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db = Firebase.firestore

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile = _userProfile.asStateFlow()

    private val _isCheckingSession = mutableStateOf(true)
    val isCheckingSession: State<Boolean> = _isCheckingSession


    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                if (user != null) {
                    if (AppConfig.USE_FIRESTORE_SYNC) {
                        // ✅ Mark as checking until Firestore done
                        _isCheckingSession.value = true

                        // Wait for Firestore connection to be available
                        try {
                            Firebase.firestore.enableNetwork().await()
                        } catch (_: Exception) {}

                        // Load or create the user safely
                        loadOrCreateUser(
                            user.uid,
                            user.displayName,
                            user.email,
                            user.photoUrl?.toString()
                        )

                        // Wait until Firestore has returned a value
                        // This ensures _userProfile is not null before continuing
                        repeat(20) {
                            if (_userProfile.value != null) return@repeat
                            delay(50)
                        }
                    } else {
                        _userProfile.value = UserProfile(
                            uid = user.uid,
                            name = user.displayName ?: "Guest",
                            email = user.email,
                            photoUrl = user.photoUrl?.toString(),
                            joinDate = System.currentTimeMillis(),
                            totalPoints = 0
                        )
                    }
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error during session check."
            } finally {
                // ✅ Now safe to end loading (Firestore fully loaded or skipped)
                _isCheckingSession.value = false
            }
        }
    }


    // ------------------------------------------------------------------
    // ☁️ Firestore save/load (Protected by Config)
    // ------------------------------------------------------------------

    private suspend fun saveUser(profile: UserProfile) {
        if (!AppConfig.USE_FIRESTORE_SYNC) return
        try {
            db.collection("users")
                .document(profile.uid)
                .set(profile)
                .await()
        } catch (e: Exception) {
            _error.value = e.localizedMessage ?: "Error saving user."
        }
    }

    private fun loadOrCreateUser(uid: String, name: String?, email: String?, photoUrl: String?) {
        if (!AppConfig.USE_FIRESTORE_SYNC) return
        viewModelScope.launch {
            try {
                val snapshot = db.collection("users").document(uid).get().await()
                if (snapshot.exists()) {
                    val data = snapshot.data
                    _userProfile.value = UserProfile(
                        uid = uid,
                        name = data?.get("name") as? String ?: name,
                        email = data?.get("email") as? String ?: email,
                        photoUrl = data?.get("photoUrl") as? String ?: photoUrl,
                        joinDate = data?.get("joinDate") as? Long ?: System.currentTimeMillis(),
                        totalPoints = (data?.get("totalPoints") as? Long)?.toInt() ?: 0
                    )
                } else {
                    val newProfile = UserProfile(
                        uid = uid,
                        name = name,
                        email = email,
                        photoUrl = photoUrl,
                        joinDate = System.currentTimeMillis(),
                        totalPoints = 0
                    )
                    saveUser(newProfile)
                    _userProfile.value = newProfile
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error loading user profile."
            }
        }
    }

    // ------------------------------------------------------------------
    // 🔐 Authentication Functions
    // ------------------------------------------------------------------

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                auth.currentUser?.let {
                    if (AppConfig.USE_FIRESTORE_SYNC) {
                        loadOrCreateUser(it.uid, it.displayName, it.email, it.photoUrl?.toString())
                    } else {
                        _userProfile.value = UserProfile(
                            uid = it.uid,
                            name = it.displayName ?: "Guest",
                            email = it.email,
                            photoUrl = it.photoUrl?.toString()
                        )
                    }
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, password: String, username: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                val currentUser = auth.currentUser ?: return@launch

                currentUser.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                ).await()

                // Send verification email
                currentUser.sendEmailVerification().await()

                val profile = UserProfile(
                    uid = currentUser.uid,
                    name = username,
                    email = currentUser.email,
                    photoUrl = currentUser.photoUrl?.toString(),
                    joinDate = System.currentTimeMillis(),
                    totalPoints = 0
                )

                if (AppConfig.USE_FIRESTORE_SYNC) saveUser(profile)
                _userProfile.value = profile

                _error.value = "✅ Verification email sent. Please check your inbox."
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Registration failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()

                delay(400) // Small delay to ensure Firebase sync
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    if (AppConfig.USE_FIRESTORE_SYNC) {
                        try {
                            Firebase.firestore.enableNetwork().await()
                        } catch (_: Exception) {}
                        loadOrCreateUser(
                            currentUser.uid,
                            currentUser.displayName,
                            currentUser.email,
                            currentUser.photoUrl?.toString()
                        )
                    } else {
                        _userProfile.value = UserProfile(
                            uid = currentUser.uid,
                            name = currentUser.displayName ?: "Guest",
                            email = currentUser.email,
                            photoUrl = currentUser.photoUrl?.toString(),
                            joinDate = System.currentTimeMillis(),
                            totalPoints = 0
                        )
                    }
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Google sign-in failed"
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                _error.value = "Password reset email sent."
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error sending reset email."
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _userProfile.value = null
    }

    fun updatePointsInFirestore(newPoints: Int) {
        if (!AppConfig.USE_FIRESTORE_SYNC) return
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                val userDoc = db.collection("users").document(user.uid)
                userDoc.update("totalPoints", newPoints).await()

                _userProfile.value?.let {
                    _userProfile.value = it.copy(totalPoints = newPoints)
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error updating points."
            }
        }
    }
}
