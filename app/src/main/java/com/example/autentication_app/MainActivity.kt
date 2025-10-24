package com.example.autentication_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.example.autentication_app.ui.theme.Autentication_AppTheme

class MainActivity : ComponentActivity() {

    private lateinit var auth0: Auth0
    private lateinit var credentialsManager: CredentialsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Auth0 client
        auth0 = Auth0(BuildConfig.AUTH0_CLIENT_ID, BuildConfig.AUTH0_DOMAIN)

        // Use AuthenticationAPIClient to fix previous error
        val apiClient = AuthenticationAPIClient(auth0)

        // Create CredentialsManager instance
        credentialsManager = CredentialsManager(apiClient, SharedPreferencesStorage(this))

        // ViewModel factory (inject dependencies)
        val viewModelFactory = AuthViewModelFactory(auth0, credentialsManager)
        val viewModel: AuthViewModel by viewModels { viewModelFactory }

        setContent {
            Autentication_AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AuthScreen(viewModel, this)
                }
            }
        }
    }
}
