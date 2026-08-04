# Implementation Plan: Racing Menu Settings Redesign

Completely overhaul the settings system and UI to match a high-performance racing game aesthetic (Gran Turismo / GRID style). This includes refactoring the data model, migrating to DataStore, implementing navigation, and creating a custom "Tuning Menu" UI.

## User Review Required

> [!IMPORTANT]
> This change involves refactoring the existing `ControllerConfig` model. Any code currently relying on the old flat structure of `ControllerConfig` will need to be updated to the new nested structure.

> [!NOTE]
> We are migrating from standard SharedPreferences (or no storage) to **Jetpack DataStore**. This ensures type-safety and asynchronous updates, which is critical for real-time engine tuning.

## Proposed Changes

### Infrastructure & Dependencies

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/gradle/libs.versions.toml)
- Add `datastore` and `navigation-compose` library definitions.

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/build.gradle.kts)
- Include the new dependencies.

---

### Data Models & Repository

#### [NEW] [SteeringConfig.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/model/SteeringConfig.kt)
#### [NEW] [PedalConfig.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/model/PedalConfig.kt)
#### [NEW] [NetworkConfig.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/model/NetworkConfig.kt)
#### [NEW] [UIConfig.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/model/UIConfig.kt)
#### [MODIFY] [ControllerConfig.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/model/ControllerConfig.kt)
- Redefine as a composition of the 4 specialized config classes.

#### [NEW] [SettingsRepository.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/repository/SettingsRepository.kt)
- Create a DataStore-backed repository to manage these configs.

---

### Navigation

#### [NEW] [Screen.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/navigation/Screen.kt)
- Route definitions: `Home`, `Settings`, `Calibration`, `Diagnostics`.

#### [NEW] [NavGraph.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/navigation/NavGraph.kt)
- Implement `NavHost` with transitions.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/MainActivity.kt)
- Integrate the `NavGraph`.

---

### UI & UX (The "Racing" Look)

#### [NEW] [RacingComponents.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/ui/components/RacingComponents.kt)
- Custom composables: `RacingSlider`, `RacingSwitch`, `RacingHeader`, `TuningCard`.

#### [NEW] [SettingsScreen.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/ui/screens/SettingsScreen.kt)
- The main settings UI implementation following the "Tuning Menu" mockup.

#### [NEW] [SettingsViewModel.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/viewmodel/SettingsViewModel.kt)
- ViewModel to bridge the UI and the `SettingsRepository`.

---

### Engine Integration

#### [MODIFY] [SteeringViewModel.kt](file:///C:/Users/Shubham/AndroidStudioProjects/virtual_steer/app/src/main/java/com/example/virtual_steer/viewmodel/SteeringViewModel.kt)
- Observe the settings Flow and apply updates to the steering logic.

## Verification Plan

### Automated Tests
- `SettingsRepositoryTest`: Verify DataStore read/write.
- `SteeringProcessorTest`: Ensure config changes correctly affect processing logic.

### Manual Verification
- Navigate from Home to Settings and back.
- Adjust sliders (e.g., Sensitivity) and verify real-time impact on the steering angle display in `HomeScreen`.
- Kill and restart the app to verify settings persistence.
- Verify UI aesthetics on a physical device or emulator.
