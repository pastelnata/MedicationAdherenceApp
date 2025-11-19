# Sensor Implementation Guide

## Overview
This guide explains the sensor monitoring implementation in the Medication Adherence App. The sensor system allows the app to track patient activity patterns through device sensors, which can provide insights for medication adherence and health monitoring.

## Architecture

### Components

#### 1. SensorType (Enum)
**Location**: `sensors/SensorType.kt`

Defines the types of sensors used in the app:
- `STEP_COUNTER`: Tracks daily step count
- `ACCELEROMETER`: Monitors device movement (X, Y, Z axes)
- `LIGHT`: Measures ambient light levels

#### 2. SensorData (Data Class)
**Location**: `sensors/SensorData.kt`

Represents a sensor reading with:
- `sensorType`: The type of sensor
- `values`: Array of sensor values (size depends on sensor type)
- `timestamp`: When the reading was taken
- `accuracy`: Sensor accuracy level

#### 3. SensorManagerWrapper (Singleton)
**Location**: `sensors/SensorManagerWrapper.kt`

**Responsibility**: Wraps Android's SensorManager and provides a reactive API using Kotlin Flow.

**Key Methods**:
- `isSensorAvailable(sensorType)`: Checks if a sensor is available on the device
- `getSensorData(sensorType, samplingPeriod)`: Returns a Flow of sensor readings
- `getAvailableSensors()`: Lists all available sensors on the device

**Design Features**:
- Uses `callbackFlow` to bridge callback-based `SensorEventListener` to coroutine Flow
- Automatically registers sensor listener when Flow is collected
- Automatically unregisters sensor listener via `awaitClose` when Flow is cancelled
- Thread-safe sensor operations

**Example**:
```kotlin
sensorManagerWrapper.getSensorData(SensorType.STEP_COUNTER)
    .collect { sensorData ->
        val stepCount = sensorData.values[0]
        // Use step count...
    }
```

#### 4. SensorRepository (Singleton)
**Location**: `sensors/SensorRepository.kt`

**Responsibility**: Provides a clean, single-source-of-truth API for ViewModels to access sensor data.

**Exposed StateFlows**:
- `stepCounterData`: Current step counter reading
- `accelerometerData`: Current accelerometer reading
- `lightSensorData`: Current light sensor reading
- `sensorError`: Error messages if sensors fail

**Key Methods**:
- `isSensorAvailable(sensorType)`: Check sensor availability
- `startMonitoring(sensorType, scope)`: Start monitoring a sensor
- `getCurrentStepCount()`: Get latest step count value
- `getCurrentAccelerometerValues()`: Get latest X, Y, Z values
- `getCurrentLightLevel()`: Get latest light level in lux
- `clearError()`: Clear error state

**Pattern**: Repository collects from SensorManagerWrapper's Flow and updates StateFlows that ViewModels observe.

#### 5. SensorViewModel (Hilt ViewModel)
**Location**: `ui/viewmodel/SensorViewModel.kt`

**Responsibility**: Manages UI state for the sensor screen.

**Exposed State**:
- Sensor availability flags (Boolean StateFlows)
- Monitoring status flags (Boolean StateFlows)
- Sensor data (SensorData? StateFlows)
- Error messages (String? StateFlow)

**User Actions**:
- `toggleStepCounterMonitoring()`: Start/stop step counter
- `toggleAccelerometerMonitoring()`: Start/stop accelerometer
- `toggleLightSensorMonitoring()`: Start/stop light sensor
- `clearError()`: Dismiss error messages

**Formatting Methods**:
- `getFormattedStepCount()`: Returns "1234" or "N/A"
- `getFormattedAccelerometerValues()`: Returns "X: 0.12, Y: 0.34, Z: 9.81" or "N/A"
- `getFormattedLightLevel()`: Returns "123.45 lux" or "N/A"

**Lifecycle**: ViewModelScope ensures all coroutines are cancelled when ViewModel is cleared, which triggers Flow cancellation and sensor unregistration.

#### 6. SensorScreen (Composable)
**Location**: `ui/components/sensors/SensorScreen.kt`

**Responsibility**: UI that displays sensor information and controls.

**Features**:
- Shows which sensors are available on the device
- Displays real-time sensor readings
- Provides start/stop buttons for each sensor
- Shows error messages when sensors fail
- Responsive Material Design 3 UI

**Components**:
- Header with title and description
- Three `SensorCard` components (one per sensor type)
- Info card explaining sensor usage
- Scrollable layout for smaller screens

## Data Flow

### Starting Sensor Monitoring

1. **User taps "Start Monitoring" button** in `SensorScreen`
2. **SensorScreen** calls `viewModel.toggleStepCounterMonitoring()`
3. **SensorViewModel** updates `_isStepCounterMonitoring` to `true`
4. **SensorViewModel** calls `repository.startMonitoring(SensorType.STEP_COUNTER, viewModelScope)`
5. **SensorRepository** launches coroutine that collects from `sensorManagerWrapper.getSensorData()`
6. **SensorManagerWrapper** creates a Flow using `callbackFlow`:
   - Registers `SensorEventListener` with Android's SensorManager
   - Emits `SensorData` whenever `onSensorChanged()` is called
7. **SensorRepository** receives `SensorData` and updates `_stepCounterData` StateFlow
8. **SensorViewModel** exposes repository's StateFlow to UI
9. **SensorScreen** collects the StateFlow and recomposes with new data

### Stopping Sensor Monitoring

1. **User taps "Stop Monitoring" button**
2. **SensorViewModel** updates `_isStepCounterMonitoring` to `false`
3. **ViewModelScope coroutine** (collecting from repository) continues but UI shows "stopped" state
4. When **ViewModel is cleared** (navigation away, etc.):
   - ViewModelScope cancels all coroutines
   - Flow collection stops
   - `awaitClose` block in `callbackFlow` executes
   - SensorEventListener is unregistered from SensorManager

## Sensor Details

### Step Counter
- **Sensor Type**: `TYPE_STEP_COUNTER`
- **Values**: Single float value representing total steps since last reboot
- **Use Case**: Track daily activity levels
- **Note**: Value resets on device reboot

### Accelerometer
- **Sensor Type**: `TYPE_ACCELEROMETER`
- **Values**: Three float values [X, Y, Z] in m/s²
- **Use Case**: Detect movement patterns, potential falls
- **Axes**:
  - X: Left/right movement
  - Y: Forward/backward movement
  - Z: Up/down movement

### Light Sensor
- **Sensor Type**: `TYPE_LIGHT`
- **Values**: Single float value in lux (lumens per square meter)
- **Use Case**: Infer user activity patterns (awake/asleep, indoor/outdoor)
- **Typical Values**:
  - 0-10 lux: Dark/night
  - 10-100 lux: Indoor lighting
  - 100-1000 lux: Overcast day
  - 1000+ lux: Direct sunlight

## Navigation

The sensor screen is accessible via the `Destinations.SENSORS` route:

```kotlin
navController.navigate(Destinations.SENSORS)
```

In the navigation graph:
```kotlin
composable(Destinations.SENSORS) {
    ScaffoldWithTopBar {
        SensorScreen()
    }
}
```

## Best Practices Implemented

### 1. Lifecycle-Aware Sensor Management
- Sensors are only active when Flow is being collected
- Automatic cleanup via `awaitClose` prevents sensor battery drain
- ViewModelScope ensures cleanup on ViewModel destruction

### 2. Reactive Architecture
- Uses Flow/StateFlow for reactive data streams
- UI automatically updates when sensor data changes
- Follows unidirectional data flow pattern

### 3. Error Handling
- Checks sensor availability before attempting to use
- Gracefully handles missing sensors
- Provides user-friendly error messages
- Uses nullable types for optional data

### 4. Thread Safety
- SensorManager operations are thread-safe via coroutines
- StateFlow updates are atomic
- No race conditions in sensor registration/unregistration

### 5. Performance
- Non-blocking sensor reads via Flow
- Efficient recomposition in Compose UI
- Sensor data throttled by Android's sampling rate

### 6. Material Design 3
- Consistent theming
- Clear visual hierarchy
- Accessible touch targets
- Responsive layouts

## Testing Considerations

### Unit Testing
- Mock `SensorManagerWrapper` in `SensorRepository` tests
- Mock `SensorRepository` in `SensorViewModel` tests
- Test Flow emissions and StateFlow updates

### Integration Testing
- Test sensor availability checks on different devices
- Verify Flow cancellation triggers sensor unregistration
- Test error handling for unavailable sensors

### UI Testing
- Verify buttons enable/disable based on sensor availability
- Test monitoring toggle behavior
- Verify sensor data display updates

## Future Enhancements

1. **Data Persistence**: Store sensor readings in Room database for historical analysis
2. **Analytics**: Compute daily/weekly averages and trends
3. **Alerts**: Notify caregivers of unusual activity patterns (e.g., low step count, detected falls)
4. **Additional Sensors**: Heart rate, proximity, GPS for location-based reminders
5. **Background Monitoring**: Use WorkManager for periodic sensor checks
6. **Data Export**: Allow users to export sensor data for healthcare providers

## Permissions

Currently, no special permissions are required for the sensors used (step counter, accelerometer, light). However, if you add sensors like heart rate or location, you'll need to:

1. Add permissions to `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.BODY_SENSORS" />
<uses-permission android:name="android.permission.ACTIVITY_RECOGNITION" />
```

2. Request runtime permissions in the app
3. Handle permission denied cases
4. Provide rationale dialogs

## Troubleshooting

### Sensor Not Available
- Not all devices have all sensors
- Check `isSensorAvailable()` before use
- Provide fallback UI for missing sensors

### Sensor Data Not Updating
- Verify monitoring is started
- Check that Flow is being collected
- Ensure ViewModel is not cleared prematurely

### High Battery Drain
- Use appropriate sampling rates (SENSOR_DELAY_NORMAL is recommended)
- Stop monitoring when not needed
- Consider WorkManager for periodic checks instead of continuous monitoring

## Related Files
- `sensors/SensorType.kt` - Sensor type enumeration
- `sensors/SensorData.kt` - Sensor data model
- `sensors/SensorManagerWrapper.kt` - Android sensor API wrapper
- `sensors/SensorRepository.kt` - Data layer for sensors
- `ui/viewmodel/SensorViewModel.kt` - UI state management
- `ui/components/sensors/SensorScreen.kt` - UI composable
- `ui/navigation/NavGraph.kt` - Navigation setup

## Exam Key Points

### What the sensor system does:
- Monitors device sensors (step counter, accelerometer, light) to track patient activity
- Provides real-time sensor readings in a Material Design UI
- Follows MVVM architecture with reactive data flow

### How it works:
1. SensorManagerWrapper wraps Android's SensorManager with reactive Flows
2. SensorRepository collects sensor Flows and exposes StateFlows
3. SensorViewModel manages UI state and user actions
4. SensorScreen displays data and provides controls
5. Sensors auto-register on Flow collection and auto-unregister on cancellation

### Key architectural patterns:
- **Reactive Streams**: Flow/StateFlow for sensor data
- **Lifecycle Awareness**: ViewModelScope + awaitClose for cleanup
- **Single Source of Truth**: Repository layer
- **Separation of Concerns**: UI, ViewModel, Repository, Data Source layers
- **Dependency Injection**: Hilt provides all dependencies

### How to explain sensor lifecycle in an exam:
"When the user starts monitoring, the ViewModel tells the Repository to start. The Repository collects from a Flow provided by SensorManagerWrapper. That Flow uses callbackFlow to register a SensorEventListener with Android's SensorManager. As sensor events arrive, they're emitted through the Flow, collected by the Repository, and exposed via StateFlow. The UI collects the StateFlow and recomposes with new data. When monitoring stops or the ViewModel is cleared, the Flow collection is cancelled, triggering awaitClose which unregisters the sensor listener, preventing battery drain."

