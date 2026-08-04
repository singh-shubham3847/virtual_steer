package com.example.virtual_steer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.virtual_steer.ui.screens.HomeScreen
import com.example.virtual_steer.ui.screens.SettingsScreen
import com.example.virtual_steer.viewmodel.SteeringViewModel
import com.example.virtual_steer.viewmodel.SettingsViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    steeringViewModel: SteeringViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val steeringAngle by steeringViewModel.steeringAngle.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                steeringAngle = steeringAngle,
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onBrakeChange = { steeringViewModel.updateBrake(it) },
                onThrottleChange = { steeringViewModel.updateThrottle(it) }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onBackClick = { navController.popBackStack() },
                onCalibrationClick = { navController.navigate(Screen.Calibration.route) },
                onDiagnosticsClick = { navController.navigate(Screen.Diagnostics.route) }
            )
        }
        
        composable(Screen.Calibration.route) {
            // Placeholder
        }
        
        composable(Screen.Diagnostics.route) {
            // Placeholder
        }
    }
}
