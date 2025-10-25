package com.example.medicationadherenceapp.userInterface

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.time.ZonedDateTime
import java.time.ZoneId


data class NavItem(val title: String, val icon: ImageVector, val badgeCount: Int? = null)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithTopBar(
    initialDrawerValue: DrawerValue = DrawerValue.Closed,
    content: @Composable () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = initialDrawerValue)
    val scope = rememberCoroutineScope()
    val currentScreen = remember { mutableStateOf("Medications") }
    val navItems = listOf(
        NavItem("Medications", Icons.Filled.MedicalServices, 3),
        NavItem("Progress", Icons.Filled.Timeline),
        NavItem("Messages", Icons.AutoMirrored.Filled.Message),
        NavItem("Help & Support", Icons.AutoMirrored.Filled.Help)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.fillMaxHeight()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("MedCare", style = MaterialTheme.typography.titleLarge)
                        Text("name", style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(modifier = Modifier.padding(vertical = 8.dp))
                    navItems.forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(item.title) },
                            selected = currentScreen.value == item.title,
                            onClick = {
                                currentScreen.value = item.title
                                scope.launch { drawerState.close() }
                            },
                            badge = { item.badgeCount?.let { Text(it.toString()) } }
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                        label = { Text("Sign Out") },
                        selected = false,
                        onClick = { /*TODO*/ }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("MedCare", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Welcome back, name",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Navigation drawer"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                when (currentScreen.value) {
                    "Messages" -> {
                        // Example fake data for now
                        val sampleMessages = listOf(
                            MessageItem("1", "Dr. Smith", ZonedDateTime.now(ZoneId.systemDefault()), "Please confirm your next appointment.", false),
                            MessageItem("2", "Mom", ZonedDateTime.now(ZoneId.systemDefault()).minusDays(1), "I am so proud of you.", true)
                        )
                        MessagesScreen(messages = sampleMessages)
                    }
                    else -> content()
                }
            }
        }
    }
}

//@Preview(name = "Scaffold - Closed Drawer")
//@Composable
//fun ScaffoldWithTopBarPreview() {
//    ScaffoldWithTopBar {
//        Text(text = "This is the content of the page")
//    }
//}

//@Preview(name = "Scaffold - Open Drawer")
//@Composable
//fun ScaffoldWithTopBarOpenDrawerPreview() {
//    ScaffoldWithTopBar(initialDrawerValue = DrawerValue.Open) {
//        Text(text = "This is the content of the page")
//    }
//}