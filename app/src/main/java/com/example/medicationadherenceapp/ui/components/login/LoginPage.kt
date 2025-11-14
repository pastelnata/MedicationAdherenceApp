package com.example.medicationadherenceapp.ui.components.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medicationadherenceapp.UserType
import com.example.medicationadherenceapp.ui.theme.MedicationAdherenceAppTheme
import com.example.medicationadherenceapp.ui.viewmodel.LoginViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun LoginPage(onLogin: (UserType) -> Unit) {
    var selectedUserType by remember { mutableStateOf<UserType?>(null) }

    if (selectedUserType == null) {
        UserTypeSelection(onSelect = { selectedUserType = it })
    } else {
        LoginScreen(userType = selectedUserType!!, onBack = { selectedUserType = null }, onLogin = { onLogin(selectedUserType!!) })
    }
}

/**
 * ViewModel-backed entry for navigation graph to use.
 * Collects ViewModel state and forwards events into the existing UI.
 */
@Composable
fun LoginPageFromViewModel(viewModel: LoginViewModel, onNavigateToDashboard: () -> Unit) {
    // collect one-shot success to navigate
    LaunchedEffect(viewModel) {
        viewModel.loginSuccess.collect { _ ->
            onNavigateToDashboard()
        }
    }

    // Observe simple state (not yet used by the current UI implementation)
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // We'll pass an onLogin that calls viewModel.login; to reuse the composable structure
    LoginPage(onLogin = { userType -> viewModel.login(userType) })
}

// Hilt convenience overload so callers can omit passing the ViewModel
@Composable
fun LoginPageFromViewModel(onNavigateToDashboard: () -> Unit) {
    val vm: LoginViewModel = hiltViewModel()
    LoginPageFromViewModel(viewModel = vm, onNavigateToDashboard = onNavigateToDashboard)
}

@Composable
fun UserTypeSelection(onSelect: (UserType) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to MedCare", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Your comprehensive medication management system", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Text("Who are you?", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelect(UserType.PATIENT) },
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = "Patient", modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("I'm a Patient", style = MaterialTheme.typography.titleMedium)
                    Text("Manage my medications and health", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelect(UserType.FAMILY) },
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, contentDescription = "Family Member", modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("I'm a Family Member", style = MaterialTheme.typography.titleMedium)
                    Text("Monitor and support my loved one", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun LoginScreen(userType: UserType, onBack: () -> Unit, onLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isSignUp by remember { mutableStateOf(false) }

    // Simulate login process
    val onLoginClick = {
        isLoading = true
        error = null
        onLogin()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = when {
                    isSignUp -> if (userType == UserType.PATIENT) "Patient Sign Up" else "Family Member Sign Up"
                    else -> if (userType == UserType.PATIENT) "Patient Login" else "Family Member Login"
                },
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isSignUp) "Create an account to get started" else "Sign in to access your dashboard",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                isError = error != null
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                isError = error != null
            )
            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isSignUp) "Sign Up" else "Login")
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { isSignUp = !isSignUp }) {
                    Text(if (isSignUp) "Already have an account? Login" else "Don't have an account? Sign Up")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPagePreview() {
    MedicationAdherenceAppTheme {
        LoginPage(onLogin = {})
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPatientPreview() {
    MedicationAdherenceAppTheme {
        LoginScreen(userType = UserType.PATIENT, onBack = {}, onLogin = {})
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenFamilyPreview() {
    MedicationAdherenceAppTheme {
        LoginScreen(userType = UserType.FAMILY, onBack = {}, onLogin = {})
    }
}
