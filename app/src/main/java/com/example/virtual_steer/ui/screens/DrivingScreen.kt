package com.example.virtual_steer.ui.screens

import androidx.compose.foundation.background
import java.util.Locale
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.virtual_steer.ui.components.AnalogPedalZone
import com.example.virtual_steer.ui.components.PedalType
import com.example.virtual_steer.ui.components.PedalUIConfig
import com.example.virtual_steer.model.PedalResponseCurve
import com.example.virtual_steer.model.PedalDiagnostics
import com.example.virtual_steer.ui.components.RacingButton
import com.example.virtual_steer.ui.theme.*

@Composable
fun DrivingScreen(
    latencyMs: Int = 32,
    steeringAngle: Float = 0f,
    pcName: String = "Unknown PC",
    batteryLevel: Int = 100,
    packetRate: Int = 0,
    showRadio: Boolean = true,
    startInEditMode: Boolean = false,
    
    // Layout coordinates
    pauseX: Float = 0.90f,
    pauseY: Float = 0.08f,
    pauseScale: Float = 1.0f,
    camX: Float = 0.80f,
    camY: Float = 0.08f,
    camScale: Float = 1.0f,
    lightsX: Float = 0.70f,
    lightsY: Float = 0.08f,
    lightsScale: Float = 1.0f,
    gearDownX: Float = 0.38f,
    gearDownY: Float = 0.90f,
    gearDownScale: Float = 1.0f,
    handbrakeX: Float = 0.50f,
    handbrakeY: Float = 0.90f,
    handbrakeScale: Float = 1.0f,
    gearUpX: Float = 0.62f,
    gearUpY: Float = 0.90f,
    gearUpScale: Float = 1.0f,
    radioX: Float = 0.88f,
    radioY: Float = 0.50f,
    radioScale: Float = 1.0f,

    onPauseClick: () -> Unit = {},
    onCamClick: () -> Unit = {},
    onLightsClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onBrakeChange: (Float) -> Unit = {},
    onThrottleChange: (Float) -> Unit = {},
    onHandbrakeChange: (Boolean) -> Unit = {},
    onGearDownChange: (Boolean) -> Unit = {},
    onGearUpChange: (Boolean) -> Unit = {},
    onRadioClick: () -> Unit = {},
    onSaveLayout: (
        pauseX: Float, pauseY: Float, pauseScale: Float,
        camX: Float, camY: Float, camScale: Float,
        lightsX: Float, lightsY: Float, lightsScale: Float,
        gearDownX: Float, gearDownY: Float, gearDownScale: Float,
        handbrakeX: Float, handbrakeY: Float, handbrakeScale: Float,
        gearUpX: Float, gearUpY: Float, gearUpScale: Float,
        radioX: Float, radioY: Float, radioScale: Float
    ) -> Unit = { _,_,_, _,_,_, _,_,_, _,_,_, _,_,_, _,_,_, _,_,_ -> },
    onBrakeDiagnostics: (PedalDiagnostics) -> Unit = {},
    onThrottleDiagnostics: (PedalDiagnostics) -> Unit = {}
) {
    // Shared config for pedals
    val pedalConfig = remember {
        PedalUIConfig(
            smoothingEnabled = false,
            smoothingFactor = 1.0f,
            responseCurve = PedalResponseCurve.LINEAR,
            showDebug = true,
            maxDragPx = 400f
        )
    }

    var isEditingLayout by remember { mutableStateOf(startInEditMode) }

    // Coordinates states
    var pausePos by remember(pauseX, pauseY) { mutableStateOf(Offset(pauseX, pauseY)) }
    var camPos by remember(camX, camY) { mutableStateOf(Offset(camX, camY)) }
    var lightsPos by remember(lightsX, lightsY) { mutableStateOf(Offset(lightsX, lightsY)) }
    var gearDownPos by remember(gearDownX, gearDownY) { mutableStateOf(Offset(gearDownX, gearDownY)) }
    var handbrakePos by remember(handbrakeX, handbrakeY) { mutableStateOf(Offset(handbrakeX, handbrakeY)) }
    var gearUpPos by remember(gearUpX, gearUpY) { mutableStateOf(Offset(gearUpX, gearUpY)) }
    var radioPos by remember(radioX, radioY) { mutableStateOf(Offset(radioX, radioY)) }

    var pauseS by remember(pauseScale) { mutableFloatStateOf(pauseScale) }
    var camS by remember(camScale) { mutableFloatStateOf(camScale) }
    var lightsS by remember(lightsScale) { mutableFloatStateOf(lightsScale) }
    var gearDownS by remember(gearDownScale) { mutableFloatStateOf(gearDownScale) }
    var handbrakeS by remember(handbrakeScale) { mutableFloatStateOf(handbrakeScale) }
    var gearUpS by remember(gearUpScale) { mutableFloatStateOf(gearUpScale) }
    var radioS by remember(radioScale) { mutableFloatStateOf(radioScale) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(CarbonDark)
    ) {
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()
        val density = LocalDensity.current

        // Layout Editor Composable Container to handle position, drag, resize, and scale
        @Composable
        fun LayoutEditorItem(
            position: Offset,
            baseWidthDp: Int,
            baseHeightDp: Int,
            scale: Float,
            onPositionChanged: (Offset) -> Unit,
            onScaleChanged: (Float) -> Unit,
            content: @Composable () -> Unit
        ) {
            val xDp = with(density) { (position.x * screenWidthPx).toDp() }
            val yDp = with(density) { (position.y * screenHeightPx).toDp() }
            val buttonWidthDp = (baseWidthDp * scale).dp
            val buttonHeightDp = (baseHeightDp * scale).dp

            Box(
                modifier = Modifier
                    .offset(
                        x = xDp - buttonWidthDp / 2,
                        y = yDp - buttonHeightDp / 2
                    )
                    .size(width = buttonWidthDp, height = buttonHeightDp)
                    .pointerInput(isEditingLayout) {
                        if (!isEditingLayout) return@pointerInput
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val newX = (position.x + dragAmount.x / screenWidthPx).coerceIn(0.02f, 0.98f)
                            val newY = (position.y + dragAmount.y / screenHeightPx).coerceIn(0.02f, 0.98f)
                            onPositionChanged(Offset(newX, newY))
                        }
                    }
                    .then(
                        if (isEditingLayout) {
                            Modifier.border(1.5.dp, AccentYellow, RoundedCornerShape(8.dp))
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                content()

                if (isEditingLayout) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-24).dp)
                            .background(CarbonDark.copy(alpha = 0.85f), RoundedCornerShape(4.dp))
                            .border(0.5.dp, AccentYellow, RoundedCornerShape(4.dp))
                            .padding(horizontal = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onScaleChanged((scale - 0.1f).coerceIn(0.5f, 2.0f)) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("-", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = String.format(Locale.US, "%.1fx", scale),
                            color = AccentYellow,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onScaleChanged((scale + 0.1f).coerceIn(0.5f, 2.0f)) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("+", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // ==========================================
        // 1. INTERACTION ZONES (Bottom Half)
        // Only active if not editing layout
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .align(Alignment.BottomCenter)
        ) {
            // LEFT ZONE: BRAKE
            Box(modifier = Modifier.weight(1f)) {
                AnalogPedalZone(
                    type = PedalType.BRAKE,
                    onValueChange = { if (!isEditingLayout) onBrakeChange(it) },
                    config = pedalConfig,
                    onDiagnosticsUpdate = onBrakeDiagnostics
                )
            }
            
            // RIGHT ZONE: THROTTLE
            Box(modifier = Modifier.weight(1f)) {
                AnalogPedalZone(
                    type = PedalType.THROTTLE,
                    onValueChange = { if (!isEditingLayout) onThrottleChange(it) },
                    config = pedalConfig,
                    onDiagnosticsUpdate = onThrottleDiagnostics
                )
            }
        }

        // ==========================================
        // 2. HUD CONTROLS (Top)
        // ==========================================

        // TOP-LEFT: Status
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 16.dp, start = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            HudStatRow("PC", pcName)
        }

        // TOP-CENTER: Control Bar
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isEditingLayout) {
                HudButton(
                    label = "💾 SAVE & EXIT",
                    onClick = {
                        isEditingLayout = false
                        onSaveLayout(
                            pausePos.x, pausePos.y, pauseS,
                            camPos.x, camPos.y, camS,
                            lightsPos.x, lightsPos.y, lightsS,
                            gearDownPos.x, gearDownPos.y, gearDownS,
                            handbrakePos.x, handbrakePos.y, handbrakeS,
                            gearUpPos.x, gearUpPos.y, gearUpS,
                            radioPos.x, radioPos.y, radioS
                        )
                    },
                    color = ThrottleGreen
                )
            } else {
                HudButton(label = "STOP DRIVING", onClick = onBackClick, color = BrakeRed)
            }
        }

        // ==========================================
        // 3. DRAGGABLE & RESIZEABLE VIRTUAL BUTTONS
        // ==========================================

        // PAUSE Button
        LayoutEditorItem(
            position = pausePos,
            baseWidthDp = 68,
            baseHeightDp = 42,
            scale = pauseS,
            onPositionChanged = { pausePos = it },
            onScaleChanged = { pauseS = it }
        ) {
            HudButton(
                label = "Pause",
                modifier = Modifier.fillMaxSize(),
                onClick = { if (!isEditingLayout) onPauseClick() }
            )
        }

        // CAM Button
        LayoutEditorItem(
            position = camPos,
            baseWidthDp = 68,
            baseHeightDp = 42,
            scale = camS,
            onPositionChanged = { camPos = it },
            onScaleChanged = { camS = it }
        ) {
            HudButton(
                label = "Cam",
                modifier = Modifier.fillMaxSize(),
                onClick = { if (!isEditingLayout) onCamClick() }
            )
        }

        // LIGHTS Button
        LayoutEditorItem(
            position = lightsPos,
            baseWidthDp = 68,
            baseHeightDp = 42,
            scale = lightsS,
            onPositionChanged = { lightsPos = it },
            onScaleChanged = { lightsS = it }
        ) {
            HudButton(
                label = "Lights",
                modifier = Modifier.fillMaxSize(),
                onClick = { if (!isEditingLayout) onLightsClick() }
            )
        }

        // GEAR DOWN Button
        LayoutEditorItem(
            position = gearDownPos,
            baseWidthDp = 64,
            baseHeightDp = 42,
            scale = gearDownS,
            onPositionChanged = { gearDownPos = it },
            onScaleChanged = { gearDownS = it }
        ) {
            RacingButton(
                text = "GEAR-",
                modifier = Modifier.fillMaxSize(),
                onPressedChange = { if (!isEditingLayout) onGearDownChange(it) }
            ) {}
        }

        // HANDBRAKE Button
        LayoutEditorItem(
            position = handbrakePos,
            baseWidthDp = 70,
            baseHeightDp = 42,
            scale = handbrakeS,
            onPositionChanged = { handbrakePos = it },
            onScaleChanged = { handbrakeS = it }
        ) {
            RacingButton(
                text = "HBRAKE",
                modifier = Modifier.fillMaxSize(),
                onPressedChange = { if (!isEditingLayout) onHandbrakeChange(it) }
            ) {}
        }

        // GEAR UP Button
        LayoutEditorItem(
            position = gearUpPos,
            baseWidthDp = 64,
            baseHeightDp = 42,
            scale = gearUpS,
            onPositionChanged = { gearUpPos = it },
            onScaleChanged = { gearUpS = it }
        ) {
            RacingButton(
                text = "GEAR+",
                modifier = Modifier.fillMaxSize(),
                onPressedChange = { if (!isEditingLayout) onGearUpChange(it) }
            ) {}
        }

        // RADIO Button
        if (showRadio) {
            LayoutEditorItem(
                position = radioPos,
                baseWidthDp = 80,
                baseHeightDp = 42,
                scale = radioS,
                onPositionChanged = { radioPos = it },
                onScaleChanged = { radioS = it }
            ) {
                RacingButton(
                    text = "📻 RADIO",
                    modifier = Modifier.fillMaxSize(),
                    onClick = { if (!isEditingLayout) onRadioClick() }
                )
            }
        }

        // Layout Editor Hint Overlay
        if (isEditingLayout) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .border(2.dp, AccentYellow, RoundedCornerShape(0.dp)),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    text = "LAYOUT EDITOR ACTIVE: DRAG ANY BUTTON TO REARRANGE, THEN CLICK 'SAVE'",
                    color = AccentYellow,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun HudStatRow(label: String, value: String, color: Color = Color.White) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "$label:",
            color = Color.White.copy(alpha = 0.5f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = value,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun HudButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.9f)
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(GridPanelBg)
            .border(1.dp, MetallicBorder, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.uppercase(),
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}
