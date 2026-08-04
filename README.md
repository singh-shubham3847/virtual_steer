# 📱 Virtual Steer Android Companion App

Virtual Steer is an ultra-low latency, high-precision virtual steering wheel controller that transforms your Android smartphone into a wireless racing wheel. By utilizing your device's built-in motion sensors, it emulates a physical steering axis and transmits control inputs to a Windows host PC over UDP, feeding directly into a virtual Xbox 360 controller via the ViGEmBus driver.

---

## ⚡ Core Features

* **High-Frequency Motion Steering:** Uses hardware-fused `Rotation Vector` sensors running at **100Hz–200Hz** for ultra-responsive control.
* **Analog Pedals:** On-screen sliders for precision Throttle, Brake, and Clutch inputs.
* **7 Programmable Action Buttons:** Integrated mapping for Handbrake, Sequential Gear Shift Up/Down, Pause, Horn, Camera, and Headlights.
* **Zero-Configuration Auto-Discovery:** Dynamic subnet broadcasting finds the Windows companion server instantly on local Wi-Fi or when connected via **Android Hotspot**.
* **Smart Angle Calibration:** Seamlessly handles 360° sensor wrap-around (`[-180°, 180°]`) to prevent sudden controller snapping, regardless of how you hold your device.
* **Advanced Diagnostics Panel:** Real-time sensor charts, outgoing binary packet inspector, UDP performance telemetry, and event logger.

---

## 🛠️ Build & Installation Guide

You can compile a standalone `.apk` directly from this repository using **Android Studio** or the **Gradle command line**.

### Prerequisites
* **Java Development Kit (JDK 17)** or higher.
* **Android SDK** (API Level 34+ recommended).

### 1. Build via Command Line (CLI)
Open your terminal (PowerShell or Command Prompt) in this project directory:

* **Compile Debug APK (Fastest, for testing):**
  ```cmd
  .\gradlew.bat assembleDebug
  ```
  *Your compiled package will be saved to:*  
  📂 `app/build/outputs/apk/debug/app-debug.apk`

* **Compile Release APK (Optimized):**
  ```cmd
  .\gradlew.bat assembleRelease
  ```
  *Your compiled package will be saved to:*  
  📂 `app/build/outputs/apk/release/app-release-unsigned.apk`

* **Clean Build Cache:**
  ```cmd
  .\gradlew.bat clean
  ```

### 2. Build via Android Studio
1. Open Android Studio and select **File > Open**.
2. Select the directory: `C:\Users\Shubham\AndroidStudioProjects\virtual_steer`.
3. Wait for the Gradle sync to finish.
4. Click **Build > Build Bundle(s) / APK(s) > Build APK(s)** in the top menu.
5. A popup will appear in the bottom-right corner when complete. Click **Locate** to find the output APK.

---

## 🏎️ Connection & Setup Checklist

For the lowest possible latency and minimum interference, **Mobile Hotspot Mode** is highly recommended.

1. **Enable Mobile Hotspot** on your Android phone.
2. **Connect your Windows PC** to this mobile hotspot network.
3. Open the **Virtual Steer Windows Receiver** on your PC and click **Start Listening**.
4. Launch the **Virtual Steer Android App**. It should automatically detect and pair with your PC.
   * *If auto-discovery is blocked by a firewall, go to **Settings > Network** in the app and manually enter your PC's IP address.*
5. **Calibrate Steering:** Hold the phone level in your preferred driving angle and tap **CALIBRATE (Set Center)**.
6. Verify your inputs on PC using Windows Game Controllers setting (**`joy.cpl`**).

---

## 📊 Communication Protocol (v1)

Data is serialized into a highly compacted **24-byte binary packet** for maximum network throughput:

| Offset | Data Type | Field | Description |
| :--- | :--- | :--- | :--- |
| **0** | Byte | Header | `0x56` (representing ASCII 'V') |
| **1** | Byte | Version | `0x01` |
| **2** | Short | Sequence | Packet sequence ID (for loss detection) |
| **4** | Float | Steering | Left/Right axis `[-1.0, 1.0]` |
| **8** | Float | Throttle | Gas pedal `[0.0, 1.0]` |
| **12** | Float | Brake | Brake pedal `[0.0, 1.0]` |
| **16** | Float | Clutch | Clutch pedal `[0.0, 1.0]` |
| **20** | Byte | Buttons | Bitfield mapped to Xbox digital buttons |
| **21** | Byte | Reserved | Alignment padding (always `0`) |
| **22** | Short | CRC | 16-bit Checksum (calculated over offsets 0-21) |

### Action Button Bitfield Maps
* **Bit 0:** Handbrake
* **Bit 1:** Gear Up
* **Bit 2:** Gear Down
* **Bit 3:** Pause
* **Bit 4:** Horn
* **Bit 5:** Camera
* **Bit 6:** Headlights
