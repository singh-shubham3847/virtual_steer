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
                pauseScaleX = config.ui.pauseScaleX,
                pauseScaleY = config.ui.pauseScaleY,
                camX = config.ui.camX,
                camY = config.ui.camY,
                camScaleX = config.ui.camScaleX,
                camScaleY = config.ui.camScaleY,
                lightsX = config.ui.lightsX,
                lightsY = config.ui.lightsY,
                lightsScaleX = config.ui.lightsScaleX,
                lightsScaleY = config.ui.lightsScaleY,
                gearDownX = config.ui.gearDownX,
                gearDownY = config.ui.gearDownY,
                gearDownScaleX = config.ui.gearDownScaleX,
                gearDownScaleY = config.ui.gearDownScaleY,
                handbrakeX = config.ui.handbrakeX,
                handbrakeY = config.ui.handbrakeY,
                handbrakeScaleX = config.ui.handbrakeScaleX,
                handbrakeScaleY = config.ui.handbrakeScaleY,
                gearUpX = config.ui.gearUpX,
                gearUpY = config.ui.gearUpY,
                gearUpScaleX = config.ui.gearUpScaleX,
                gearUpScaleY = config.ui.gearUpScaleY,
                radioX = config.ui.radioX,
                radioY = config.ui.radioY,
                radioScaleX = config.ui.radioScaleX,
                radioScaleY = config.ui.radioScaleY,
                lookX = config.ui.lookX,
                lookY = config.ui.lookY,
                lookScaleX = config.ui.lookScaleX,
                lookScaleY = config.ui.lookScaleY,
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
                onLookChange = { x, y -> controllerViewModel.updateLook(x, y) },
                onSaveLayout = { pX, pY, pSX, pSY, cX, cY, cSX, cSY, lX, lY, lSX, lSY, gdX, gdY, gdSX, gdSY, hX, hY, hSX, hSY, guX, guY, guSX, guSY, rX, rY, rSX, rSY, lkX, lkY, lkSX, lkSY ->
                    settingsViewModel.updateUI { u ->
                        u.copy(
                            pauseX = pX, pauseY = pY, pauseScaleX = pSX, pauseScaleY = pSY,
                            camX = cX, camY = cY, camScaleX = cSX, camScaleY = cSY,
                            lightsX = lX, lightsY = lY, lightsScaleX = lSX, lightsScaleY = lSY,
                            gearDownX = gdX, gearDownY = gdY, gearDownScaleX = gdSX, gearDownScaleY = gdSY,
                            handbrakeX = hX, handbrakeY = hY, handbrakeScaleX = hSX, handbrakeScaleY = hSY,
                            gearUpX = guX, gearUpY = guY, gearUpScaleX = guSX, gearUpScaleY = guSY,
                            radioX = rX, radioY = rY, radioScaleX = rSX, radioScaleY = rSY,
                            lookX = lkX, lookY = lkY, lookScaleX = lkSX, lookScaleY = lkSY
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
                pauseScaleX = config.ui.pauseScaleX,
                pauseScaleY = config.ui.pauseScaleY,
                camX = config.ui.camX,
                camY = config.ui.camY,
                camScaleX = config.ui.camScaleX,
                camScaleY = config.ui.camScaleY,
                lightsX = config.ui.lightsX,
                lightsY = config.ui.lightsY,
                lightsScaleX = config.ui.lightsScaleX,
                lightsScaleY = config.ui.lightsScaleY,
                gearDownX = config.ui.gearDownX,
                gearDownY = config.ui.gearDownY,
                gearDownScaleX = config.ui.gearDownScaleX,
                gearDownScaleY = config.ui.gearDownScaleY,
                handbrakeX = config.ui.handbrakeX,
                handbrakeY = config.ui.handbrakeY,
                handbrakeScaleX = config.ui.handbrakeScaleX,
                handbrakeScaleY = config.ui.handbrakeScaleY,
                gearUpX = config.ui.gearUpX,
                gearUpY = config.ui.gearUpY,
                gearUpScaleX = config.ui.gearUpScaleX,
                gearUpScaleY = config.ui.gearUpScaleY,
                radioX = config.ui.radioX,
                radioY = config.ui.radioY,
                radioScaleX = config.ui.radioScaleX,
                radioScaleY = config.ui.radioScaleY,
                lookX = config.ui.lookX,
                lookY = config.ui.lookY,
                lookScaleX = config.ui.lookScaleX,
                lookScaleY = config.ui.lookScaleY,
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
                onLookChange = { x, y -> controllerViewModel.updateLook(x, y) },
                onSaveLayout = { pX, pY, pSX, pSY, cX, cY, cSX, cSY, lX, lY, lSX, lSY, gdX, gdY, gdSX, gdSY, hX, hY, hSX, hSY, guX, guY, guSX, guSY, rX, rY, rSX, rSY, lkX, lkY, lkSX, lkSY ->
                    settingsViewModel.updateUI { u ->
                        u.copy(
                            pauseX = pX, pauseY = pY, pauseScaleX = pSX, pauseScaleY = pSY,
                            camX = cX, camY = cY, camScaleX = cSX, camScaleY = cSY,
                            lightsX = lX, lightsY = lY, lightsScaleX = lSX, lightsScaleY = lSY,
                            gearDownX = gdX, gearDownY = gdY, gearDownScaleX = gdSX, gearDownScaleY = gdSY,
                            handbrakeX = hX, handbrakeY = hY, handbrakeScaleX = hSX, handbrakeScaleY = hSY,
                            gearUpX = guX, gearUpY = guY, gearUpScaleX = guSX, gearUpScaleY = guSY,
                            radioX = rX, radioY = rY, radioScaleX = rSX, radioScaleY = rSY,
                            lookX = lkX, lookY = lkY, lookScaleX = lkSX, lookScaleY = lkSY
                        )
                    }
                    navController.popBackStack() // Go back to settings after saving layout config
                }
            )
        }
    }
}
