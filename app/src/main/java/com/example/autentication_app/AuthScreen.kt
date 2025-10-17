package com.example.autentication_app

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(viewModel: AuthViewModel, activity: ComponentActivity) {
    val state = viewModel.authState

    when {
        state.isLoading -> {
            // 🌀 Loading screen while checking or performing authentication
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.isAuthenticated -> {
            // ✅ Logged in UI
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Welcome, ${state.name}!", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Email: ${state.email}")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.logout(activity) }) {
                    Text("Logout")
                }
            }
        }

        else -> {
            // 🔐 Not logged in yet
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Welcome to the Authentication App")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.login(activity) }) {
                    Text("Login with Auth0")
                }
            }
        }
    }
}
