# Implementation Plan: High-Precision Analog Pedals

Replace the existing `RadialPedal` and `DynamicTouchPedal` with a professional-grade, high-precision `AnalogPedal` component.

## User Review Required

> [!IMPORTANT]
> The new implementation will replace the current floating "RadialPedal" with a fixed-position vertical analog pedal. The entire height of the pedal zone will be touch-sensitive.

## Proposed Changes

### [Component] UI Components

#### [MODIFY] [AnalogPedal.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/ui/components/AnalogPedal.kt)
- Add `PedalConfig` data class to hold smoothing, response curve, and dead zone settings.
- Add `PedalResponseCurve` enum (LINEAR, RACING, AGGRESSIVE).
- Implement `AnalogPedal` composable:
    - Fixed vertical layout.
    - `pointerInput` with `awaitPointerEventScope` for high-performance touch tracking.
    - Application of top and bottom dead zones.
    - Application of response curves (power functions).
    - Internal smoothing using a low-pass filter (LpFilter) driven by `withFrameNanos` if enabled.
    - Professional "Metal/Carbon" visual styling.
    - Debug text display (3 decimal places).
- Remove or deprecate `RadialPedal` if no longer used.

#### [MODIFY] [HomeScreen.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/ui/screens/HomeScreen.kt)
- Replace `DynamicTouchPedal` and `RadialPedal` integration with the new `AnalogPedal`.
- Define fixed positions for Brake (left) and Throttle (right).

### [Logic] ViewModel & State

#### [MODIFY] [SteeringViewModel.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/viewmodel/SteeringViewModel.kt)
- Ensure it properly receives and stores the high-precision Float values.

## Verification Plan

### Automated Tests
- I will verify the math for response curves and dead zones in the code.
- Since it's a UI-heavy change, manual verification on device is key.

### Manual Verification
- Deploy to the device and test the pedals.
- Verify that the values are continuous and reach 1.0 at the top and 0.0 at the bottom.
- Test different response curves (Linear vs Racing vs Aggressive).
- Verify that smoothing removes "stepping" when enabled.
- Check the 3-decimal place debug display.
