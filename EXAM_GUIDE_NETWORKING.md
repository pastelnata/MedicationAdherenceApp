# Networking - Complete Exam Guide

## Table of Contents
1. [Quick Facts & Overview](#quick-facts--overview)
2. [Exam Questions & Answers](#exam-questions--answers)
3. [Architecture Deep Dive](#architecture-deep-dive)
4. [Code Examples You Should Know](#code-examples-you-should-know)
5. [Common Scenarios & Solutions](#common-scenarios--solutions)
6. [Quick Reference](#quick-reference)

---

## Quick Facts & Overview

### What is implemented?
- **24 REST API endpoints** (authentication, users, medications, schedules, intakes, health tips, sync)
- **Offline-first architecture** (local DB first, sync to remote)
- **Type-safe error handling** (NetworkResult sealed class)
- **Authentication** with token management
- **Network monitoring** (reactive connectivity detection)

### Technology Stack
- **Retrofit**: 3.0.0 (REST API client)
- **OkHttp**: 5.3.2 (HTTP client with logging)
- **Gson**: JSON serialization/deserialization
- **Kotlin Coroutines**: Async operations
- **Flow**: Reactive data streams
- **Hilt**: Dependency injection

### Key Files
- `ApiService.kt` - API endpoint definitions
- `RemoteDataSource.kt` - API wrapper with error handling
- `NetworkModule.kt` - DI configuration
- `NetworkResult.kt` - Result wrapper
- `NetworkMonitor.kt` - Connectivity monitoring
- `dto/` folder - Data Transfer Objects

---

## Exam Questions & Answers

### Q1: What is the networking architecture pattern used in this app?

**Answer:**
The app uses an **offline-first architecture** with three distinct paths:

1. **Read Path**: UI always reads from local Room database via Flow
   - Fast and works offline
   - No network calls needed for reads

2. **Write Path**: Optimistic updates
   - Data written to local DB immediately
   - UI updates instantly via Flow
   - Sync to remote API in background

3. **Refresh Path**: Pull-to-refresh
   - Explicit refresh methods fetch from API
   - Update local cache
   - Flow emits updates automatically

**Why this pattern?**
- App works completely offline
- Fast UI updates (no waiting for network)
- Single source of truth (Room database)
- Eventual consistency with remote server

---

### Q2: How is error handling implemented in the networking layer?

**Answer:**
Using a **type-safe sealed class** called `NetworkResult<T>`:

```kotlin
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}
```

**Three types of errors handled:**
1. **Network errors** (IOException) - no internet, timeout
2. **HTTP errors** (4xx, 5xx) - server errors, auth failures
3. **Parsing errors** - malformed JSON

**In ViewModels:**
```kotlin
when (val result = repository.login(name, password)) {
    is NetworkResult.Success -> { /* handle success */ }
    is NetworkResult.Error -> { /* show error message */ }
    is NetworkResult.Loading -> { /* show loading */ }
}
```

**Benefits:**
- Type-safe (compile-time checks)
- Exhaustive when expressions
- Clear error states
- Easy to test

---

### Q3: Explain how authentication works with token management.

**Answer:**
**Authentication Flow (5 steps):**

1. **User Login** - ViewModel calls `userRepository.login(name, password)`
2. **API Call** - RemoteDataSource sends credentials to `/auth/login`
3. **Token Storage** - On success, auth token stored in RemoteDataSource
4. **Token Injection** - All subsequent API calls include `Authorization: Bearer {token}` header
5. **Offline Fallback** - If network fails, app checks local DB for credentials

**Implementation:**
```kotlin
// In RemoteDataSource
private var authToken: String? = null

suspend fun login(name: String, password: String): NetworkResult<LoginResponse> {
    return when (val result = apiService.login(LoginRequest(name, password))) {
        is Success -> {
            // Store token
            authToken = result.data.token
            // Cache user locally
            userDao.insertUser(result.data.toEntity())
            result
        }
        is Error -> result
    }
}

// Token injection in all API calls
suspend fun getMedications(): NetworkResult<List<MedicationDto>> {
    return safeApiCall {
        apiService.getMedications("Bearer $authToken")
    }
}
```

**Offline Login:**
```kotlin
suspend fun loginOffline(name: String, password: String): User? {
    val user = userDao.getUserByName(name)
    return if (user?.password == password) user else null
}
```

---

### Q4: How does the app handle offline scenarios?

**Answer:**
**Three offline strategies:**

1. **Offline Reads** - Always works
   - UI reads from local Room database
   - No network needed
   - Data from previous sync available

2. **Offline Writes** - Optimistic updates
   - Write to local DB immediately
   - UI updates via Flow
   - Changes queued for sync when online

3. **Offline Login** - Fallback authentication
   - Check local DB for stored credentials
   - Compare password hash
   - Grant access without network

**Network Monitoring:**
```kotlin
class NetworkMonitor @Inject constructor(context: Context) {
    val isConnected: Flow<Boolean> = callbackFlow {
        // Emits true/false based on connectivity
    }
}

// In ViewModel
networkMonitor.isConnected.collect { isOnline ->
    if (isOnline) {
        syncPendingChanges()
    }
}
```

**Benefits:**
- App never crashes due to network issues
- Users can continue working offline
- Automatic sync when connection restored

---

### Q5: What are DTOs and why are they used?

**Answer:**
**DTO = Data Transfer Object**

DTOs are separate classes for network communication, different from Room entities.

**Why separate DTOs from Entities?**
1. **Separation of concerns** - Network format ≠ Database format
2. **API evolution** - Server changes don't break database
3. **Security** - Don't send passwords from server
4. **Flexibility** - Transform data formats

**Example:**
```kotlin
// DTO (from API)
data class MedicationDto(
    val medicationId: String,      // UUID as String
    val name: String,
    val dosageMg: Float
)

// Entity (in Room)
@Entity
data class Medication(
    @PrimaryKey val medicationId: UUID,  // UUID type
    val name: String,
    val dosageMg: Float
)

// Conversion functions
fun MedicationDto.toEntity(): Medication {
    return Medication(
        medicationId = UUID.fromString(medicationId),
        name = name,
        dosageMg = dosageMg
    )
}

fun Medication.toDto(): MedicationDto {
    return MedicationDto(
        medicationId = medicationId.toString(),
        name = name,
        dosageMg = dosageMg
    )
}
```

---

### Q6: Explain the Repository pattern with network integration.

**Answer:**
**Repository = Single Source of Truth**

Repositories abstract both local and remote data sources, exposing a simple API to ViewModels.

**Three types of operations:**

1. **Local-First Reads (Flow)**
```kotlin
fun getSchedules(patientId: UUID): Flow<List<MedicationSchedule>> = 
    medicationDao.getMedicationSchedules(patientId)
```

2. **Optimistic Writes**
```kotlin
suspend fun addMedication(med: Medication) {
    // Write locally (UI updates immediately)
    medicationDao.insertMedication(med)
    
    // Sync to remote in background
    if (networkMonitor.isCurrentlyConnected()) {
        syncMedicationToRemote(med)
    }
}
```

3. **Network Refresh**
```kotlin
suspend fun refreshMedications(): NetworkResult<Unit> {
    return when (val result = remoteDataSource.getMedications()) {
        is NetworkResult.Success -> {
            // Update local cache
            result.data.forEach { dto ->
                medicationDao.insertMedication(dto.toEntity())
            }
            NetworkResult.Success(Unit)
        }
        is NetworkResult.Error -> result
        is NetworkResult.Loading -> result
    }
}
```

**ViewModel Usage:**
```kotlin
class MedicationViewModel @Inject constructor(
    private val repository: MedicationRepository
) : ViewModel() {
    
    // Always observe from repository (local DB)
    val medications = repository.getMedications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    // Pull to refresh
    fun refresh() {
        viewModelScope.launch {
            repository.refreshMedications()
            // UI updates automatically via Flow
        }
    }
}
```

---

### Q7: How is Retrofit configured with dependency injection?

**Answer:**
**Hilt NetworkModule provides all networking dependencies:**

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .serializeNulls()
            .create()
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .callTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/api/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
```

**Configuration highlights:**
- **Logging**: Full request/response logging (development)
- **Timeouts**: 30 seconds for all operations
- **Retry**: Automatic retry on connection failure
- **Singleton**: Single instance app-wide

---

### Q8: What API endpoints are available?

**Answer:**
**24 REST endpoints across 7 categories:**

**1. Authentication (3)**
- `POST /auth/login` - Login with credentials
- `POST /auth/register` - Create new account
- `POST /auth/logout` - Logout and invalidate token

**2. Users (3)**
- `GET /users/{userId}` - Get user details
- `PUT /users/{userId}` - Update user
- `DELETE /users/{userId}` - Delete user

**3. Medications (5)**
- `GET /medications` - List all medications
- `GET /medications/{id}` - Get specific medication
- `POST /medications` - Create medication
- `PUT /medications/{id}` - Update medication
- `DELETE /medications/{id}` - Delete medication

**4. Schedules (5)**
- `GET /schedules?patientId={id}` - List schedules for patient
- `GET /schedules/{id}` - Get specific schedule
- `POST /schedules` - Create schedule
- `PUT /schedules/{id}` - Update schedule
- `DELETE /schedules/{id}` - Delete schedule

**5. Intake Records (3)**
- `GET /intakes?scheduleId={id}` - List intake records
- `POST /intakes` - Record medication taken
- `DELETE /intakes/{id}` - Delete intake record

**6. Health Tips (2)**
- `GET /health-tips?limit={n}` - List health tips
- `GET /health-tips/{id}` - Get specific tip

**7. Sync (2)**
- `POST /sync/schedules` - Bulk sync schedules
- `POST /sync/intakes` - Bulk sync intakes

**Plus:**
- `GET /health` - Health check endpoint

---

### Q9: How does the RemoteDataSource handle errors?

**Answer:**
**RemoteDataSource wraps all API calls with comprehensive error handling:**

```kotlin
class RemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    private suspend fun <T> safeApiCall(
        apiCall: suspend () -> Response<ApiResponse<T>>
    ): NetworkResult<T> {
        return try {
            val response = apiCall()
            
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.success && body.data != null) {
                    NetworkResult.Success(body.data)
                } else {
                    NetworkResult.Error(
                        message = body?.message ?: "Unknown error",
                        code = response.code()
                    )
                }
            } else {
                // Parse error body
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                NetworkResult.Error(
                    message = errorMessage,
                    code = response.code()
                )
            }
        } catch (e: IOException) {
            // Network error (no internet, timeout)
            NetworkResult.Error("Network error: ${e.message}")
        } catch (e: Exception) {
            // Any other error
            NetworkResult.Error("Unexpected error: ${e.message}")
        }
    }
}
```

**Error categories:**
1. **IOException** - Network unavailable, timeout
2. **HTTP 4xx** - Client errors (bad request, unauthorized)
3. **HTTP 5xx** - Server errors
4. **Parsing errors** - Invalid JSON
5. **Null response** - Missing data

---

### Q10: Describe the complete flow of a login operation.

**Answer:**
**Step-by-step login flow:**

**1. UI Layer (Composable)**
```kotlin
Button(onClick = { viewModel.login(username, password) }) {
    Text("Login")
}

when (val state = loginState) {
    is LoginState.Loading -> CircularProgressIndicator()
    is LoginState.Success -> { /* Navigate to dashboard */ }
    is LoginState.Error -> Text(state.message, color = Red)
}
```

**2. ViewModel**
```kotlin
fun login(name: String, password: String) {
    viewModelScope.launch {
        _loginState.value = LoginState.Loading
        
        when (val result = userRepository.login(name, password)) {
            is NetworkResult.Success -> {
                _loginState.value = LoginState.Success(result.data)
            }
            is NetworkResult.Error -> {
                // Try offline fallback
                val user = userRepository.loginOffline(name, password)
                if (user != null) {
                    _loginState.value = LoginState.OfflineSuccess(user)
                } else {
                    _loginState.value = LoginState.Error(result.message)
                }
            }
            is NetworkResult.Loading -> {}
        }
    }
}
```

**3. Repository**
```kotlin
suspend fun login(name: String, password: String): NetworkResult<LoginResponse> {
    return when (val result = remoteDataSource.login(name, password)) {
        is NetworkResult.Success -> {
            // Store token
            result.data.token?.let { remoteDataSource.setAuthToken(it) }
            
            // Cache user locally
            val user = User(
                userId = UUID.fromString(result.data.userId),
                name = result.data.name,
                password = password,
                userType = UserType.valueOf(result.data.userType)
            )
            userDao.insertUser(user)
            
            NetworkResult.Success(result.data)
        }
        is NetworkResult.Error -> result
        is NetworkResult.Loading -> result
    }
}
```

**4. RemoteDataSource**
```kotlin
suspend fun login(name: String, password: String): NetworkResult<LoginResponse> {
    return safeApiCall {
        apiService.login(LoginRequest(name, password))
    }
}
```

**5. ApiService (Retrofit)**
```kotlin
@POST("auth/login")
suspend fun login(@Body request: LoginRequest): Response<ApiResponse<LoginResponse>>
```

**Flow summary:**
UI → ViewModel → Repository → RemoteDataSource → ApiService → Network → Server

---

## Architecture Deep Dive

### Offline-First Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                         UI LAYER                             │
│  Jetpack Compose Screens (Login, Dashboard, etc.)          │
└──────────────────────┬──────────────────────────────────────┘
                       │ collectAsState()
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                      VIEWMODEL LAYER                         │
│  StateFlow<UiState> + User Events → Repository calls       │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                    REPOSITORY LAYER                          │
│  Single Source of Truth - Coordinates Local + Remote        │
├──────────────────────┬───────────────────────┬──────────────┤
│  Flow<Data> (Read)   │  suspend fun (Write)  │  refresh()   │
└──────────────────────┼───────────────────────┼──────────────┘
                       │                       │
        ┌──────────────┴──────────┐           │
        ▼                         ▼           ▼
┌────────────────┐        ┌─────────────────────────────┐
│   LOCAL DB     │        │    NETWORK LAYER            │
│   Room DAO     │        │  RemoteDataSource           │
│   DataStore    │        │  + ApiService (Retrofit)    │
└────────────────┘        └─────────────────────────────┘
        │                              │
        │ Flow emits                   │ HTTP
        │ updates                      │
        ▼                              ▼
┌────────────────┐              ┌──────────────┐
│  Room Database │              │  REST API    │
│  (SQLite)      │              │  Server      │
└────────────────┘              └──────────────┘
```

### Data Flow Patterns

**Pattern 1: Read (Offline-First)**
```
UI observes Flow → ViewModel exposes StateFlow → Repository returns DAO Flow
→ Room emits from local DB → UI updates automatically
```

**Pattern 2: Write (Optimistic)**
```
UI calls ViewModel → ViewModel calls Repository → Repository writes to DAO
→ Room Flow emits → UI updates immediately
→ Background: Sync to API when online
```

**Pattern 3: Refresh (Pull-to-Refresh)**
```
UI triggers refresh → ViewModel calls repository.refresh()
→ Repository calls RemoteDataSource → API call
→ Success: Write to DAO → Room Flow emits → UI updates
```

---

## Code Examples You Should Know

### Example 1: ApiService Definition

```kotlin
interface ApiService {
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ApiResponse<LoginResponse>>
    
    @GET("medications")
    suspend fun getMedications(
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<MedicationDto>>>
    
    @POST("schedules")
    suspend fun createSchedule(
        @Body request: CreateScheduleRequest,
        @Header("Authorization") token: String
    ): Response<ApiResponse<MedicationScheduleDto>>
}
```

### Example 2: Repository with Network

```kotlin
@Singleton
class MedicationRepository @Inject constructor(
    private val medicationDao: MedicationDao,
    private val remoteDataSource: RemoteDataSource,
    private val networkMonitor: NetworkMonitor
) {
    // Local-first read
    fun getMedications(): Flow<List<Medication>> = 
        medicationDao.getAllMedications()
    
    // Optimistic write
    suspend fun addMedication(medication: Medication) {
        medicationDao.insertMedication(medication)
        
        if (networkMonitor.isCurrentlyConnected()) {
            syncMedicationToRemote(medication)
        }
    }
    
    // Explicit refresh
    suspend fun refreshMedications(): NetworkResult<Unit> {
        return when (val result = remoteDataSource.getMedications()) {
            is NetworkResult.Success -> {
                result.data.forEach { dto ->
                    medicationDao.insertMedication(dto.toEntity())
                }
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
    
    // Sync to remote
    private suspend fun syncMedicationToRemote(
        medication: Medication
    ): NetworkResult<Unit> {
        val dto = medication.toDto()
        return when (val result = remoteDataSource.createMedication(
            dto.name, 
            dto.dosageMg
        )) {
            is NetworkResult.Success -> {
                medicationDao.insertMedication(result.data.toEntity())
                NetworkResult.Success(Unit)
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
}
```

### Example 3: ViewModel with Network States

```kotlin
@HiltViewModel
class MedicationListViewModel @Inject constructor(
    private val repository: MedicationRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {
    
    // Observe medications from local DB
    val medications = repository.getMedications()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Refresh state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()
    
    // Network connectivity
    val isConnected = networkMonitor.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()
    
    // Pull to refresh
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _error.value = null
            
            when (val result = repository.refreshMedications()) {
                is NetworkResult.Success -> {
                    // Local DB updated, Flow will emit
                }
                is NetworkResult.Error -> {
                    _error.value = result.message
                }
                is NetworkResult.Loading -> {}
            }
            
            _isRefreshing.value = false
        }
    }
    
    // Add medication
    fun addMedication(name: String, dosageMg: Float) {
        viewModelScope.launch {
            val medication = Medication(name = name, dosageMg = dosageMg)
            repository.addMedication(medication)
            // UI updates automatically via Flow
        }
    }
}
```

### Example 4: Compose UI with Network States

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationListScreen(
    viewModel: MedicationListViewModel = hiltViewModel()
) {
    val medications by viewModel.medications.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    val error by viewModel.error.collectAsState()
    
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medications") },
                actions = {
                    // Network indicator
                    Icon(
                        imageVector = if (isConnected) 
                            Icons.Default.CloudDone 
                        else 
                            Icons.Default.CloudOff,
                        contentDescription = if (isConnected) "Online" else "Offline",
                        tint = if (isConnected) Color.Green else Color.Gray
                    )
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            if (medications.isEmpty()) {
                Text(
                    text = "No medications",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn {
                    items(medications) { medication ->
                        MedicationItem(medication)
                    }
                }
            }
            
            // Error snackbar
            error?.let { errorMessage ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Text(errorMessage)
                }
            }
            
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
```

---

## Common Scenarios & Solutions

### Scenario 1: User has no internet connection

**Problem:** User tries to login but network is down  
**Solution:** Offline login fallback

```kotlin
// In UserRepository
suspend fun login(name: String, password: String): NetworkResult<LoginResponse> {
    // Try online login first
    val result = remoteDataSource.login(name, password)
    
    if (result is NetworkResult.Error) {
        // Fallback to offline
        val user = loginOffline(name, password)
        if (user != null) {
            return NetworkResult.Success(
                LoginResponse(
                    userId = user.userId.toString(),
                    name = user.name,
                    userType = user.userType.name,
                    token = null // No token in offline mode
                )
            )
        }
    }
    
    return result
}
```

### Scenario 2: API returns 401 Unauthorized

**Problem:** Auth token expired  
**Solution:** Catch 401, clear token, redirect to login

```kotlin
// In RemoteDataSource
private suspend fun <T> safeApiCall(...): NetworkResult<T> {
    try {
        val response = apiCall()
        
        if (response.code() == 401) {
            // Token expired
            setAuthToken(null)
            return NetworkResult.Error(
                message = "Session expired. Please login again.",
                code = 401
            )
        }
        // ... rest of handling
    } catch (e: Exception) {
        // ...
    }
}

// In ViewModel
when (result) {
    is NetworkResult.Error -> {
        if (result.code == 401) {
            navigateToLogin()
        } else {
            showError(result.message)
        }
    }
}
```

### Scenario 3: Sync conflicts (user edits same data on two devices)

**Problem:** Local changes conflict with remote  
**Solution:** Implement conflict resolution strategy

```kotlin
// Last-write-wins strategy
suspend fun syncSchedule(schedule: MedicationSchedule): NetworkResult<Unit> {
    return when (val result = remoteDataSource.updateSchedule(
        schedule.scheduleId.toString(),
        schedule.toDto()
    )) {
        is NetworkResult.Success -> {
            // Server version wins - update local
            medicationDao.insertMedicationSchedule(result.data.toEntity())
            NetworkResult.Success(Unit)
        }
        is NetworkResult.Error -> {
            if (result.code == 409) { // Conflict
                // Fetch server version and merge
                refreshSchedule(schedule.scheduleId)
            }
            result
        }
        is NetworkResult.Loading -> result
    }
}
```

### Scenario 4: Slow network connection

**Problem:** API calls timeout  
**Solution:** Configured timeouts + retry logic

```kotlin
// In NetworkModule
fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .callTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
}
```

### Scenario 5: Large data sync after being offline

**Problem:** Need to sync many items efficiently  
**Solution:** Batch sync operations

```kotlin
// Bulk sync endpoint
suspend fun syncAllPendingSchedules(): NetworkResult<Unit> {
    val pendingSchedules = medicationDao.getPendingSchedules()
    
    if (pendingSchedules.isEmpty()) {
        return NetworkResult.Success(Unit)
    }
    
    val dtos = pendingSchedules.map { it.toDto() }
    
    return when (val result = remoteDataSource.syncSchedules(dtos)) {
        is NetworkResult.Success -> {
            // Mark as synced
            result.data.forEach { dto ->
                medicationDao.insertMedicationSchedule(dto.toEntity())
            }
            NetworkResult.Success(Unit)
        }
        is NetworkResult.Error -> result
        is NetworkResult.Loading -> result
    }
}
```

---

## Quick Reference

### Key Concepts to Remember

1. **Offline-First** = Local DB first, sync to remote
2. **NetworkResult** = Type-safe error handling (Success/Error/Loading)
3. **DTO** = Data Transfer Object (separate from Room entities)
4. **Flow** = Reactive stream for UI updates
5. **suspend** = Coroutine function for async operations

### File Locations

```
app/src/main/java/com/example/medicationadherenceapp/
├── data/
│   ├── remote/
│   │   ├── ApiService.kt              ← API endpoints
│   │   ├── RemoteDataSource.kt        ← API wrapper
│   │   ├── NetworkResult.kt           ← Result type
│   │   ├── NetworkMonitor.kt          ← Connectivity
│   │   └── dto/
│   │       ├── UserDto.kt             ← User DTOs
│   │       ├── MedicationDto.kt       ← Medication DTOs
│   │       ├── HealthTipDto.kt        ← Health tip DTOs
│   │       └── ApiResponse.kt         ← Response wrapper
│   └── local/
│       ├── entities/                  ← Room entities
│       └── dao/                       ← Room DAOs
├── repository/
│   ├── UserRepository.kt              ← User + auth
│   ├── MedicationRepository.kt        ← Medications
│   └── HealthTipRepository.kt         ← Health tips
└── di/
    └── network/
        └── NetworkModule.kt           ← DI config
```

### Gradle Dependencies

```kotlin
// Networking
implementation("com.squareup.retrofit2:retrofit:3.0.0")
implementation("com.squareup.retrofit2:converter-gson:3.0.0")
implementation("com.squareup.okhttp3:logging-interceptor:5.3.2")
```

### Common Commands

```bash
# Build project
./gradlew assembleDebug

# Run lint
./gradlew lint

# Run tests
./gradlew testDebugUnitTest
```

### Configuration

```kotlin
// Base URL (NetworkModule.kt)
private const val BASE_URL = "https://api.example.com/api/v1/"

// For development
private const val BASE_URL = "http://10.0.2.2:8080/api/v1/"  // Emulator
private const val BASE_URL = "http://192.168.1.100:8080/api/v1/"  // Device
```

---

## Exam Tips

### What to memorize:
1. ✅ Offline-first pattern (3 paths: read, write, refresh)
2. ✅ NetworkResult sealed class structure
3. ✅ Authentication flow (5 steps)
4. ✅ DTO vs Entity difference
5. ✅ Repository pattern (single source of truth)

### What to be able to explain:
1. ✅ How error handling works
2. ✅ Why DTOs are separate from entities
3. ✅ How offline scenarios are handled
4. ✅ Complete login flow (UI → Server)
5. ✅ Benefits of offline-first architecture

### Code you should be able to write:
1. ✅ NetworkResult when expression
2. ✅ Simple API endpoint in ApiService
3. ✅ Repository refresh function
4. ✅ ViewModel with network states
5. ✅ Basic Compose UI with pull-to-refresh

---

**Last Updated:** November 19, 2025  
**Status:** ✅ Complete Exam Guide  
**Coverage:** All networking concepts, code examples, and common scenarios

