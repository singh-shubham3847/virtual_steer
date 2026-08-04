package com.example.virtual_steer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virtual_steer.model.PedalResponseCurve
import com.example.virtual_steer.ui.components.*
import com.example.virtual_steer.ui.theme.*
import com.example.virtual_steer.viewmodel.SettingsViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    onCalibrationClick: () -> Unit,
    onDiagnosticsClick: () -> Unit
) {
    val config by viewModel.config.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "TUNING MENU",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("< BACK", color = AccentYellow, fontFamily = FontFamily.Monospace)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CarbonDark,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = CarbonDark
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            
            // STEERING
            RacingHeader("Steering")
            TuningCard("Core Handling") {
                RacingSlider(
                    label = "Sensitivity",
                    value = config.steering.sensitivity,
                    onValueChange = { viewModel.updateSteering { s -> s.copy(sensitivity = it) } },
                    valueRange = 0.5f..2.0f,
                    displayValue = String.format(Locale.US, "%.0f%%", config.steering.sensitivity * 100)
                )
                RacingSlider(
                    label = "Dead Zone",
                    value = config.steering.deadZone,
                    onValueChange = { viewModel.updateSteering { s -> s.copy(deadZone = it) } },
                    valueRange = 0f..0.2f,
                    displayValue = String.format(Locale.US, "%.1f%%", config.steering.deadZone * 100)
                )
                RacingSlider(
                    label = "Smoothing",
                    value = config.steering.smoothing,
                    onValueChange = { viewModel.updateSteering { s -> s.copy(smoothing = it) } },
                    valueRange = 0.01f..0.5f,
                    displayValue = String.format(Locale.US, "%.0f%%", config.steering.smoothing * 100)
                )
                RacingSwitch(
                    label = "Invert Steering",
                    checked = config.steering.invertSteering,
                    onCheckedChange = { viewModel.updateSteering { s -> s.copy(invertSteering = it) } }
                )
            }

            // PEDALS
            RacingHeader("Pedals")
            TuningCard("Throttle Response") {
                PedalResponseCurve.entries.forEach { curve ->
                    RacingRadioButton(
                        label = curve.name.lowercase().replaceFirstChar { it.uppercase() },
                        selected = config.pedals.throttleCurve == curve,
                        onClick = { viewModel.updatePedals { p -> p.copy(throttleCurve = curve) } }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TuningCard("Brake Response") {
                PedalResponseCurve.entries.forEach { curve ->
                    RacingRadioButton(
                        label = curve.name.lowercase().replaceFirstChar { it.uppercase() },
                        selected = config.pedals.brakeCurve == curve,
                        onClick = { viewModel.updatePedals { p -> p.copy(brakeCurve = curve) } }
                    )
                }
            }

            // ADVANCED
            RacingHeader("Advanced")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HudButton(label = "CALIBRATE", onClick = onCalibrationClick, modifier = Modifier.weight(1f))
                HudButton(label = "DIAGNOSTICS", onClick = onDiagnosticsClick, modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { viewModel.resetToDefaults() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BrakeRed),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("RESET TO DEFAULTS", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun HudButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(GridPanelBg)
            .border(1.dp, MetallicBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
