package com.mieso.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mieso.app.ui.SplashScreen
import com.mieso.app.ui.auth.AuthScreen
import com.mieso.app.ui.navigation.Screen
import com.mieso.app.ui.theme.MieSoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                viewModel.authStatus.value == AuthStatus.LOADING
            }
        }

        enableEdgeToEdge()
        setContent {
            MieSoTheme {
                val navController = rememberNavController()
                val authStatus by viewModel.authStatus.collectAsState()

                LaunchedEffect(authStatus) {
                    if (authStatus != AuthStatus.LOADING) {
                        navController.navigate(
                            if (authStatus == AuthStatus.AUTHENTICATED) Screen.Main.route else Screen.Login.route
                        ) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }

                NavHost(navController = navController, startDestination = Screen.Splash.route) {
                    composable(Screen.Splash.route) {
                        SplashScreen()
                    }
                    composable(Screen.Login.route) {
                        AuthScreen(
                            onSignInSuccess = {
                                viewModel.refreshAuthenticationState()
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