package com.example.virtual_steer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.virtual_steer.navigation.NavGraph
import com.example.virtual_steer.ui.theme.Virtual_steerTheme
import com.example.virtual_steer.viewmodel.ControllerViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Hide the status bar and system navigation buttons (Immersive Mode)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        setContent {
            Virtual_steerTheme {
                val viewModel: ControllerViewModel = viewModel()

                LaunchedEffect(Unit) {
                    viewModel.startSensor()
                }

                NavGraph(controllerViewModel = viewModel)
            }
        }
    }
}
