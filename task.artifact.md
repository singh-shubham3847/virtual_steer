# Tasks: High-Precision Analog Pedals

- [ ] Create/Update `PedalConfig` and `PedalResponseCurve` enums/data classes in `AnalogPedal.kt`
- [ ] Implement `AnalogPedal` core logic
    - [ ] High-precision touch tracking with `pointerInput`
    - [ ] Dead zone application
    - [ ] Response curve calculation
    - [ ] Smoothing (Low-pass filter) logic
- [ ] Implement `AnalogPedal` UI/Visuals
    - [ ] Professional Metal/Carbon styling
    - [ ] Progress visualization
    - [ ] 3-decimal place debug text
- [ ] Update `HomeScreen.kt` to use the new `AnalogPedal`
- [ ] Verify functionality with a build and manual check (simulated)
