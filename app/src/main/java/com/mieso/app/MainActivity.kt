package com.mieso.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mieso.app.ui.theme.MieSoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This is the modern way to enable an edge-to-edge display.
        // It correctly handles drawing behind the system bars.
        enableEdgeToEdge()
        setContent {
            MieSoTheme {
                // We call MainScreen() directly. The Scaffold that provides
                // the app's structure is located inside MainScreen.
                MainScreen()
            }
        }
    }
}