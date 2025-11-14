package com.example.medicationadherenceapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.medicationadherenceapp.ui.components.common.ScaffoldWithTopBar
import com.example.medicationadherenceapp.ui.components.dashboard.MainPage
import com.example.medicationadherenceapp.ui.components.login.LoginPageFromViewModel
import com.example.medicationadherenceapp.ui.components.progress.ProgressComponent
import com.example.medicationadherenceapp.ui.components.support.SupportScreen
import com.example.medicationadherenceapp.ui.viewmodel.LoginViewModel

// Object that holds route constants used throughout the app.
// Use simple string routes for top-level screens and include placeholders for routes
// that require arguments (e.g., DETAILS expects an `itemId`). Keeping these routes
// centralized reduces typos and makes refactors easier.
object Destinations {
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    // DETAILS demonstrates how to declare a route that expects an integer path argument
    // The actual composable below will declare the navArgument for "itemId".
    const val DETAILS = "details/{itemId}"
    const val SUPPORT = "support"
    const val PROGRESS = "progress"
}

@Composable
fun NavGraph(startDestination: String = Destinations.LOGIN) {
    // rememberNavController creates and remembers a NavController instance tied to
    // the composition lifecycle. Pass this controller to NavHost so composables
    // can navigate between destinations.
    val navController = rememberNavController()

    // NavHost defines the navigation graph. Each composable() call registers a
    // destination screen. startDestination controls which route is shown first.
    NavHost(navController = navController, startDestination = startDestination) {
        // Login route: obtains a LoginViewModel via Hilt and passes it to the
        // composable UI. The UI calls `onNavigateToDashboard` when login succeeds.
        composable(Destinations.LOGIN) {
            // hiltViewModel() gives a ViewModel scoped to the current NavBackStackEntry.
            val viewModel: LoginViewModel = hiltViewModel()
            LoginPageFromViewModel(viewModel = viewModel, onNavigateToDashboard = {
                // After a successful login, navigate to the dashboard. popUpTo with
                // inclusive = true removes the login destination from the back stack
                // so the user cannot press Back to return to the login screen.
                navController.navigate(Destinations.DASHBOARD) {
                    popUpTo(Destinations.LOGIN) { inclusive = true }
                }
            })
        }

        // Dashboard route: a top-level screen wrapped in a scaffold that provides
        // a top app bar. Use lightweight composables that read state from
        // their ViewModels (not shown here) to render UI.
        composable(Destinations.DASHBOARD) {
            ScaffoldWithTopBar {
                MainPage()
            }
        }


        // Support route: another top-level screen example.
        composable(Destinations.SUPPORT) {
            ScaffoldWithTopBar {
                SupportScreen()
            }
        }

        // Progress route: demonstrates reusing the same scaffold pattern.
        composable(Destinations.PROGRESS) {
            ScaffoldWithTopBar {
                ProgressComponent()
            }
        }

        // DETAILS route: shows how to declare a route that takes a typed argument
        // (NavType.IntType) and how to set up a deep link that maps a URL to the
        // same destination. The backStackEntry contains the parsed argument values.
        composable(
            route = Destinations.DETAILS,
            arguments = listOf(navArgument("itemId") { type = NavType.IntType }),
            deepLinks = listOf(navDeepLink { uriPattern = "https://www.example.com/details/{itemId}" })
        ) { backStackEntry ->
            // Retrieve the integer argument safely (nullable if missing). In a
            // production screen, handle null (show error or navigate back).
            val itemId = backStackEntry.arguments?.getInt("itemId")
            // Log the itemId for debugging and to ensure the variable is used
            // (avoids an unused-variable warning). Replace this with your
            // DetailsScreen call when implementing the details UI.
            android.util.Log.d("NavGraph", "DETAILS route itemId=$itemId")
            // TODO: Show details screen based on itemId. Example:
            // itemId?.let { DetailsScreen(itemId = it) } ?: run { /* show error */ }
        }
    }
}
