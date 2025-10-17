package com.example.autentication_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.autentication_app.ui.theme.Autentication_AppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Create Auth0 and CredentialsManager
        val auth0 = Auth0(BuildConfig.AUTH0_CLIENT_ID, BuildConfig.AUTH0_DOMAIN)
        val apiClient = AuthenticationAPIClient(auth0)
        val credentialsManager = CredentialsManager(apiClient, SharedPreferencesStorage(this))

        // ✅ Provide ViewModel with dependencies using a factory
        val viewModel: AuthViewModel by viewModels {
            AuthViewModelFactory(auth0, credentialsManager)
        }

        setContent {
            Autentication_AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthScreen(viewModel, this)
                }
            }
        }
    }
}
