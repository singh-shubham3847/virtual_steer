package com.example.virtual_steer.engine

import com.example.virtual_steer.model.ControllerConfig
import com.example.virtual_steer.model.ControllerState
import com.example.virtual_steer.model.NetworkDiagnostics
import com.example.virtual_steer.model.SystemDiagnostics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ControllerEngine {

    private var config = ControllerConfig()
    
    private val steeringProcessor = SteeringProcessor(config.steering)
    private val throttleProcessor = PedalProcessor(config.pedals, isBrake = false)
    private val brakeProcessor = PedalProcessor(config.pedals, isBrake = true)

    private val _controllerState = MutableStateFlow(ControllerState())
    val controllerState: StateFlow<ControllerState> = _controllerState
    
    private val _diagnostics = MutableStateFlow(SystemDiagnostics())
    val diagnostics: StateFlow<SystemDiagnostics> = _diagnostics

    fun updateConfig(newConfig: ControllerConfig) {
        config = newConfig
        steeringProcessor.updateConfig(config.steering)
        throttleProcessor.updateConfig(config.pedals)
        brakeProcessor.updateConfig(config.pedals)
    }

    fun updateSteering(rawValue: Float) {
        val diag = steeringProcessor.process(rawValue)
        _controllerState.update { it.copy(steering = diag.finalOutput) }
        _diagnostics.update { it.copy(steering = diag) }
    }

    fun updateThrottle(value: Float) {
        val diag = throttleProcessor.process(value, config.pedals.throttleCurve)
        _controllerState.update { it.copy(throttle = diag.finalOutput) }
        _diagnostics.update { it.copy(throttle = diag) }
    }

    fun updateBrake(value: Float) {
        val diag = brakeProcessor.process(value, config.pedals.brakeCurve)
        _controllerState.update { it.copy(brake = diag.finalOutput) }
        _diagnostics.update { it.copy(brake = diag) }
    }

    fun updateClutch(value: Float) {
        _controllerState.update { it.copy(clutch = value) }
    }

    fun updateLook(x: Float, y: Float) {
        _controllerState.update { it.copy(lookX = x, lookY = y) }
    }

    fun updateNetworkDiagnostics(network: NetworkDiagnostics) {
        _diagnostics.update { it.copy(network = network) }
    }

    private val lastPressTimes = java.util.concurrent.ConcurrentHashMap<String, Long>()
    private val BUTTON_COOLDOWN_MS = 250L

    private fun checkCooldown(key: String, value: Boolean): Boolean {
        if (value) {
            val now = System.currentTimeMillis()
            val last = lastPressTimes[key] ?: 0L
            if (now - last < BUTTON_COOLDOWN_MS) {
                return false
            }
            lastPressTimes[key] = now
        }
        return true
    }

    fun updateHandbrake(value: Boolean) {
        if (!checkCooldown("handbrake", value)) return
        _controllerState.update { it.copy(handbrake = value) }
    }

    fun updateGearUp(value: Boolean) {
        if (!checkCooldown("gearUp", value)) return
        _controllerState.update { it.copy(gearUp = value) }
    }

    fun updateGearDown(value: Boolean) {
        if (!checkCooldown("gearDown", value)) return
        _controllerState.update { it.copy(gearDown = value) }
    }

    fun updatePause(value: Boolean) {
        if (!checkCooldown("pause", value)) return
        _controllerState.update { it.copy(pause = value) }
    }

    fun updateHorn(value: Boolean) {
        if (!checkCooldown("horn", value)) return
        _controllerState.update { it.copy(horn = value) }
    }

    fun updateHeadlights(value: Boolean) {
        if (!checkCooldown("headlights", value)) return
        _controllerState.update { it.copy(headlights = value) }
    }

    fun updateCamera(value: Boolean) {
        if (!checkCooldown("camera", value)) return
        _controllerState.update { it.copy(camera = value) }
    }

    fun updateDpadUp(value: Boolean) {
        if (!checkCooldown("dpadUp", value)) return
        _controllerState.update { it.copy(dpadUp = value) }
    }

    fun updateDpadDown(value: Boolean) {
        if (!checkCooldown("dpadDown", value)) return
        _controllerState.update { it.copy(dpadDown = value) }
    }

    fun updateDpadLeft(value: Boolean) {
        if (!checkCooldown("dpadLeft", value)) return
        _controllerState.update { it.copy(dpadLeft = value) }
    }

    fun updateDpadRight(value: Boolean) {
        if (!checkCooldown("dpadRight", value)) return
        _controllerState.update { it.copy(dpadRight = value) }
    }

    fun updateLb(value: Boolean) {
        if (!checkCooldown("lb", value)) return
        _controllerState.update { it.copy(lb = value) }
    }

    fun updateRb(value: Boolean) {
        if (!checkCooldown("rb", value)) return
        _controllerState.update { it.copy(rb = value) }
    }

    fun updateBack(value: Boolean) {
        if (!checkCooldown("back", value)) return
        _controllerState.update { it.copy(back = value) }
    }

    fun updateBattery(level: Int) {
        _diagnostics.update { it.copy(battery = level) }
    }

    fun resetCalibration() {
        // Handled by ViewModel calling resetCalibration
    }
}
