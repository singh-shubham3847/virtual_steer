package com.example.virtual_steer.model

data class SteeringConfig(
    val sensitivity: Float = 1.0f,
    val deadZone: Float = 0.02f,
    val maxAngle: Int = 270,
    val smoothing: Float = 0.18f,
    val invertSteering: Boolean = false,
    val autoCalibration: Boolean = true
)
