package com.mieso.app.ui.auth

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

// NEW: UI for the Login Screen
@Composable
fun LoginScreen(
    googleAuthUiClient: GoogleAuthUiClient,
    onSignInSuccess: () -> Unit
) {
    val viewModel: LoginViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == -1) { // RESULT_OK
                viewModel.viewModelScope.launch {
                    val signInResult = googleAuthUiClient.getSignInResultFromIntent(result.data!!)
                    signInResult.errorMessage?.let { token ->
                        viewModel.onSignInResult(token)
                    }
                }
            }
        }
    )

    LaunchedEffect(key1 = state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            Toast.makeText(context, "Sign in successful", Toast.LENGTH_LONG).show()
            onSignInSuccess()
            viewModel.resetState()
        }
    }

    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selamat Datang di MieSo",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Silakan masuk untuk melanjutkan pesanan Anda",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = {
            viewModel.viewModelScope.launch {
                val signInIntentSender = googleAuthUiClient.signIn()
                launcher.launch(
                    IntentSenderRequest.Builder(
                        signInIntentSender ?: return@launch
                    ).build()
                )
            }
        }) {
            Text(text = "Masuk dengan Google")
        }
    }
}