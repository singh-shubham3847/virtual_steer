package com.example.virtual_steer.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object Calibration : Screen("calibration")
    object Diagnostics : Screen("diagnostics")
}
