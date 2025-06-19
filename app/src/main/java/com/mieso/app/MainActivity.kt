package com.mieso.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mieso.app.data.repository.AuthRepository
import com.mieso.app.ui.auth.GoogleAuthUiClient
import com.mieso.app.ui.auth.LoginScreen
import com.mieso.app.ui.theme.MieSoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var googleAuthUiClient: GoogleAuthUiClient

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MieSoTheme {
                val authState by authRepository.getAuthState().collectAsState(initial = null)
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "auth_check") {
                    composable("auth_check") {
                        if (authState != null) {
                            MainScreen()
                        } else {
                            LoginScreen(
                                googleAuthUiClient = googleAuthUiClient,
                                onSignInSuccess = {
                                    navController.navigate("main_app") {
                                        popUpTo("auth_check") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                    composable("main_app") {
                        MainScreen()
                    }
                }
            }
        }
    }
}