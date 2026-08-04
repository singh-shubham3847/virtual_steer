package com.example.virtual_steer.model

enum class PedalResponseCurve {
    LINEAR, RACING, AGGRESSIVE
}

data class PedalConfig(
    val throttleCurve: PedalResponseCurve = PedalResponseCurve.RACING,
    val brakeCurve: PedalResponseCurve = PedalResponseCurve.RACING,
    val pedalSmoothing: Float = 0.20f,
    val pedalDeadZone: Float = 0.05f
)
