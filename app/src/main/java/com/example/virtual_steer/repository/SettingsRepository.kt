package com.example.virtual_steer.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.virtual_steer.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val STEERING_SENSITIVITY = floatPreferencesKey("steering_sensitivity")
        val STEERING_DEADZONE = floatPreferencesKey("steering_deadzone")
        val STEERING_MAX_ANGLE = intPreferencesKey("steering_max_angle")
        val STEERING_SMOOTHING = floatPreferencesKey("steering_smoothing")
        val STEERING_INVERT = booleanPreferencesKey("steering_invert")
        val STEERING_AUTO_CALIB = booleanPreferencesKey("steering_auto_calib")

        val THROTTLE_CURVE = stringPreferencesKey("throttle_curve")
        val BRAKE_CURVE = stringPreferencesKey("brake_curve")
        val PEDAL_SMOOTHING = floatPreferencesKey("pedal_smoothing")
        val PEDAL_DEADZONE = floatPreferencesKey("pedal_deadzone")

        val AUTO_DISCOVER = booleanPreferencesKey("auto_discover")
        val MANUAL_IP = stringPreferencesKey("manual_ip")
        val UDP_PORT = intPreferencesKey("udp_port")
        val PACKET_RATE = intPreferencesKey("packet_rate")
        val TIMEOUT = intPreferencesKey("timeout")

        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val HAPTIC = booleanPreferencesKey("haptic")
        val TELEMETRY = booleanPreferencesKey("telemetry")
        val FPS = booleanPreferencesKey("fps")
        val BATTERY = booleanPreferencesKey("battery")
        val ACCENT_COLOR = intPreferencesKey("accent_color")
    }

    val configFlow: Flow<ControllerConfig> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            ControllerConfig(
                steering = SteeringConfig(
                    sensitivity = prefs[Keys.STEERING_SENSITIVITY] ?: 1.0f,
                    deadZone = prefs[Keys.STEERING_DEADZONE] ?: 0.02f,
                    maxAngle = prefs[Keys.STEERING_MAX_ANGLE] ?: 270,
                    smoothing = prefs[Keys.STEERING_SMOOTHING] ?: 0.18f,
                    invertSteering = prefs[Keys.STEERING_INVERT] ?: false,
                    autoCalibration = prefs[Keys.STEERING_AUTO_CALIB] ?: true
                ),
                pedals = PedalConfig(
                    throttleCurve = safeValueOf(prefs[Keys.THROTTLE_CURVE], PedalResponseCurve.RACING),
                    brakeCurve = safeValueOf(prefs[Keys.BRAKE_CURVE], PedalResponseCurve.RACING),
                    pedalSmoothing = prefs[Keys.PEDAL_SMOOTHING] ?: 0.20f,
                    pedalDeadZone = prefs[Keys.PEDAL_DEADZONE] ?: 0.05f
                ),
                network = NetworkConfig(
                    autoDiscover = prefs[Keys.AUTO_DISCOVER] ?: true,
                    manualIp = prefs[Keys.MANUAL_IP] ?: "192.168.1.100",
                    udpPort = prefs[Keys.UDP_PORT] ?: 4444,
                    packetRate = prefs[Keys.PACKET_RATE] ?: 100,
                    connectionTimeout = prefs[Keys.TIMEOUT] ?: 5000
                ),
                ui = UIConfig(
                    darkTheme = prefs[Keys.DARK_THEME] ?: true,
                    hapticFeedback = prefs[Keys.HAPTIC] ?: true,
                    showTelemetry = prefs[Keys.TELEMETRY] ?: true,
                    showFps = prefs[Keys.FPS] ?: false,
                    batteryIndicator = prefs[Keys.BATTERY] ?: true,
                    accentColor = prefs[Keys.ACCENT_COLOR] ?: 0xFF00E676.toInt()
                )
            )
        }

    suspend fun updateSteering(transform: (SteeringConfig) -> SteeringConfig) {
        context.dataStore.edit { prefs ->
            val current = SteeringConfig(
                sensitivity = prefs[Keys.STEERING_SENSITIVITY] ?: 1.0f,
                deadZone = prefs[Keys.STEERING_DEADZONE] ?: 0.02f,
                maxAngle = prefs[Keys.STEERING_MAX_ANGLE] ?: 270,
                smoothing = prefs[Keys.STEERING_SMOOTHING] ?: 0.18f,
                invertSteering = prefs[Keys.STEERING_INVERT] ?: false,
                autoCalibration = prefs[Keys.STEERING_AUTO_CALIB] ?: true
            )
            val next = transform(current)
            prefs[Keys.STEERING_SENSITIVITY] = next.sensitivity
            prefs[Keys.STEERING_DEADZONE] = next.deadZone
            prefs[Keys.STEERING_MAX_ANGLE] = next.maxAngle
            prefs[Keys.STEERING_SMOOTHING] = next.smoothing
            prefs[Keys.STEERING_INVERT] = next.invertSteering
            prefs[Keys.STEERING_AUTO_CALIB] = next.autoCalibration
        }
    }

    suspend fun updatePedals(transform: (PedalConfig) -> PedalConfig) {
        context.dataStore.edit { prefs ->
            val current = PedalConfig(
                throttleCurve = safeValueOf(prefs[Keys.THROTTLE_CURVE], PedalResponseCurve.RACING),
                brakeCurve = safeValueOf(prefs[Keys.BRAKE_CURVE], PedalResponseCurve.RACING),
                pedalSmoothing = prefs[Keys.PEDAL_SMOOTHING] ?: 0.20f,
                pedalDeadZone = prefs[Keys.PEDAL_DEADZONE] ?: 0.05f
            )
            val next = transform(current)
            prefs[Keys.THROTTLE_CURVE] = next.throttleCurve.name
            prefs[Keys.BRAKE_CURVE] = next.brakeCurve.name
            prefs[Keys.PEDAL_SMOOTHING] = next.pedalSmoothing
            prefs[Keys.PEDAL_DEADZONE] = next.pedalDeadZone
        }
    }

    suspend fun updateNetwork(transform: (NetworkConfig) -> NetworkConfig) {
        context.dataStore.edit { prefs ->
            val current = NetworkConfig(
                autoDiscover = prefs[Keys.AUTO_DISCOVER] ?: true,
                manualIp = prefs[Keys.MANUAL_IP] ?: "192.168.1.100",
                udpPort = prefs[Keys.UDP_PORT] ?: 4444,
                packetRate = prefs[Keys.PACKET_RATE] ?: 100,
                connectionTimeout = prefs[Keys.TIMEOUT] ?: 5000
            )
            val next = transform(current)
            prefs[Keys.AUTO_DISCOVER] = next.autoDiscover
            prefs[Keys.MANUAL_IP] = next.manualIp
            prefs[Keys.UDP_PORT] = next.udpPort
            prefs[Keys.PACKET_RATE] = next.packetRate
            prefs[Keys.TIMEOUT] = next.connectionTimeout
        }
    }

    suspend fun updateUI(transform: (UIConfig) -> UIConfig) {
        context.dataStore.edit { prefs ->
            val current = UIConfig(
                darkTheme = prefs[Keys.DARK_THEME] ?: true,
                hapticFeedback = prefs[Keys.HAPTIC] ?: true,
                showTelemetry = prefs[Keys.TELEMETRY] ?: true,
                showFps = prefs[Keys.FPS] ?: false,
                batteryIndicator = prefs[Keys.BATTERY] ?: true,
                accentColor = prefs[Keys.ACCENT_COLOR] ?: 0xFF00E676.toInt()
            )
            val next = transform(current)
            prefs[Keys.DARK_THEME] = next.darkTheme
            prefs[Keys.HAPTIC] = next.hapticFeedback
            prefs[Keys.TELEMETRY] = next.showTelemetry
            prefs[Keys.FPS] = next.showFps
            prefs[Keys.BATTERY] = next.batteryIndicator
            prefs[Keys.ACCENT_COLOR] = next.accentColor
        }
    }

    private fun safeValueOf(name: String?, default: PedalResponseCurve): PedalResponseCurve {
        return try {
            if (name != null) PedalResponseCurve.valueOf(name) else default
        } catch (e: IllegalArgumentException) {
            default
        }
    }
}
