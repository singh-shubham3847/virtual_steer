# Virtual Steer Android v1.0 - Technical Documentation

This document describes the data pipeline and communication protocol between the Virtual Steer Android application and the Windows host.

## Data Pipeline Overview

The data pipeline is optimized for ultra-low latency and high precision to provide a commercial-grade racing experience.

### 1. High-Frequency Data Capture
*   **Motion Steering**: Uses the `Rotation Vector` hardware-fused sensor (Gyroscope + Accelerometer + Magnetometer). Operating in `SENSOR_DELAY_GAME` mode, it achieves update rates between 100-200Hz.
*   **Analog Pedals**: Captured via Jetpack Compose `pointerInput`. The vertical drag distance is calculated relative to the touch origin, ensuring consistent precision regardless of screen position.

### 2. Processing Engine
The raw sensor and touch data are processed through several stages:
*   **Calibration**: Applies a user-defined center offset to account for device orientation.
*   **Smoothing**: A Low-Pass filter removes high-frequency jitter.
*   **Response Curves**: Non-linear power functions translate inputs for better control (e.g., S-curves for pedals).
*   **Normalization**: Final values are mapped to standard ranges: Steering `[-1.0, 1.0]`, Pedals `[0.0, 1.0]`.

### 3. State Aggregation
The `ControllerViewModel` manages the active `ControllerState`. It combines processed steering, throttle, brake, and button states into a single immutable data object that is synchronized with the network transmission loop.

### 4. Binary Serialization (Protocol v1)
To minimize bandwidth and latency, data is serialized into a compact **24-byte binary packet** instead of text-based formats like JSON.

**Packet Structure:**

| Offset | Data Type | Field | Description |
| :--- | :--- | :--- | :--- |
| 0 | Byte | Header | `0x56` (Identifies Virtual Steer packets) |
| 1 | Byte | Version | `0x01` (Protocol Version) |
| 2 | Short | Sequence | Monotonically increasing packet ID |
| 4 | Float | Steering | Left/Right Normalized value `[-1, 1]` |
| 8 | Float | Throttle | Gas Pedal Normalized value `[0, 1]` |
| 12 | Float | Brake | Brake Pedal Normalized value `[0, 1]` |
| 16 | Float | Clutch | Clutch Pedal Normalized value `[0, 1]` |
| 20 | Byte | Buttons | Bitfield for 7 buttons (Handbrake, Gears, etc.) |
| 21 | Byte | Reserved | Future use / Padding |
| 22 | Short | CRC | 16-bit Checksum for packet integrity |

### 5. UDP Transmission
The `UDPClient` handles the physical transmission:
*   **Protocol**: UDP (User Datagram Protocol) for zero-latency transmission without retransmission overhead.
*   **Heartbeats**: Periodic packets sent every 1000ms even when no input is detected to maintain the connection.
*   **Transmission Rate**: Configurable up to **200Hz** (default 100Hz / 10ms interval).

---

## Developer Diagnostics
The application includes a built-in **Diagnostics Screen** that provides:
*   Real-time input graphs.
*   Raw Hex dump of outgoing packets.
*   Sensor frequency monitoring.
*   System event logs for connection and calibration events.
