package com.example.virtual_steer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.virtual_steer.ui.screens.*
import com.example.virtual_steer.viewmodel.ControllerViewModel
import com.example.virtual_steer.viewmodel.SettingsViewModel

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    controllerViewModel: ControllerViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val steeringAngle by controllerViewModel.steeringAngle.collectAsState()
    val connectionState by controllerViewModel.connectionState.collectAsState()
    val diagnostics by controllerViewModel.diagnostics.collectAsState()
    val config by settingsViewModel.config.collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = Screen.Pair.route
    ) {
        composable(Screen.Pair.route) {
            PairScreen(
                viewModel = controllerViewModel,
                onPairSuccess = { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Pair.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                connectionStatus = connectionState.status,
                pcName = connectionState.serverName,
                latencyMs = connectionState.latencyMs,
                batteryLevel = diagnostics.battery,
                onStartDriving = { navController.navigate(Screen.Driving.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onDiagnosticsClick = { navController.navigate(Screen.Diagnostics.route) }
            )
        }

        composable(Screen.Driving.route) {
            DrivingScreen(
                steeringAngle = steeringAngle,
                pcName = connectionState.serverName,
                latencyMs = diagnostics.network.latencyMs,
                packetRate = diagnostics.network.packetRate,
                showRadio = config.ui.showRadio,
                pauseX = config.ui.pauseX,
                pauseY = config.ui.pauseY,
                pauseScale = config.ui.pauseScale,
                camX = config.ui.camX,
                camY = config.ui.camY,
                camScale = config.ui.camScale,
                lightsX = config.ui.lightsX,
                lightsY = config.ui.lightsY,
                lightsScale = config.ui.lightsScale,
                gearDownX = config.ui.gearDownX,
                gearDownY = config.ui.gearDownY,
                gearDownScale = config.ui.gearDownScale,
                handbrakeX = config.ui.handbrakeX,
                handbrakeY = config.ui.handbrakeY,
                handbrakeScale = config.ui.handbrakeScale,
                gearUpX = config.ui.gearUpX,
                gearUpY = config.ui.gearUpY,
                gearUpScale = config.ui.gearUpScale,
                radioX = config.ui.radioX,
                radioY = config.ui.radioY,
                radioScale = config.ui.radioScale,
                onBackClick = { navController.popBackStack() },
                onBrakeChange = { controllerViewModel.updateBrake(it) },
                onThrottleChange = { controllerViewModel.updateThrottle(it) },
                onHandbrakeChange = { controllerViewModel.updateHandbrake(it) },
                onGearDownChange = { controllerViewModel.updateGearDown(it) },
                onGearUpChange = { controllerViewModel.updateGearUp(it) },
                onPauseClick = { controllerViewModel.pulsePause() },
                onCamClick = { controllerViewModel.pulseCamera() },
                onLightsClick = { controllerViewModel.pulseHeadlights() },
                onRadioClick = { controllerViewModel.pulseDpadRight() },
                onSaveLayout = { pX, pY, pS, cX, cY, cS, lX, lY, lS, gdX, gdY, gdS, hX, hY, hS, guX, guY, guS, rX, rY, rS ->
                    settingsViewModel.updateUI { u ->
                        u.copy(
                            pauseX = pX, pauseY = pY, pauseScale = pS,
                            camX = cX, camY = cY, camScale = cS,
                            lightsX = lX, lightsY = lY, lightsScale = lS,
                            gearDownX = gdX, gearDownY = gdY, gearDownScale = gdS,
                            handbrakeX = hX, handbrakeY = hY, handbrakeScale = hS,
                            gearUpX = guX, gearUpY = guY, gearUpScale = guS,
                            radioX = rX, radioY = rY, radioScale = rS
                        )
                    }
                }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onBackClick = { navController.popBackStack() },
                onCalibrationClick = { navController.navigate(Screen.Calibration.route) },
                onDiagnosticsClick = { navController.navigate(Screen.Diagnostics.route) },
                onCustomizeLayoutClick = { navController.navigate(Screen.LayoutEditor.route) }
            )
        }
        
        composable(Screen.Calibration.route) {
            CalibrationScreen(
                viewModel = controllerViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Diagnostics.route) {
            DiagnosticsScreen(
                viewModel = controllerViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.LayoutEditor.route) {
            DrivingScreen(
                steeringAngle = steeringAngle,
                pcName = connectionState.serverName,
                latencyMs = diagnostics.network.latencyMs,
                packetRate = diagnostics.network.packetRate,
                showRadio = config.ui.showRadio,
                startInEditMode = true,
                pauseX = config.ui.pauseX,
                pauseY = config.ui.pauseY,
                pauseScale = config.ui.pauseScale,
                camX = config.ui.camX,
                camY = config.ui.camY,
                camScale = config.ui.camScale,
                lightsX = config.ui.lightsX,
                lightsY = config.ui.lightsY,
                lightsScale = config.ui.lightsScale,
                gearDownX = config.ui.gearDownX,
                gearDownY = config.ui.gearDownY,
                gearDownScale = config.ui.gearDownScale,
                handbrakeX = config.ui.handbrakeX,
                handbrakeY = config.ui.handbrakeY,
                handbrakeScale = config.ui.handbrakeScale,
                gearUpX = config.ui.gearUpX,
                gearUpY = config.ui.gearUpY,
                gearUpScale = config.ui.gearUpScale,
                radioX = config.ui.radioX,
                radioY = config.ui.radioY,
                radioScale = config.ui.radioScale,
                onBackClick = { navController.popBackStack() },
                onBrakeChange = { controllerViewModel.updateBrake(it) },
                onThrottleChange = { controllerViewModel.updateThrottle(it) },
                onHandbrakeChange = { controllerViewModel.updateHandbrake(it) },
                onGearDownChange = { controllerViewModel.updateGearDown(it) },
                onGearUpChange = { controllerViewModel.updateGearUp(it) },
                onPauseClick = { controllerViewModel.pulsePause() },
                onCamClick = { controllerViewModel.pulseCamera() },
                onLightsClick = { controllerViewModel.pulseHeadlights() },
                onRadioClick = { controllerViewModel.pulseDpadRight() },
                onSaveLayout = { pX, pY, pS, cX, cY, cS, lX, lY, lS, gdX, gdY, gdS, hX, hY, hS, guX, guY, guS, rX, rY, rS ->
                    settingsViewModel.updateUI { u ->
                        u.copy(
                            pauseX = pX, pauseY = pY, pauseScale = pS,
                            camX = cX, camY = cY, camScale = cS,
                            lightsX = lX, lightsY = lY, lightsScale = lS,
                            gearDownX = gdX, gearDownY = gdY, gearDownScale = gdS,
                            handbrakeX = hX, handbrakeY = hY, handbrakeScale = hS,
                            gearUpX = guX, gearUpY = guY, gearUpScale = guS,
                            radioX = rX, radioY = rY, radioScale = rS
                        )
                    }
                    navController.popBackStack() // Go back to settings after saving layout config
                }
            )
        }
    }
}
