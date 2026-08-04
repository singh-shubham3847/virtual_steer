package com.example.virtual_steer.model

data class UIConfig(
    val darkTheme: Boolean = true,
    val hapticFeedback: Boolean = true,
    val showTelemetry: Boolean = true,
    val showFps: Boolean = false,
    val batteryIndicator: Boolean = true,
    val accentColor: Int = 0xFF00E676.toInt() // ThrottleGreen
)
