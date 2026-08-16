package com.example.virtual_steer.ui.screens

import androidx.compose.foundation.background
import java.util.Locale
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberUpdatedState
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
    pauseScaleX: Float = 1.0f,
    pauseScaleY: Float = 1.0f,
    camX: Float = 0.80f,
    camY: Float = 0.08f,
    camScaleX: Float = 1.0f,
    camScaleY: Float = 1.0f,
    lightsX: Float = 0.70f,
    lightsY: Float = 0.08f,
    lightsScaleX: Float = 1.0f,
    lightsScaleY: Float = 1.0f,
    gearDownX: Float = 0.38f,
    gearDownY: Float = 0.90f,
    gearDownScaleX: Float = 1.0f,
    gearDownScaleY: Float = 1.0f,
    handbrakeX: Float = 0.50f,
    handbrakeY: Float = 0.90f,
    handbrakeScaleX: Float = 1.0f,
    handbrakeScaleY: Float = 1.0f,
    gearUpX: Float = 0.62f,
    gearUpY: Float = 0.90f,
    gearUpScaleX: Float = 1.0f,
    gearUpScaleY: Float = 1.0f,
    radioX: Float = 0.88f,
    radioY: Float = 0.50f,
    radioScaleX: Float = 1.0f,
    radioScaleY: Float = 1.0f,

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
        pauseX: Float, pauseY: Float, pauseScaleX: Float, pauseScaleY: Float,
        camX: Float, camY: Float, camScaleX: Float, camScaleY: Float,
        lightsX: Float, lightsY: Float, lightsScaleX: Float, lightsScaleY: Float,
        gearDownX: Float, gearDownY: Float, gearDownScaleX: Float, gearDownScaleY: Float,
        handbrakeX: Float, handbrakeY: Float, handbrakeScaleX: Float, handbrakeScaleY: Float,
        gearUpX: Float, gearUpY: Float, gearUpScaleX: Float, gearUpScaleY: Float,
        radioX: Float, radioY: Float, radioScaleX: Float, radioScaleY: Float
    ) -> Unit = { _,_,_,_, _,_,_,_, _,_,_,_, _,_,_,_, _,_,_,_, _,_,_,_, _,_,_,_ -> },
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

    var pauseSX by remember(pauseScaleX) { mutableFloatStateOf(pauseScaleX) }
    var pauseSY by remember(pauseScaleY) { mutableFloatStateOf(pauseScaleY) }
    var camSX by remember(camScaleX) { mutableFloatStateOf(camScaleX) }
    var camSY by remember(camScaleY) { mutableFloatStateOf(camScaleY) }
    var lightsSX by remember(lightsScaleX) { mutableFloatStateOf(lightsScaleX) }
    var lightsSY by remember(lightsScaleY) { mutableFloatStateOf(lightsScaleY) }
    var gearDownSX by remember(gearDownScaleX) { mutableFloatStateOf(gearDownScaleX) }
    var gearDownSY by remember(gearDownScaleY) { mutableFloatStateOf(gearDownScaleY) }
    var handbrakeSX by remember(handbrakeScaleX) { mutableFloatStateOf(handbrakeScaleX) }
    var handbrakeSY by remember(handbrakeScaleY) { mutableFloatStateOf(handbrakeScaleY) }
    var gearUpSX by remember(gearUpScaleX) { mutableFloatStateOf(gearUpScaleX) }
    var gearUpSY by remember(gearUpScaleY) { mutableFloatStateOf(gearUpScaleY) }
    var radioSX by remember(radioScaleX) { mutableFloatStateOf(radioScaleX) }
    var radioSY by remember(radioScaleY) { mutableFloatStateOf(radioScaleY) }

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
            scaleX: Float,
            scaleY: Float,
            onPositionChanged: (Offset) -> Unit,
            onScaleXChanged: (Float) -> Unit,
            onScaleYChanged: (Float) -> Unit,
            content: @Composable () -> Unit
        ) {
            val currentPosition by rememberUpdatedState(position)
            val currentOnPositionChanged by rememberUpdatedState(onPositionChanged)
            val currentScaleX by rememberUpdatedState(scaleX)
            val currentOnScaleXChanged by rememberUpdatedState(onScaleXChanged)
            val currentScaleY by rememberUpdatedState(scaleY)
            val currentOnScaleYChanged by rememberUpdatedState(onScaleYChanged)

            val xDp = with(density) { (currentPosition.x * screenWidthPx).toDp() }
            val yDp = with(density) { (currentPosition.y * screenHeightPx).toDp() }
            val buttonWidthDp = (baseWidthDp * scaleX).dp
            val buttonHeightDp = (baseHeightDp * scaleY).dp

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
                            val newX = (currentPosition.x + dragAmount.x / screenWidthPx).coerceIn(0.02f, 0.98f)
                            val newY = (currentPosition.y + dragAmount.y / screenHeightPx).coerceIn(0.02f, 0.98f)
                            currentOnPositionChanged(Offset(newX, newY))
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
                    val handleSize = 14.dp
                    val handleColor = AccentYellow

                    // LEFT Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = (-7).dp)
                            .size(handleSize)
                            .clip(CircleShape)
                            .background(handleColor)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val deltaScaleX = dragAmount.x / density.run { baseWidthDp.dp.toPx() }
                                    currentOnScaleXChanged((currentScaleX - deltaScaleX).coerceIn(0.5f, 3.0f))
                                }
                            }
                    )

                    // RIGHT Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = 7.dp)
                            .size(handleSize)
                            .clip(CircleShape)
                            .background(handleColor)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val deltaScaleX = dragAmount.x / density.run { baseWidthDp.dp.toPx() }
                                    currentOnScaleXChanged((currentScaleX + deltaScaleX).coerceIn(0.5f, 3.0f))
                                }
                            }
                    )

                    // TOP Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-7).dp)
                            .size(handleSize)
                            .clip(CircleShape)
                            .background(handleColor)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val deltaScaleY = dragAmount.y / density.run { baseHeightDp.dp.toPx() }
                                    currentOnScaleYChanged((currentScaleY - deltaScaleY).coerceIn(0.5f, 3.0f))
                                }
                            }
                    )

                    // BOTTOM Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 7.dp)
                            .size(handleSize)
                            .clip(CircleShape)
                            .background(handleColor)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val deltaScaleY = dragAmount.y / density.run { baseHeightDp.dp.toPx() }
                                    currentOnScaleYChanged((currentScaleY + deltaScaleY).coerceIn(0.5f, 3.0f))
                                }
                            }
                    )
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
                            pausePos.x, pausePos.y, pauseSX, pauseSY,
                            camPos.x, camPos.y, camSX, camSY,
                            lightsPos.x, lightsPos.y, lightsSX, lightsSY,
                            gearDownPos.x, gearDownPos.y, gearDownSX, gearDownSY,
                            handbrakePos.x, handbrakePos.y, handbrakeSX, handbrakeSY,
                            gearUpPos.x, gearUpPos.y, gearUpSX, gearUpSY,
                            radioPos.x, radioPos.y, radioSX, radioSY
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
            scaleX = pauseSX,
            scaleY = pauseSY,
            onPositionChanged = { pausePos = it },
            onScaleXChanged = { pauseSX = it },
            onScaleYChanged = { pauseSY = it }
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
            scaleX = camSX,
            scaleY = camSY,
            onPositionChanged = { camPos = it },
            onScaleXChanged = { camSX = it },
            onScaleYChanged = { camSY = it }
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
            scaleX = lightsSX,
            scaleY = lightsSY,
            onPositionChanged = { lightsPos = it },
            onScaleXChanged = { lightsSX = it },
            onScaleYChanged = { lightsSY = it }
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
            scaleX = gearDownSX,
            scaleY = gearDownSY,
            onPositionChanged = { gearDownPos = it },
            onScaleXChanged = { gearDownSX = it },
            onScaleYChanged = { gearDownSY = it }
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
            scaleX = handbrakeSX,
            scaleY = handbrakeSY,
            onPositionChanged = { handbrakePos = it },
            onScaleXChanged = { handbrakeSX = it },
            onScaleYChanged = { handbrakeSY = it }
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
            scaleX = gearUpSX,
            scaleY = gearUpSY,
            onPositionChanged = { gearUpPos = it },
            onScaleXChanged = { gearUpSX = it },
            onScaleYChanged = { gearUpSY = it }
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
                scaleX = radioSX,
                scaleY = radioSY,
                onPositionChanged = { radioPos = it },
                onScaleXChanged = { radioSX = it },
                onScaleYChanged = { radioSY = it }
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
                    text = "DRAG HANDLES TO RESHAPE/RESIZE • DRAG CENTER TO MOVE BUTTON",
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
