# Networking Implementation

This document explains the networking architecture implemented for the Medication Adherence App.

## Architecture Overview

The networking layer follows an **offline-first** architecture pattern:
- **Local database (Room)** is the single source of truth
- **Remote API** syncs data when network is available
- **Graceful degradation** when offline - app continues to work with local data
- **Automatic retry** for failed network requests

## Components

### 1. Data Transfer Objects (DTOs)
Located in `data/remote/dto/`:
- `UserDto.kt` - User authentication and profile data
- `MedicationDto.kt` - Medication, schedules, and intake records
- `HealthTipDto.kt` - Health tips data
- `SyncDto.kt` - Bulk sync response
- `Result.kt` - Sealed class for handling network states (Success/Error/Loading)

Each DTO has extension functions to convert between network models and local entities.

### 2. API Service
`ApiService.kt` - Retrofit interface defining all REST endpoints:

#### Authentication
- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `POST /auth/logout` - User logout

#### User Management
- `GET /users/{userId}` - Get user details
- `PUT /users/{userId}` - Update user
- `DELETE /users/{userId}` - Delete user

#### Family Management
- `GET /family/{familyMemberId}` - Get family member
- `POST /family` - Create family member
- `POST /family/link` - Link family to patient
- `GET /users/{patientId}/family` - Get patient's family

#### Medications
- `GET /medications` - Get all medications
- `POST /medications` - Create medication
- `PUT /medications/{medicationId}` - Update medication
- `DELETE /medications/{medicationId}` - Delete medication

#### Schedules
- `GET /patients/{patientId}/schedules` - Get patient schedules
- `POST /schedules` - Create schedule
- `PATCH /schedules/{scheduleId}/status` - Update schedule status

#### Intake Records
- `POST /intakes` - Record medication intake
- `GET /schedules/{scheduleId}/intakes` - Get intake records

#### Health Tips
- `GET /health-tips` - Get health tips (with pagination)
- `POST /health-tips` - Create health tip

#### Sync
- `GET /sync/patient/{patientId}` - Bulk sync all patient data

### 3. Interceptors
Located in `data/remote/interceptors/`:

#### AuthInterceptor
- Automatically adds JWT token to request headers
- Retrieves token from DataStore
- Skips auth for login/register endpoints

#### LoggingInterceptor
- Logs all HTTP requests and responses
- Only active in DEBUG builds
- Useful for debugging API issues

#### ErrorHandlingInterceptor
- Converts HTTP errors to custom exceptions
- Handles network errors (timeout, no connection)
- Provides meaningful error messages

### 4. Network Module
`di/network/NetworkModule.kt` - Hilt dependency injection:
- Configures Retrofit with base URL
- Sets up OkHttpClient with interceptors
- Provides Gson for JSON serialization
- Configures timeouts (30s connect/read/write)

### 5. Safe API Call Helper
`SafeApiCall.kt` - Utility functions:
- `safeApiCall()` - Wraps API calls in Result type
- `safeApiCallWithRetry()` - Automatic retry with exponential backoff
- Handles exceptions and converts to Result.Error

### 6. Repositories (Updated)
All repositories now implement offline-first pattern:

#### UserRepository
- `login()` - Authenticate user
- `addUser()` - Register new user (syncs to server)
- `syncPatientFamily()` - Fetch family from server
- All CRUD operations sync with server when available

#### MedicationRepository
- `addMedication()` - Create medication (syncs to server)
- `scheduleMedication()` - Create schedule (syncs to server)
- `syncMedications()` - Fetch all medications from server
- `syncPatientSchedules()` - Fetch schedules from server
- `updateScheduleStatus()` - Mark medication as taken

#### HealthTipRepository
- `syncHealthTips()` - Fetch latest tips from server
- Supports pagination with limit/offset

### 7. Sync Manager
`SyncManager.kt` - Centralized sync coordinator:
- `syncPatientData()` - Full sync of all patient data
- `syncMedications()` - Sync medications only
- `syncPatientSchedules()` - Sync schedules only
- `syncHealthTips()` - Sync health tips only
- `isNetworkAvailable()` - Check network connectivity

## Configuration

### Base URL
Update the base URL in `NetworkModule.kt`:
```kotlin
private const val BASE_URL = "https://api.medicationapp.com/v1/"
```

For development/testing, you can use:
- Local server: `http://10.0.2.2:8080/` (Android emulator)
- Mock server: `https://mockapi.io/projects/your-project/`

### Authentication Token
Tokens are stored in DataStore and automatically added to requests.

To save a token after login:
```kotlin
dataStoreManager.savePreference(AuthInterceptor.AUTH_TOKEN_KEY, token)
```

### Timeouts
Modify in `NetworkModule.kt`:
```kotlin
private const val CONNECT_TIMEOUT = 30L
private const val READ_TIMEOUT = 30L
private const val WRITE_TIMEOUT = 30L
```

## Usage Examples

### 1. Login User
```kotlin
viewModelScope.launch {
    val result = userRepository.login(username, password)
    when (result) {
        is Result.Success -> {
            // Save token and navigate to dashboard
            dataStoreManager.savePreference(
                AuthInterceptor.AUTH_TOKEN_KEY, 
                result.data.token ?: ""
            )
        }
        is Result.Error -> {
            // Show error message
            _errorState.value = result.message
        }
        is Result.Loading -> {
            // Show loading indicator
        }
    }
}
```

### 2. Add Medication with Sync
```kotlin
val medication = Medication(
    name = "Aspirin",
    dosageMg = 100f
)

val result = medicationRepository.addMedication(medication)
when (result) {
    is Result.Success -> {
        // Medication saved locally and synced to server
    }
    is Result.Error -> {
        // Error syncing, but saved locally
    }
    is Result.Loading -> { }
}
```

### 3. Sync All Patient Data
```kotlin
val result = syncManager.syncPatientData(patientId)
when (result) {
    is Result.Success -> {
        val syncResult = result.data
        Log.d("Sync", "Synced ${syncResult.medicationsCount} medications")
        Log.d("Sync", "Synced ${syncResult.schedulesCount} schedules")
    }
    is Result.Error -> {
        // Handle sync error
    }
    is Result.Loading -> { }
}
```

### 4. Observe Schedules (Reactive)
```kotlin
// In ViewModel
val schedules: StateFlow<List<MedicationSchedule>> = 
    medicationRepository.getSchedules(patientId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
```

## Error Handling

The implementation provides several custom exceptions:
- `NetworkException` - No internet or network timeout
- `UnauthorizedException` - 401 - Invalid credentials
- `ForbiddenException` - 403 - Access denied
- `NotFoundException` - 404 - Resource not found
- `ServerException` - 5xx - Server error

All errors are wrapped in `Result.Error` with meaningful messages.

## Testing

### Mock Server
For testing without a real backend:
1. Use [MockAPI](https://mockapi.io/) or [JSON Server](https://github.com/typicode/json-server)
2. Update BASE_URL to your mock server
3. Ensure response format matches DTOs

### Network Simulation
Test offline scenarios:
1. Disable network in emulator
2. App should continue working with cached data
3. Changes should sync when network returns

## Dependencies

Required in `libs.versions.toml`:
```toml
retrofit = "3.0.0"
okhttp = "5.0.0-alpha.14"
```

Required in `app/build.gradle.kts`:
```kotlin
implementation(libs.retrofit)
implementation(libs.retrofit.converter.gson)
implementation(libs.okhttp)
implementation(libs.okhttp.logging.interceptor)
```

## Next Steps

1. **Implement Background Sync** - Use WorkManager to sync periodically
2. **Conflict Resolution** - Handle conflicts when offline changes conflict with server
3. **Cache Strategy** - Implement cache expiration policies
4. **Offline Queue** - Queue failed requests to retry later
5. **Push Notifications** - Firebase Cloud Messaging for real-time updates

## Security Considerations

- ✅ HTTPS only (enforced in base URL)
- ✅ JWT token authentication
- ✅ Tokens stored securely in DataStore
- ✅ Sensitive data not logged in production
- ⚠️ TODO: Implement certificate pinning
- ⚠️ TODO: Add request signing
- ⚠️ TODO: Implement token refresh mechanism
