// File: app/src/main/java/com/mieso/app/MainActivity.kt

package com.mieso.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mieso.app.data.repository.AuthRepository
import com.mieso.app.ui.auth.AuthScreen
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

                NavHost(navController = navController, startDestination = "auth_gate") {

                    composable("auth_gate") {
                        val authState by authRepository.getAuthState().collectAsState(initial = null)

                        // LaunchedEffect ini adalah kunci dari alur navigasi kita.
                        // Ia akan berjalan setiap kali authState berubah.
                        LaunchedEffect(authState) {
                            if (authState != null) {
                                // Jika ada pengguna yang login (authState bukan null),
                                // navigasi ke MainScreen.
                                navController.navigate(Screen.Main.route) {
                                    popUpTo("auth_gate") { inclusive = true }
                                }
                            } else {
                                // Jika tidak ada pengguna yang login (authState adalah null),
                                // navigasi ke AuthScreen (Login.route).
                                navController.navigate(Screen.Login.route) {
                                    popUpTo("auth_gate") { inclusive = true }
                                }
                            }
                        }

                        // Menampilkan indikator loading selama proses pengecekan status.
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

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
