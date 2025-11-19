# 🚀 Quick Start: Using the Networking Layer

## Step 1: Sync Gradle Dependencies

First, sync your project to download the new OkHttp dependency:

```bash
# In Android Studio
File → Sync Project with Gradle Files
```

Or via command line:
```bash
./gradlew build
```

## Step 2: Configure Base URL

Update the BASE_URL in `NetworkModule.kt` to point to your backend:

```kotlin
// For production
private const val BASE_URL = "https://api.yourdomain.com/api/v1/"

// For local development (Android Emulator)
private const val BASE_URL = "http://10.0.2.2:8080/api/v1/"

// For local development (Real Device - use your computer's IP)
private const val BASE_URL = "http://192.168.1.100:8080/api/v1/"
```

## Step 3: Adjust Logging (Optional for Production)

Logging is enabled by default for development. For production builds, update `NetworkModule.kt`:

```kotlin
val loggingInterceptor = HttpLoggingInterceptor().apply {
    // For development
    level = HttpLoggingInterceptor.Level.BODY
    
    // For production
    // level = HttpLoggingInterceptor.Level.NONE
}
```

Or use BuildConfig for automatic switching:

```kotlin
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE
    }
}
```

## Step 4: Use in ViewModels

### Example: Login

```kotlin
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()
    
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            when (val result = userRepository.login(username, password)) {
                is NetworkResult.Success -> {
                    _loginState.value = LoginState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    // Try offline login as fallback
                    val user = userRepository.loginOffline(username, password)
                    if (user != null) {
                        _loginState.value = LoginState.OfflineSuccess(user)
                    } else {
                        _loginState.value = LoginState.Error(result.message)
                    }
                }
                is NetworkResult.Loading -> {
                    // Already handled above
                }
            }
        }
    }
}

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data class Success(val response: LoginResponse) : LoginState()
    data class OfflineSuccess(val user: User) : LoginState()
    data class Error(val message: String) : LoginState()
}
```

### Example: Medication List with Refresh

```kotlin
@HiltViewModel
class MedicationListViewModel @Inject constructor(
    private val medicationRepository: MedicationRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {
    
    // Always observe from local DB
    val medications = medicationRepository.getMedications()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()
    
    val isConnected = networkMonitor.isConnected
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    // Pull to refresh
    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            medicationRepository.refreshMedications()
            _isRefreshing.value = false
        }
    }
    
    // Add medication with optimistic UI update
    fun addMedication(name: String, dosageMg: Float) {
        viewModelScope.launch {
            val medication = Medication(
                name = name,
                dosageMg = dosageMg
            )
            
            // Write to local DB (UI updates immediately via Flow)
            medicationRepository.addMedication(medication)
            
            // Sync to remote if online
            if (networkMonitor.isCurrentlyConnected()) {
                medicationRepository.syncMedicationToRemote(medication)
            }
        }
    }
}
```

## Step 5: Use in Compose UI

### Login Screen

```kotlin
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val loginState by viewModel.loginState.collectAsState()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { viewModel.login(username, password) },
            enabled = loginState !is LoginState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loginState is LoginState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Login")
            }
        }
        
        // Handle states
        when (val state = loginState) {
            is LoginState.Success -> {
                LaunchedEffect(Unit) {
                    onLoginSuccess()
                }
            }
            is LoginState.OfflineSuccess -> {
                LaunchedEffect(Unit) {
                    // Show toast: "Logged in offline"
                    onLoginSuccess()
                }
            }
            is LoginState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            else -> {}
        }
    }
}
```

### Medication List with Pull-to-Refresh

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationListScreen(
    viewModel: MedicationListViewModel = hiltViewModel()
) {
    val medications by viewModel.medications.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isConnected by viewModel.isConnected.collectAsState()
    
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Medications") },
                actions = {
                    // Connection indicator
                    Icon(
                        imageVector = if (isConnected) 
                            Icons.Default.CloudDone 
                        else 
                            Icons.Default.CloudOff,
                        contentDescription = if (isConnected) 
                            "Online" 
                        else 
                            "Offline",
                        tint = if (isConnected) 
                            Color.Green 
                        else 
                            Color.Gray
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Show add dialog */ }
            ) {
                Icon(Icons.Default.Add, "Add Medication")
            }
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
                    text = "No medications yet",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn {
                    items(medications) { medication ->
                        MedicationItem(medication)
                    }
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

@Composable
fun MedicationItem(medication: Medication) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = medication.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${medication.dosageMg} mg",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
```

## Step 6: Test the Implementation

### Test Network Calls

1. **Online Test**: Enable network on your device/emulator
2. **Offline Test**: Disable network and verify offline-first behavior
3. **Sync Test**: Go offline, make changes, go online and observe sync

### Test with Mock Backend

You can use tools like:
- **Postman Mock Server**
- **JSON Server** (npm install -g json-server)
- **MockWebServer** for unit tests

Example JSON Server setup:
```bash
# Install
npm install -g json-server

# Create db.json
{
  "medications": [],
  "schedules": [],
  "health-tips": []
}

# Run server
json-server --watch db.json --port 8080
```

## Common Issues & Solutions

### Issue: Lint error about OkHttp version
**Solution**: Already fixed! OkHttp version updated to 5.3.2 (stable)

### Issue: Network calls fail
**Solution**: 
1. Check BASE_URL is correct
2. For emulator, use `10.0.2.2` not `localhost`
3. For real device, use computer's local IP

### Issue: Offline mode not working
**Solution**: Data is cached locally automatically. Check Room database is setup correctly.

### Issue: Token not persisting
**Solution**: Currently in-memory. For persistence, use EncryptedSharedPreferences or DataStore.

### Issue: Too much logging in production
**Solution**: Set logging level to NONE for release builds (see Step 3 above)

## Next Steps

1. ✅ Sync Gradle
2. ✅ Configure BASE_URL
3. ✅ Logging is enabled (adjust for production if needed)
4. ✅ Test login flow
5. ✅ Test data sync
6. 🔜 Implement WorkManager for background sync
7. 🔜 Add conflict resolution
8. 🔜 Implement secure token storage

## Need Help?

- See `NETWORKING_GUIDE.md` for detailed documentation
- See `NETWORKING_IMPLEMENTATION_SUMMARY.md` for architecture overview
- Check individual file comments for API details

Happy coding! 🎉

