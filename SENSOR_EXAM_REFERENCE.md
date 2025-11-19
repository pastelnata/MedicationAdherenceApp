# Sensor Implementation - Exam Quick Reference

## One-Sentence Summary
The sensor system monitors device sensors (step counter, accelerometer, light) using reactive Flows, following MVVM architecture with lifecycle-aware registration/unregistration for battery efficiency.

## Architecture Flow (Memorize This)
```
UI (SensorScreen) 
  ↓ collects StateFlow
ViewModel (SensorViewModel) 
  ↓ calls repository methods
Repository (SensorRepository) 
  ↓ collects Flow
SensorManagerWrapper 
  ↓ wraps
Android SensorManager
```

## Key Components (4 Layers)

### 1. Data Source Layer
**SensorManagerWrapper** - Wraps Android SensorManager
- Returns: `Flow<SensorData>`
- Uses: `callbackFlow` to bridge callbacks to Flow
- Lifecycle: Registers sensor on collection, unregisters on cancellation

### 2. Repository Layer
**SensorRepository** - Single source of truth
- Exposes: `StateFlow<SensorData?>` for each sensor
- Pattern: Collects from wrapper, updates StateFlow
- Error handling: Exposes `StateFlow<String?>` for errors

### 3. ViewModel Layer
**SensorViewModel** - UI state management
- Exposes: Availability flags, monitoring status, sensor data
- Actions: Toggle monitoring, clear errors, format data
- Scope: Uses `viewModelScope` for automatic cleanup

### 4. UI Layer
**SensorScreen** - Composable UI
- Displays: Real-time sensor readings, controls
- Collects: StateFlows via `collectAsState()`
- Pattern: Unidirectional data flow

## Lifecycle Management (Exam Answer)

**Question: "How do sensors get registered and unregistered?"**

**Answer:**
1. User clicks "Start Monitoring" → ViewModel calls `repository.startMonitoring()`
2. Repository launches coroutine in viewModelScope that collects sensor Flow
3. Flow collection triggers `callbackFlow` which registers SensorEventListener
4. Sensor events flow: Android SensorManager → Listener → Flow → StateFlow → UI
5. When monitoring stops or ViewModel clears:
   - ViewModelScope cancels coroutines
   - Flow collection stops
   - `awaitClose` block executes
   - SensorEventListener unregisters
   - No battery drain!

**Key Point**: Registration happens on Flow collection start, unregistration happens automatically on Flow cancellation via `awaitClose`.

## Code Snippets for Exam

### Creating Reactive Sensor Flow
```kotlin
fun getSensorData(sensorType: SensorType): Flow<SensorData> = callbackFlow {
    val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            event?.let { trySend(SensorData(...)) }
        }
    }
    sensorManager.registerListener(listener, sensor, samplingRate)
    awaitClose { sensorManager.unregisterListener(listener) }
}
```

### Repository Pattern
```kotlin
fun startMonitoring(sensorType: SensorType, scope: CoroutineScope) {
    scope.launch {
        sensorManagerWrapper.getSensorData(sensorType)
            .catch { error -> _sensorError.value = error.message }
            .collect { data -> _stepCounterData.value = data }
    }
}
```

### UI Collection
```kotlin
val stepCounterData by viewModel.stepCounterData.collectAsState()
Text(text = stepCounterData?.values?.first()?.toString() ?: "N/A")
```

## Three Sensors Explained

| Sensor | Type | Values | Use Case |
|--------|------|--------|----------|
| Step Counter | `TYPE_STEP_COUNTER` | 1 float (total steps) | Track daily activity |
| Accelerometer | `TYPE_ACCELEROMETER` | 3 floats (X, Y, Z in m/s²) | Detect movement/falls |
| Light | `TYPE_LIGHT` | 1 float (lux) | Infer awake/asleep |

## Key Patterns Used

1. **callbackFlow** - Bridges callback-based API to Flow
2. **StateFlow** - For continuous state that UI observes
3. **ViewModelScope** - Ensures cleanup when ViewModel destroyed
4. **awaitClose** - Executes cleanup when Flow cancelled
5. **Single Source of Truth** - Repository is the only data source
6. **Unidirectional Data Flow** - Data flows one direction: Source → UI

## Common Exam Questions & Answers

**Q: Why use Flow instead of LiveData?**
A: Flow is more powerful - supports operators, backpressure, and integrates with coroutines. It's also null-safe by default.

**Q: How do you prevent memory leaks?**
A: Use ViewModelScope which auto-cancels, callbackFlow with awaitClose, and StateFlow which doesn't hold strong references.

**Q: What if sensor isn't available?**
A: Check `isSensorAvailable()` first, disable UI elements, show "Not Available" message, handle null sensor gracefully.

**Q: How to reduce battery drain?**
A: Use appropriate sampling rate (SENSOR_DELAY_NORMAL), unregister when not needed, use StateFlow to avoid duplicate processing.

**Q: Thread safety?**
A: All sensor operations happen in coroutines (background threads), StateFlow updates are atomic, SensorManager is thread-safe.

## Design Decisions (Justify in Exam)

1. **Why Singleton Repository?** - Ensures single source of truth, prevents duplicate sensor registrations
2. **Why StateFlow over LiveData?** - Type-safe, coroutine-native, better Flow integration
3. **Why callbackFlow?** - Converts callback-based SensorEventListener to reactive Flow
4. **Why ViewModelScope?** - Automatic cancellation on config changes prevents leaks
5. **Why awaitClose?** - Guarantees sensor unregistration when Flow cancelled

## Reactive Principles Applied

✅ **Backpressure** - Flow handles slow collectors  
✅ **Thread Safety** - Coroutines manage threading  
✅ **Lifecycle Aware** - Auto cleanup on lifecycle events  
✅ **Single Source of Truth** - Repository is canonical source  
✅ **Declarative UI** - UI is pure function of state  

## Integration Points

- **Navigation**: `Destinations.SENSORS` route
- **DI**: Hilt provides all dependencies (`@Inject`, `@Singleton`)
- **Theming**: Uses Material Design 3 colors/typography
- **Architecture**: Follows existing MVVM pattern
- **Data**: Can extend to persist in Room database

## Quick Exam Talking Points

1. "Sensors use callbackFlow to bridge imperative Android API to reactive Flow"
2. "ViewModelScope ensures all coroutines cancel when ViewModel clears"
3. "awaitClose guarantees sensor unregistration preventing battery drain"
4. "Repository exposes StateFlow as single source of truth"
5. "UI collects StateFlow and recomposes automatically when data changes"
6. "Thread-safe because coroutines manage background execution"
7. "Lifecycle-aware because Flow cancellation triggers cleanup"
8. "Follows MVVM: UI → ViewModel → Repository → Data Source"

## Testing Strategy

- **Unit Tests**: Mock wrapper, test repository Flow emissions
- **ViewModel Tests**: Mock repository, verify state changes
- **Integration Tests**: Test on real device, verify sensor registration
- **UI Tests**: Verify buttons, data display, error handling

## Time-to-Explain (30 seconds)

"The sensor system monitors device sensors to track patient activity. SensorManagerWrapper wraps Android's SensorManager and exposes sensor data as reactive Flows. The Repository collects these Flows and exposes StateFlows to the ViewModel. The ViewModel manages UI state and user actions. The UI collects StateFlows and recomposes when data changes. Sensors auto-register when Flows are collected and auto-unregister via awaitClose when cancelled by ViewModelScope, preventing battery drain. It follows MVVM with dependency injection via Hilt."

## Remember These Buzzwords

- ✅ Reactive Streams (Flow/StateFlow)
- ✅ Lifecycle-Aware
- ✅ callbackFlow with awaitClose
- ✅ ViewModelScope
- ✅ Single Source of Truth
- ✅ Unidirectional Data Flow
- ✅ MVVM Architecture
- ✅ Dependency Injection (Hilt)
- ✅ Thread-Safe Coroutines
- ✅ Material Design 3

