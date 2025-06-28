package com.mieso.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mieso.app.ui.auth.AuthScreen
import com.mieso.app.ui.navigation.Screen
import com.mieso.app.ui.theme.MieSoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MieSoTheme {
                val navController = rememberNavController()
                val authStatus by viewModel.authStatus.collectAsState()

                when (authStatus) {
                    AuthStatus.LOADING -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    AuthStatus.AUTHENTICATED -> {
                        NavHost(navController = navController, startDestination = Screen.Main.route) {
                            composable(Screen.Main.route) {
                                MainScreen()
                            }
                        }
                    }
                    AuthStatus.UNAUTHENTICATED -> {
                        NavHost(navController = navController, startDestination = Screen.Login.route) {
                            composable(Screen.Login.route) {
                                AuthScreen(
                                    onSignInSuccess = {
                                        // The MainViewModel will automatically handle the state change
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}