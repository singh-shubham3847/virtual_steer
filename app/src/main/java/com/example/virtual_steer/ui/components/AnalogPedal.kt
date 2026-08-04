package com.example.virtual_steer.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virtual_steer.ui.theme.*
import kotlin.math.roundToInt

enum class PedalType {
    THROTTLE, BRAKE
}

enum class PedalSide {
    LEFT, RIGHT
}

@Composable
fun RadialPedal(
    type: PedalType,
    side: PedalSide,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit
) {
    var rawProgress by remember { mutableFloatStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 16),
        label = "RadialPedalValue"
    )

    val primaryColor = (if (type == PedalType.THROTTLE) ThrottleGreen else BrakeRed).copy(alpha = 0.6f)
    val trackBgColor = Color(0x22FFFFFF)

    // Interaction zone: vertical drag to control progress
    // Max drag distance for 0 to 100%
    val maxDragPx = 400f

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        // Dragging up (negative y) increases progress
                        val delta = -dragAmount.y / maxDragPx
                        rawProgress = (rawProgress + delta).coerceIn(0f, 1f)
                        onValueChange(rawProgress)
                    },
                    onDragEnd = {
                        rawProgress = 0f
                        onValueChange(0f)
                    },
                    onDragCancel = {
                        rawProgress = 0f
                        onValueChange(0f)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        if (animatedProgress > 0f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 24f
                val diameter = size.minDimension - strokeWidth
                val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
                val arcSize = Size(diameter, diameter)

                // Arc settings based on side
                // Right side (Throttle): sweep from bottom-ish to top-ish clockwise
                // Left side (Brake): sweep from bottom-ish to top-ish counter-clockwise
                val startAngle = if (side == PedalSide.RIGHT) 150f else 30f
                val sweepAngle = if (side == PedalSide.RIGHT) -210f else 210f

                // 1. Background Track
                drawArc(
                    color = trackBgColor,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // 2. Progress Arc
                drawArc(
                    color = primaryColor,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle * animatedProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Telemetry Text (Label Only)
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = if (type == PedalType.THROTTLE) "THROTTLE" else "BRAKE",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
