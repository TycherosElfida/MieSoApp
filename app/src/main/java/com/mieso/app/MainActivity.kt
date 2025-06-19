package com.mieso.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mieso.app.ui.auth.AuthScreen
import com.mieso.app.data.repository.AuthRepository
import com.mieso.app.ui.navigation.Screen
import com.mieso.app.ui.theme.MieSoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MieSoTheme {
                val navController = rememberNavController()
                val authState by authRepository.getAuthState().collectAsState(initial = null)

                NavHost(
                    navController = navController,
                    // Check auth state to determine the starting screen
                    startDestination = if (authState == null) Screen.Login.route else Screen.Main.route
                ) {
                    composable(Screen.Login.route) {
                        AuthScreen(
                            onSignInSuccess = {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.Main.route) {
                        MainScreen()
                    }
                }
            }
        }
    }
}
