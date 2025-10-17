package com.example.autentication_app

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.jwt.JWT
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.interfaces.DecodedJWT
import kotlinx.coroutines.launch

/**
 * Data class to hold the authenticated user's profile information.
 * This is the state exposed by the ViewModel.
 */
data class AuthState(
    val isAuthenticated: Boolean = false,
    val userId: String = "Not logged in",
    val name: String = "",
    val email: String = "",
    val token: String = "No token",
    val isLoading: Boolean = false
)

/**
 * ViewModel to handle all authentication logic, state management, and Auth0 interactions.
 * It uses dependency injection for the Auth0 client and CredentialsManager.
 */
class AuthViewModel(
    private val auth0: Auth0,
    private val credentialsManager: CredentialsManager
) : ViewModel() {

    // Mutable state that the Composable UI will observe
    var authState by mutableStateOf(AuthState())
        private set // Private set ensures only the ViewModel can modify the state

    init {
        // Attempt to load previously saved credentials on startup
        checkSavedCredentials()
    }

    private fun checkSavedCredentials() {
        authState = authState.copy(isLoading = true)
        viewModelScope.launch {
            // Fix 1: Changed parameter name to 'result' to match the supertype and clear warning
            credentialsManager.getCredentials(object : Callback<Credentials, CredentialsManagerException> {
                override fun onSuccess(result: Credentials) {
                    // Credentials found, user is already logged in
                    updateAuthStateFromToken(result.idToken)
                    authState = authState.copy(isLoading = false)
                }

                override fun onFailure(error: CredentialsManagerException) {
                    // No valid credentials found (or expired), proceed to logged out state
                    Log.d("AuthViewModel", "No valid credentials found: ${error.message}")
                    authState = AuthState(userId = "Ready to log in", isLoading = false)
                }
            })
        }
    }

    // --- Core Authentication Methods ---

    /**
     * Starts the Auth0 Web Authentication flow for login.
     * @param activity The current ComponentActivity, needed for the WebAuthProvider.
     */
    fun login(activity: ComponentActivity) {
        authState = authState.copy(isLoading = true)
        WebAuthProvider.login(auth0)
            .withScheme("demo") // Must match android:scheme in AndroidManifest.xml
            .withScope("openid profile email")
            .start(activity, object : Callback<Credentials, AuthenticationException> {
                // Fix 2: Changed parameter name to 'result' to match the supertype and clear warning
                override fun onSuccess(result: Credentials) {
                    credentialsManager.saveCredentials(result) // Save the credentials
                    updateAuthStateFromToken(result.idToken)
                    authState = authState.copy(isLoading = false)
                }

                override fun onFailure(error: AuthenticationException) {
                    Log.e("AuthViewModel", "Login Failed: ${error.message}", error)
                    authState = AuthState(userId = "Login Failed: ${error.message}", isLoading = false)
                }
            })
    }

    /**
     * Starts the Auth0 Web Authentication flow for logout.
     * @param activity The current ComponentActivity, needed for the WebAuthProvider.
     */
    fun logout(activity: ComponentActivity) {
        authState = authState.copy(isLoading = true)
        WebAuthProvider.logout(auth0)
            .withScheme("demo") // Must match android:scheme in AndroidManifest.xml
            .start(activity, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(result: Void?) {
                    credentialsManager.clearCredentials() // Clear stored credentials
                    authState = AuthState(userId = "Logged out", isLoading = false)
                }

                override fun onFailure(error: AuthenticationException) {
                    Log.e("AuthViewModel", "Logout Failed: ${error.message}", error)
                    authState = AuthState(userId = "Logout Failed: ${error.message}", isLoading = false)
                }
            })
    }

    // --- Helper Function ---

    /**
     * Decodes the JWT ID Token to extract user claims and update the state.
     */
    private fun updateAuthStateFromToken(idToken: String) {
        try {
            // Correct imports allow JWT.decode and DecodedJWT usage
            val jwt: DecodedJWT = JWT.decode(idToken)
            // Prioritize 'name' claim, fallback to 'subject' (sub)
            val name = jwt.getClaim("name").asString() ?: jwt.subject
            val email = jwt.getClaim("email").asString() ?: "N/A"

            authState = AuthState(
                isAuthenticated = true,
                userId = jwt.subject, // sub claim is the user ID
                name = name,
                email = email,
                token = idToken,
                isLoading = false
            )
        } catch (e: JWTDecodeException) {
            Log.e("AuthViewModel", "Token Decode Error: ${e.message}", e)
            authState = AuthState(userId = "Token Decode Error: ${e.message}", isLoading = false)
        }
    }
}

// Factory to allow the ViewModel to be constructed with parameters (auth0 and credentialsManager)
class AuthViewModelFactory(
    private val auth0: Auth0,
    private val credentialsManager: CredentialsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(auth0, credentialsManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
