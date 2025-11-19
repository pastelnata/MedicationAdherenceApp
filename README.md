# An app to track medication schedules and reminders

This README serves two purposes:
- A concise, exam-focused explanation of *what the app is for* and *how it works* (quick to memorize and explain), and
- The original structured project documentation that explains architecture, packages, and runtime workflow in more detail for contributors.

---

## Exam-focused summary

# Quick summary
- Purpose: let caregivers and patients create medication schedules, record taken doses, receive reminders, and view adherence data and health tips.
- High-level idea: an offline-first Android app using Room for structured data, DataStore for small preferences, Hilt for DI, and Jetpack Compose + ViewModel for UI.

# Core responsibilities (one-liners you can use in an exam)
- UI: Render state and emit user intents (Compose composables). Keep UI logic declarative and side-effect free.
- ViewModel: Receive UI intents, validate/transform them, call repositories, and expose read-only state streams to the UI (StateFlow / SharedFlow).
- Repository: Single source of truth. Provide a stable API for data: return reactive streams for reads and suspend functions for writes. Hide persistence and (future) network details behind a simple interface.
- Persistence: Room stores domain objects (users, medications, schedules, intake records, messages, tips, contacts). DataStore stores small preferences (selected user, dark mode, notification toggle).
- Background/Notifications: WorkManager scheduled workers check upcoming schedules and issue notifications that deep-link into the UI.

# How the system works — end-to-end flows (step-by-step, exam style)
1) How a medication schedule is created (write path)
    - User fills a schedule form and taps "Save" in the UI.
    - UI calls a ViewModel method (intent) with the schedule data.
    - ViewModel validates input and calls the repository's suspend `scheduleMedication(...)` method.
    - Repository calls the DAO suspend insert method which writes to Room on a background thread.
    - Because the UI observes schedule lists via a `Flow` from Room, the database write causes the Flow to emit a new list of schedules.
    - ViewModel collects that Flow and updates its `StateFlow` so Compose recomposition shows the new schedule automatically.
      Key exam points to mention for this flow:
    - The write is synchronous from the caller's perspective (suspend function) but executed on a background thread managed by Room/coroutines.
    - The read is reactive: Room's `Flow` provides push updates; no polling required.
    - This pattern (suspend writes + Flow reads) implements an offline-first, eventual-consistency model: local writes are immediately persisted and visible; remote sync (if added) can reconcile later.

2) How an intake is recorded (linking entities)
    - Recording an intake creates a `MedicationIntakeRecord` that references a `MedicationSchedule` by id.
    - The DAO for intake records is annotated so deletes on schedules cascade to intake records (foreign key constraints), ensuring referential integrity.
    - The UI typically shows intake entries by collecting a Flow scoped to a schedule id; after insert, that Flow updates automatically.

3) How preferences (DataStore) are used and observed
    - `DataStoreManager` exposes a `userPreferencesFlow: Flow<UserPreferences>`.
    - ViewModels can collect that Flow and react to preference changes (e.g., enabling dark mode or changing the active user for the session).
    - Writes to DataStore are suspend functions that atomically update keys; DataStore is safe for concurrent use and is optimized for small key-value storage.

4) Background reminders and notification handling
    - WorkManager jobs (scheduled by the app when appropriate) wake at configured times and query Room for schedules due in a window.
    - Worker code reads the DB (via repository or DAO), constructs notifications, and issues them via NotificationManager with a PendingIntent deep-linking to the relevant screen.
    - Workers should use dependency injection (HiltWorkerFactory) to obtain DAOs/repositories and follow best practices for short, retryable work.

# Reactive principles and correctness you can explain in an exam
- Single source of truth: repository + Room ensures there is one canonical copy of domain data in the app; UI is a projection of that source via reactive streams.
- Backpressure and thread safety: Room + Flow + coroutines ensure DB access happens off the main thread and stream emission is safe to collect from UI code.
- Referential integrity: Room foreign keys and cascading deletes keep related rows consistent without manual cleanup.
- Deterministic UI updates: ViewModels expose StateFlow/SharedFlow so composables get stable snapshot states and one-shot events are handled without duplication.

# How to answer "how would you add network sync?"
- Add a `RemoteDataSource` that wraps an `ApiService` (Retrofit) returning DTOs.
- Add a sync strategy to repositories:
    - Option A (NetworkBoundResource): Expose local `Flow` immediately, then fetch remote, write remote results to Room, letting the Flow deliver updates to the UI; handle error/backoff.
    - Option B (Manual refresh + push): Provide explicit `refreshX()` methods that fetch and write to DB; allow WorkManager to schedule periodic refreshes.
- Important concerns: conflict resolution, idempotency, authentication, and offline write queuing.

# Short answers to likely exam questions
- Where is the data stored? Room (structured domain data) + DataStore (small preferences).
- How does the UI get updated when data changes? The UI collects `Flow`/`StateFlow` exposed by ViewModels; Room emits Flow updates when the DB changes.
- How are long-running or background tasks scheduled? Use WorkManager; workers read Room and post notifications to NotificationManager.
- How do you test the data layer? Use an in-memory Room database for DAO tests and mock the `ApiService` for repository tests; assert Flows emit expected sequences.

---

## App Structure

### Table of contents
- Project overview
- High-level architecture
- App modules and important packages
- Navigation and screen routes
- ViewModels and UI state
- Data layer (Room / Repository / DataStore)
- Networking (Retrofit / API)
- Background work & notifications
- Accessibility
- Typical workflow

---

### Project overview
- Purpose: Track medication schedules, provide reminders, and surface adherence progress and tips.
- Architectural pattern: MVVM with unidirectional data flow between UI and ViewModel.

---

### High-level architecture
- UI (Jetpack Compose): Composable functions represent screens and components.
- ViewModel layer: Holds UI state (`StateFlow`) and exposes read-only flows for composables to collect. Handles user events, validation, and coordinates repositories.
- Repository layer: Single source of truth for data. Abstracts Room + (optionally) network interactions and exposes Flows for the ViewModel.
- Persistence: Room for structured data (medication schedules, logs); DataStore for small key-value preferences.
- Networking: Retrofit is included in the project dependencies, but network endpoints are currently a stub (see `data/remote/ApiService.kt`).
- Background: WorkManager is referenced in the design; some workers and notification wiring may be partially stubbed or to-be-implemented depending on feature.

---

### Navigation and screen routes
- Centralized routes: `Destinations` object defines route strings (e.g., `login`, `dashboard`, `details/{itemId}`) to reduce typos.
- `NavHost` and `NavController`: The app uses `rememberNavController()` with a single `NavHost` where each route is registered using `composable`.
- Argument passing:
    - Path arguments are declared as placeholders (e.g., `details/{itemId}`) and typed using `navArgument("itemId") { type = NavType.IntType }`.
    - Deep links can map external URLs to internal routes (e.g., `https://www.example.com/details/{itemId}`).
- Navigation actions:
    - Use `navController.navigate("route")` to navigate.
    - Use `popUpTo(route) { inclusive = true }` to remove screens from the back stack when appropriate (e.g., after login).

---

### ViewModels and UI state
- Pattern: each screen has a corresponding ViewModel (Hilt injected) and exposes UI-observable state using `StateFlow` for continuous state and `SharedFlow` for one-shot events.
- Backing properties: use private `MutableStateFlow` / `MutableSharedFlow` and expose immutable `StateFlow`/`SharedFlow` to the UI to enforce single-writer semantics.
- Common state shapes:
    - Text inputs: `StateFlow<String>` (e.g., email, password).
    - Loading indicators: `StateFlow<Boolean>`.
    - Error messages: `StateFlow<String?>`.
    - Selection indices: `StateFlow<Int>`.
    - Aggregated data (e.g., counts map): `StateFlow<Map<Status, Int>>`.
- Events and navigation: emit one-shot events (e.g., `loginSuccess`) using `MutableSharedFlow` to notify the UI to navigate or show transient messages.
- Compose integration:
    - In composables, collect state flows using `val state by viewModel.someFlow.collectAsState()` or `collectAsState(initial = ...)`.
    - For one-shot SharedFlow events, use `LaunchedEffect` with `collect` to handle navigation or toasts once.

---

### Data layer (Room / Repository / DataStore)
- Entities & DAOs: Room entities represent medications, schedules, and logs; DAOs expose suspend functions and Flows for queries.
- Database: A single Room database is provided via Hilt so repositories can access DAOs.
- Repositories:
    - Abstract data sources and expose a consistent surface to ViewModels (Flows, suspend functions).
    - Encapsulate business logic like merging network results with local caches.
- Preferences: Use DataStore for lightweight persistent user preferences (e.g., selected language, accessibility flags).

---

### Networking (Retrofit / API)
---

### Sensors
- **Purpose**: Track device sensor data to monitor patient activity and health patterns.
- **Implementation**:
    - `SensorManagerWrapper`: Wraps Android's SensorManager and exposes sensor data via reactive Flows.
    - `SensorRepository`: Provides a clean API for accessing sensor data, following the single-source-of-truth pattern.
    - `SensorViewModel`: Manages UI state for sensor monitoring, including availability checks and real-time data.
    - `SensorScreen`: Composable UI that displays sensor status and allows users to start/stop monitoring.
- **Sensors supported**:
    - Step Counter: Tracks daily steps for activity monitoring.
    - Accelerometer: Detects movement patterns (can be used for fall detection).
    - Light Sensor: Monitors ambient light levels to infer user activity patterns.
- **Lifecycle management**:
    - Sensors are registered when monitoring starts via Flow collection.
    - Sensors are automatically unregistered when Flow is cancelled (via `awaitClose`).
    - ViewModelScope ensures proper cleanup when ViewModel is cleared.
- **Best practices**:
    - Uses callbackFlow to bridge callback-based SensorEventListener to coroutine Flow.
    - Thread-safe sensor registration/unregistration.
    - Exposes StateFlow for reactive UI updates.
    - Handles sensor unavailability gracefully.

---

### Background work & notifications
---

### Accessibility
- Add content descriptions, ensure touch targets are large, and verify TalkBack behavior.

---

### Typical workflow (runtime flow)
1. App start: `MainActivity` sets up the Compose host and calls `NavGraph()` with a `NavController`.
2. Login screen: `LoginViewModel` exposes email, password, isLoading, and error as StateFlows and a loginSuccess SharedFlow for navigation.
3. Dashboard/Progress screens: Obtain ViewModel via `hiltViewModel()` and collect StateFlows; UI events call ViewModel methods which update repositories and, through Flows, update UI.
4. Details flow: Navigate with route args (e.g., `details/{itemId}`) and load details from repository.
5. Background reminders: WorkManager posts notifications which deep-link into the app.

---