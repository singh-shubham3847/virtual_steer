package com.example.virtual_steer.model

data class SteeringDiagnostics(
    val rawAngle: Float = 0f,
    val calibrationOffset: Float = 0f,
    val calibratedAngle: Float = 0f,
    val deadZone: Float = 0f,
    val sensitivity: Float = 1f,
    val smoothedAngle: Float = 0f,
    val finalOutput: Float = 0f,
    val percentage: Int = 0
)

data class PedalDiagnostics(
    val type: String = "THROTTLE",
    val touchY: Float = 0f,
    val normalized: Float = 0f,
    val curve: String = "LINEAR",
    val curveOutput: Float = 0f,
    val smoothed: Float = 0f,
    val finalOutput: Float = 0f
)

data class SensorDiagnostics(
    val rotationFreq: Int = 0,
    val gyroFreq: Int = 0,
    val accelFreq: Int = 0,
    val lastTimestamp: Long = 0
)

data class NetworkDiagnostics(
    val connected: Boolean = false,
    val targetIp: String = "N/A",
    val port: Int = 0,
    val packetRate: Int = 0,
    val currentLatency: Int = 0,
    val avgLatency: Int = 0,
    val packetLoss: Int = 0
)

data class SystemDiagnostics(
    val steering: SteeringDiagnostics = SteeringDiagnostics(),
    val throttle: PedalDiagnostics = PedalDiagnostics(type = "THROTTLE"),
    val brake: PedalDiagnostics = PedalDiagnostics(type = "BRAKE"),
    val sensors: SensorDiagnostics = SensorDiagnostics(),
    val network: NetworkDiagnostics = NetworkDiagnostics(),
    val fps: Int = 0,
    val battery: Int = 0
)
