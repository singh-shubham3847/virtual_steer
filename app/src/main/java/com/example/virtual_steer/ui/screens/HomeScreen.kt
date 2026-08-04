package com.example.virtual_steer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.virtual_steer.ui.components.PedalSide
import com.example.virtual_steer.ui.components.PedalType
import com.example.virtual_steer.ui.components.RadialPedal
import com.example.virtual_steer.viewmodel.SteeringViewModel

@Composable
fun HomeScreen(
    viewModel: SteeringViewModel = viewModel()
) {
    DisposableEffect(Unit) {
        viewModel.startSensor()
        onDispose {
            viewModel.stopSensor()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Background color for HUD feel
            .padding(24.dp)
    ) {
        // --- BRAKE (LEFT RADIAL ARC) ---
        RadialPedal(
            type = PedalType.BRAKE,
            side = PedalSide.LEFT,
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomStart),
            onValueChange = { value ->
                viewModel.updateBrake(value)
            }
        )

        // --- CENTER CONTROLS ---
        // Placeholder for steering wheel or connection status

        // --- THROTTLE (RIGHT RADIAL ARC) ---
        RadialPedal(
            type = PedalType.THROTTLE,
            side = PedalSide.RIGHT,
            modifier = Modifier
                .size(220.dp)
                .align(Alignment.BottomEnd),
            onValueChange = { value ->
                viewModel.updateThrottle(value)
            }
        )
    }
}
