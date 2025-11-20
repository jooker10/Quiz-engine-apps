package futur.apps.composeproject1.auth

import android.util.Patterns
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
import futur.apps.composeproject1.quiz.core.AppConfig
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val auth = Firebase.auth
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
        checkCurrentSession()
    }


    // --------------------------------------------------
    // SESSION CHECK
    // --------------------------------------------------
    private fun checkCurrentSession() {
        viewModelScope.launch {
            try {
                val user = auth.currentUser

                if (user != null && user.isEmailVerified) {

                    // ✅ 1) Set a *local* profile immediately (no Firestore wait)
                    val baseProfile = UserProfile(
                        uid = user.uid,
                        name = user.displayName ?: "Guest",
                        email = user.email,
                        photoUrl = user.photoUrl?.toString(),
                        joinDate = System.currentTimeMillis(),
                        totalPoints = 0
                    )
                    _userProfile.value = baseProfile

                    // ✅ 2) Then, if Firestore is enabled, refine it in background
                    if (AppConfig.USE_FIRESTORE_SYNC) {
                        loadOrCreateUser(
                            uid = user.uid,
                            name = user.displayName,
                            email = user.email,
                            photoUrl = user.photoUrl?.toString()
                        )
                    }
                } else {
                    // لا مستخدم أو غير مفعّل الإيميل → نترك userProfile = null
                    _userProfile.value = null
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                // ✅ important: this happens AFTER we touched _userProfile
                _isCheckingSession.value = false
            }
        }
    }



    // --------------------------------------------------
    // HELPERS
    // --------------------------------------------------
    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private suspend fun isEmailAlreadyRegistered(email: String): Boolean {
        return try {
            val result = auth.fetchSignInMethodsForEmail(email).await()
            result.signInMethods?.isNotEmpty() == true
        } catch (e: Exception) {
            false
        }
    }


    // --------------------------------------------------
    // REGISTER
    // --------------------------------------------------
    fun register(email: String, password: String, username: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Validate email format
                if (!isValidEmail(email)) {
                    _error.value = "Invalid email format."
                    return@launch
                }

                // Check if already exists
                if (isEmailAlreadyRegistered(email)) {
                    _error.value = "This email is already registered."
                    return@launch
                }

                // Create account
                auth.createUserWithEmailAndPassword(email, password).await()
                val user = auth.currentUser ?: return@launch

                user.updateProfile(
                    UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                ).await()

                // Send verification email
                user.sendEmailVerification().await()

                _error.value = "A verification email has been sent. Please check your inbox."
                _userProfile.value = null   // Block login

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Registration failed."
            } finally {
                _isLoading.value = false
            }
        }
    }


    // --------------------------------------------------
    // LOGIN
    // --------------------------------------------------
    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                if (!isValidEmail(email)) {
                    _error.value = "Invalid email."
                    return@launch
                }

                auth.signInWithEmailAndPassword(email, password).await()
                val user = auth.currentUser ?: return@launch

                if (!user.isEmailVerified) {
                    _error.value = "Please verify your email before logging in."
                    auth.signOut()
                    return@launch
                }

                if (AppConfig.USE_FIRESTORE_SYNC) {
                    loadOrCreateUser(
                        user.uid,
                        user.displayName,
                        user.email,
                        user.photoUrl?.toString()
                    )
                } else {
                    _userProfile.value = UserProfile(
                        uid = user.uid,
                        name = user.displayName ?: "Guest",
                        email = user.email,
                        photoUrl = user.photoUrl?.toString()
                    )
                }

                _error.value = null

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Login failed."
            } finally {
                _isLoading.value = false
            }
        }
    }


    // --------------------------------------------------
    // GOOGLE SIGN-IN
    // --------------------------------------------------
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential).await()

                val user = auth.currentUser ?: return@launch

                if (!user.isEmailVerified) {
                    _error.value = "Please verify your Google email account."
                    auth.signOut()
                    return@launch
                }

                loadOrCreateUser(
                    user.uid,
                    user.displayName,
                    user.email,
                    user.photoUrl?.toString()
                )

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Google sign-in failed."
            } finally {
                _isLoading.value = false
            }
        }
    }


    // --------------------------------------------------
    // FIRESTORE LOAD OR CREATE
    // --------------------------------------------------
    private fun loadOrCreateUser(uid: String, name: String?, email: String?, photoUrl: String?) {
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(uid).get().await()
                if (doc.exists()) {
                    val data = doc.data!!
                    _userProfile.value = UserProfile(
                        uid = uid,
                        name = data["name"] as? String ?: name,
                        email = data["email"] as? String ?: email,
                        photoUrl = data["photoUrl"] as? String ?: photoUrl,
                        joinDate = data["joinDate"] as? Long ?: System.currentTimeMillis(),
                        totalPoints = (data["totalPoints"] as? Long)?.toInt() ?: 0
                    )
                } else {
                    val profile = UserProfile(
                        uid = uid,
                        name = name,
                        email = email,
                        photoUrl = photoUrl,
                        joinDate = System.currentTimeMillis(),
                        totalPoints = 0
                    )
                    db.collection("users").document(uid).set(profile).await()
                    _userProfile.value = profile
                }

            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }


    // --------------------------------------------------
    // EXTRA
    // --------------------------------------------------
    fun resendVerificationEmail() {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            try {
                user.sendEmailVerification().await()
                _error.value = "Verification email sent again."
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                _error.value = "Password reset email sent."
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _userProfile.value = null
    }

    fun updateDefaultPointsInFirestore(newPoints: Int) {
        if (!AppConfig.USE_FIRESTORE_SYNC) return

        val user = auth.currentUser ?: return

        viewModelScope.launch {
            try {
                val userDoc = db.collection("users").document(user.uid)

                userDoc.update("totalPoints", newPoints).await()

                // تحديث UserProfile المحلي
                _userProfile.value = _userProfile.value?.copy(
                    totalPoints = newPoints
                )

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error updating points."
            }
        }
    }

}
