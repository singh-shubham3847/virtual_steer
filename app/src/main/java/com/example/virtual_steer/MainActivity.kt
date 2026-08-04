package com.example.virtual_steer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.virtual_steer.ui.screens.HomeScreen
import com.example.virtual_steer.ui.theme.Virtual_steerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Virtual_steerTheme {
                HomeScreen()
            }
        }
    }
}