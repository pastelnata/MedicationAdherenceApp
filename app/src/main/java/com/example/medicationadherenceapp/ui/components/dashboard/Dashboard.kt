package com.example.medicationadherenceapp.ui.components.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.medicationadherenceapp.ui.components.common.ScaffoldWithTopBar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.medicationadherenceapp.ui.components.dashboard.MedicationAlertCard
import com.example.medicationadherenceapp.ui.components.dashboard.MedicationDoseRow
import com.example.medicationadherenceapp.ui.viewmodel.DashboardViewModel


@Composable
fun MainPage(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val todayDoses by viewModel.todayDoses.collectAsState()

    // pick one overdue dose (if any) for the "Needs Immediate Attention" box
    val urgentDose = todayDoses.firstOrNull { (it.minutesOverdue ?: 0) > 0 }
    // the rest go into "Today's Medication"
    val otherDoses = todayDoses.filterNot { it.id == urgentDose?.id }

    val statusCounts by viewModel.statusCounts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        // hard coded for now if
        MedStatusSummary(
                statusCounts = statusCounts
        )

        Spacer(modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(_root_ide_package_.com.example.medicationadherenceapp.DrawableIcons.ALARM.id),
                contentDescription = "Alert",
                modifier = Modifier.size(30.dp).padding(end = 4.dp),
                tint = Color(0xFFD32F2F)
            )
            HeaderText("Needs Immediate Attention")
        }

        // medication boxes
        urgentDose?.let { dose ->
            Spacer(modifier = Modifier.padding(top = 8.dp))

            MedicationAlertCard(
                medName = dose.name,
                dosage = dose.dosage,
                frequency = dose.frequency ?: "",
                minutesOverdue = dose.minutesOverdue ?: 0,
                instructions = dose.instructions ?: "",
                onConfirmedTaken = { viewModel.markDoseTaken(dose.id) },
                onSkip = { viewModel.skipDose(dose.id) }
            )
        }


        Spacer(modifier = Modifier.padding(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeaderText("Today's Medication")
            OutlinedCard {
                Text(
                    // hard coded for now as well
                    text = "1/4 taken",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // today's medication boxes
        if (otherDoses.isNotEmpty()) {
            Spacer(modifier = Modifier.padding(top = 8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                otherDoses.forEach { dose ->
                    MedicationAlertCard(
                        medName = dose.name,
                        dosage = dose.dosage,
                        frequency = dose.frequency ?: "",
                        minutesOverdue = dose.minutesOverdue ?: 0,
                        instructions = dose.instructions ?: "",
                        onConfirmedTaken = { viewModel.markDoseTaken(dose.id) },
                        onSkip = { viewModel.skipDose(dose.id) }
                    )
                }
            }
        }


        Spacer(modifier = Modifier.padding(8.dp))

        HealthTips()
    }
}

@Composable
fun HeaderText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )
}

@Preview
@Composable
fun MainPagePreview() {
    ScaffoldWithTopBar {
        MainPage()
    }
}

//comments from lectures:
//
//// L1: TechChoice_Factors
//
//// Choosing native, hybrid, PWA, or cross-platform depends on:
//// - Target audience and platforms
//// - Performance requirements
//// - Development cost + time
//// - Maintenance + scalability. :contentReference[oaicite:3]{index=3}
//
//// L1: Approach_Native
//// Native Apps = Built for one platform (Swift/iOS, Kotlin/Android).
//// Pros: Best performance, full device access, best UI/UX, strong security.
//// Cons: Highest cost, separate codebases, slower development. :contentReference[oaicite:4]{index=4}
//
//// L1: Approach_Hybrid
//// Hybrid Apps = Web tech (HTML/CSS/JS) inside a native wrapper (Cordova/Ionic).
//// Pros: Fast + cheap development, one codebase.
//// Cons: Slower than native, limited device access, UI often not truly native. :contentReference[oaicite:5]{index=5}
//
//// L1: Approach_PWA
//// Progressive Web Apps = Web apps with offline mode, push notifications,
//// installable without app store.
//// Good for cheap, fast deployment with minimal native feature use. :contentReference[oaicite:6]{index=6}
//
//// L1: Approach_CrossPlatform
//// Cross-Platform (Flutter, React Native, Xamarin, KMP).
//// Pros: One codebase, cost-effective, near-native performance.
//// Cons: Missing native APIs sometimes, larger app sizes, debugging complexity. :contentReference[oaicite:7]{index=7}
//
//// L1: Android_vs_iOS_Ecosystem
//// Key differences:
//// - Languages: Kotlin/Java vs Swift/Obj-C
//// - IDEs: Android Studio vs Xcode (Mac-only)
//// - Hardware: Android = highly fragmented; iOS = uniform
//// - Distribution: Android Play Store + sideload; iOS mostly App Store only
//// - Updates: Android slow adoption; iOS fast adoption. :contentReference[oaicite:8]{index=8}
//
//// L1: Android_vs_iOS_Permissions_Security
//// Android: runtime permissions, more open → higher sideloading risk.
//// iOS: stricter sandboxing + centralized permissions → more controlled. :contentReference[oaicite:9]{index=9}
//
//// L1: DesignGuidelines_Android_iOS
//// Android → Material Design: flexible, colorful, motion-rich.
//// iOS → Human Interface Guidelines: minimalist, elegant. :contentReference[oaicite:11]{index=11}
//
//
////L4: Introduction to Android and Kotlin basics
//
//// W39_Kotlin_WhyKotlin
//// Kotlin is Google’s preferred Android language (since 2017).
//// Advantages: concise syntax, fewer bugs, null-safety, coroutines, full Java interoperability.
//// Used in most modern Android apps.  :contentReference[oaicite:1]{index=1}
//
//// W39_Kotlin_Primitives
//// Kotlin uses primitive types internally but exposes them as objects → can call methods on numbers.
//// Example: 3.5.plus(4) → 7.5.  :contentReference[oaicite:2]{index=2}
//
//// W39_Kotlin_TypeCasting
//// Explicit casts: toInt(), toByte(), etc.
//// Smart casting: Kotlin auto-casts after 'is' check.
//// Safe casts: obj as? Int → returns null instead of crashing.  :contentReference[oaicite:3]{index=3}
//
//// W39_Kotlin_Variables
//// var → mutable, val → immutable (recommended).
//// Kotlin has strong compile-time type inference.
//// Once assigned, a variable’s type cannot change.  :contentReference[oaicite:4]{index=4}
//
//// W39_Ranges_Conditions
//// Ranges use '..' and work with if and when.
//// Example: if (x in 1..100) { ... }.
//// when supports ranges: in 1..39 → "Got results!".  :contentReference[oaicite:5]{index=5}
//
//// W39_Loops
//// for loops iterate directly over items: for (item in list).
//// withIndex() gives (index, element).
//// Supports step, downTo, and ranges.  :contentReference[oaicite:6]{index=6}
//
//// W39_Lists_Arrays
//// listOf() → immutable list, mutableListOf() → mutable list.
//// arrayOf() → array (fixed size), elements are mutable.
//// Arrays can mix types; intArrayOf() for primitives.  :contentReference[oaicite:7]{index=7}
//
//// W39_NullSafety
//// By default variables cannot be null: var x: Int = null → error.
//// Nullable types use ?: Int?
//// Safe call: x?.dec()
//// Elvis operator ?: provides fallback.
//// !! forces NPE → avoid except in special cases.  :contentReference[oaicite:8]{index=8}
//
//// W39_Expressions
//// In Kotlin, almost everything is an expression with a value (even if, when).
//// println() returns Unit (similar to void).  :contentReference[oaicite:9]{index=9}
//
//// W39_Functions
//// Declared with fun keyword.
//// Can be top-level (no need for classes).
//// Single-expression functions: fun double(x: Int) = x * 2.
//// Functions are first-class → can be stored, passed, returned.  :contentReference[oaicite:10]{index=10}
//
//// W39_Lambdas
//// Lambda syntax: { param: Type -> body }.
//// Function type: val f: (Int) -> Int = { it * 2 }.
//// Higher-order functions take or return functions.  :contentReference[oaicite:11]{index=11}
//
//// W39_HigherOrderFunctions
//// Common built-ins: map, filter, reduce, fold.
//// map transforms items; filter keeps items; reduce accumulates; fold accumulates with initial value.  :contentReference[oaicite:12]{index=12}
//
//// W39_FunctionReferences
//// Use :: to pass existing functions: encode("abc", ::reverse).
//// Kotlin prefers the function parameter last → trailing lambda syntax.  :contentReference[oaicite:13]{index=13}
//
//// W39_Classes
//// Class has properties + functions.
//// Constructors: primary in class header, secondary inside class.
//// init block runs logic during construction.  :contentReference[oaicite:14]{index=14}
//
//// W39_Inheritance
//// Use 'open' on classes/methods to allow inheritance.
//// Abstract classes do NOT need open.  :contentReference[oaicite:15]{index=15}
//
//// W39_DataClass
//// data class auto-creates: toString(), equals(), hashCode(), copy(), componentN().
//// Best for models/DTOs.  :contentReference[oaicite:16]{index=16}
//
//// W39_ObjectSingleton
//// object creates a singleton → only one instance in the app.  :contentReference[oaicite:17]{index=17}
//
//// W39_AndroidStudioTools
//// Tools: Android Studio, Gradle, Emulator, ADB/Logcat.
//// Gradle handles dependencies, build config, obfuscation (R8).  :contentReference[oaicite:18]{index=18}
//
//// W39_ProjectStructure
//// Key folders: manifests/, java|kotlin/, res/, Gradle scripts.
//// MVVM pattern often used: model/, repository/, viewmodel/, ui/.  :contentReference[oaicite:19]{index=19}
//
//// W39_AndroidManifest
//// Central config for the whole app.
//// Declares: Activities, Services, BroadcastReceivers, Providers, SDK versions, permissions.  :contentReference[oaicite:20]{index=20}
//
//// W39_Logcat
//// Debugging tool to view device/emulator logs in real time.
//// Used for tracking crashes, system events, lifecycle logs.  :contentReference[oaicite:21]{index=21}
//
//// W39_AndroidOSArchitecture
//// Layers (top→bottom):
//// Applications → App Framework → Android Runtime (ART) → Native Libraries → HAL → Linux Kernel.  :contentReference[oaicite:22]{index=22}
//
//// W39_LinuxKernel
//// Provides low-level features: process mgmt, memory mgmt, hardware drivers, networking, power mgmt.  :contentReference[oaicite:23]{index=23}
//
//// W39_HAL_HardwareAbstractionLayer
//// Standard interface between hardware & Android framework.
//// Examples: GPS HAL, Camera HAL.  :contentReference[oaicite:24]{index=24}
//
//// W39_ART_AndroidRuntime
//// Runs .dex bytecode.
//// Dalvik (old) used JIT → slower startup.
//// ART uses AOT compilation → faster execution, lower CPU, saves battery.
//// Modern ART uses hybrid JIT/AOT.  :contentReference[oaicite:25]{index=25}
//
//// W39_NativeLibraries
//// Precompiled C/C++ libs for performance-critical tasks (media, graphics, DB).
//// Accessed via JNI or NDK.  :contentReference[oaicite:26]{index=26}
//
//// W39_AppFramework
//// Provides system services: ActivityManager, WindowManager,
//// ContentProviders, NotificationManager, View system.
//// Supports building UI + lifecycle mgmt.  :contentReference[oaicite:27]{index=27}
//
//// W39_ApplicationsLayer
//// Top layer: user-facing apps (system apps + installed apps).  :contentReference[oaicite:28]{index=28}
//
//
////L5: Android development with Kotlin-2
//
//// W40_Activities_Basics
//// An Activity = one focused UI screen the user interacts with.
//// Entry point for UI; subclass of android.app.Activity.
//// Manages a window where UI is drawn.  :contentReference[oaicite:1]{index=1}
//
//// W40_Activity_Lifecycle
//// Key lifecycle callbacks:
//// onCreate() → initialize UI and data
//// onStart() → activity becomes visible
//// onResume() → user can interact
//// onPause() → partially obscured (e.g., dialog on top)
//// onStop() → no longer visible
//// onDestroy() → final cleanup before destruction.  :contentReference[oaicite:2]{index=2}
//
//// W40_Activity_StateManagement
//// Activities can be destroyed during rotation.
//// Use onSaveInstanceState() + onRestoreInstanceState()
//// to prevent data loss (mainly in XML-based apps).  :contentReference[oaicite:3]{index=3}
//
//// W40_Intents
//// Intent = messaging object to request actions from components.
//// Uses:
////  - Start Activity
////  - Start Service
////  - Send Broadcast
////  - Pass data between components.  :contentReference[oaicite:4]{index=4}
//
//// W40_Explicit_vs_Implicit_Intents
//// Explicit Intent → target specific class (inside your app).
//// Implicit Intent → ask system to handle action (e.g., open URL, dial).  :contentReference[oaicite:5]{index=5}
//
//// W40_Services_Broadcast_ContentProviders
//// Services → background work (music, downloads).
//// Broadcast Receivers → listen for system events (battery low, network).
//// Content Providers → share data between apps (contacts, media).  :contentReference[oaicite:6]{index=6}
//
////Jetpack compose (modern Android ui):
//
//// W40_Compose_Why
//// Compose = declarative UI toolkit.
//// Benefits: simple state mgmt, fewer bugs, interoperable with old views,
//// better performance, easier to reason about UI.  :contentReference[oaicite:7]{index=7}
//
//// W40_Composable_Functions
//// @Composable marks functions that emit UI.
//// Can accept parameters like normal functions.
//// Compose UI = built entirely from composable functions.  :contentReference[oaicite:8]{index=8}
//
//// W40_Compose_Phases
//// UI rendering pipeline:
//// 1) Composition → build UI tree
//// 2) Layout → measure & position elements
//// 3) Drawing → render pixels to screen.  :contentReference[oaicite:9]{index=9}
//
//// W40_Recomposition
//// When state changes, Compose re-runs affected composables.
//// Only updates the parts of UI that depend on changed state → efficient.  :contentReference[oaicite:10]{index=10}
//
////compose modifiers:
//
//// W40_Modifiers
//// Modifiers adjust appearance, layout, and interaction:
//// - padding(), size(), offset()
//// - background(), border()
//// - clickable(), scrollable()
//// Always pass 'modifier' into custom composables for reusability.  :contentReference[oaicite:11]{index=11}
//
//// W40_Modifier_Order
//// Modifier order matters → padding().background() != background().padding().
//// Use Modifier.then() for conditional stacking.  :contentReference[oaicite:12]{index=12}
//
////State and state management:
//// W40_Compose_State
//// State = data that drives UI.
//// When state changes → Compose automatically updates affected UI.  :contentReference[oaicite:13]{index=13}
//
//// W40_Stateful_vs_Stateless_Composables
//// Stateful: remember { mutableStateOf(...) } inside composable → holds its own state.
//// Stateless: receives state + onChange callback via parameters → preferred for reuse.  :contentReference[oaicite:14]{index=14}
//
//// W40_ViewModel
//// ViewModel survives configuration changes (rotation).
//// Stores UI logic separately from UI code.
//// Compose integrates with LiveData/StateFlow.  :contentReference[oaicite:15]{index=15}
//
//// W40_Unidirectional_Data_Flow
//// State flows DOWN (parent → child).
//// Events flow UP (child → parent via callbacks).
//// Helps keep state predictable.  :contentReference[oaicite:16]{index=16}
//
//// W40_StateHoisting
//// Move state out of composable → parent owns state and provides:
//// (value, onValueChange).
//// Benefits: reusability, testability, single source of truth.  :contentReference[oaicite:17]{index=17}
//
////compose layout system:
//
//// W40_Row_Column_Box
//// Row → horizontal layout
//// Column → vertical layout
//// Box → stack children on top of each other (like FrameLayout)
//// Order matters → first child drawn at bottom.  :contentReference[oaicite:18]{index=18}
//
//// W40_FlowRow_FlowColumn
//// Like Row/Column but automatically wrap items onto new lines.
//// Useful for chips, tags, categories.  :contentReference[oaicite:19]{index=19}
//
//// W40_CustomLayoutModifiers
//// When built-in modifiers aren't enough, you can create custom layout logic.
//// Same underlying mechanism Google uses for Row/Column/Box.  :contentReference[oaicite:20]{index=20}
//
//// W40_CustomLayout
//// Full control over measuring + positioning children.
//// Constructed using Layout() composable.  :contentReference[oaicite:21]{index=21}
//
////ConstraintLayout (Compose version):
//
//// W40_ConstraintLayout_WhenToUse
//// Use when layout becomes too complex for nested Row/Column.
//// Supports constraints, guidelines, chains, barriers.  :contentReference[oaicite:22]{index=22}
//
//// W40_ConstraintLayout_Concepts
//// createRefs() → register UI elements
//// constrainAs(ref) → attach constraints
//// linkTo() → connect edges (start, top, end, bottom)
//// Guidelines → invisible alignment lines
//// Barriers → dynamic constraints based on sibling size.  :contentReference[oaicite:23]{index=23}
//
//// W40_LazyColumn_LazyRow
//// Compose alternative to RecyclerView.
//// LazyColumn → vertical list
//// LazyRow → horizontal list
//// Only render visible items → efficient memory usage.  :contentReference[oaicite:24]{index=24}
//
//
////L6
//
////Grids in Jetpack compose:
//// W41_Grids
//// Grids = scrollable layouts organizing items in rows/columns.
//// Implemented using LazyVerticalGrid / LazyHorizontalGrid.
//// GridCells.Fixed(n) → fixed number of columns.
//// GridCells.Adaptive(minSize) → columns adapt to screen size.  :contentReference[oaicite:1]{index=1}
//
//// W41_Grids example:
//// LazyVerticalGrid organizes items in rows/columns using GridCells.Fixed or Adaptive.
////LazyVerticalGrid(columns = GridCells.Fixed(2)) {
////    items(10) { index ->
////        Text("Item $index")
////    }
////}
//
//
////Coroutines and LaunchedEffect:
//// W41_Coroutines_Intro
//// Coroutines = lightweight async tasks (better than threads).
//// Use cases: network calls, database operations, heavy CPU tasks.
//// Benefits: non-blocking UI, structured concurrency, easier cancellation.  :contentReference[oaicite:2]{index=2}
//
//// W41_Coroutines_Intro example:
//// launch() starts a coroutine without returning a result.
////rememberCoroutineScope().launch(Dispatchers.IO) {
////    val result = loadData() // pretend network call
////    Log.d("TEST", "Result = $result")
////}
//
//
//// W41_Coroutine_Scopes
//// CoroutineScope defines lifecycle.
//// Common scopes:
//// - viewModelScope → tied to ViewModel lifecycle
//// - rememberCoroutineScope() → tied to Composable
//// - GlobalScope → avoid in Android (lives entire app).  :contentReference[oaicite:3]{index=3}
//
//// W41_Coroutine_Dispatchers
//// Dispatchers:
//// - Main: UI updates
//// - IO: network, database, file I/O
//// - Default: heavy CPU work.  :contentReference[oaicite:4]{index=4}
//
//// W41_Coroutine_Builders
//// launch {}  → fire-and-forget, no return value.
//// async {}   → returns Deferred<T>, use await().
//// withContext(dispatcher) → switch thread for code block.
//// runBlocking → ONLY for tests / main(); never in Compose/UI.  :contentReference[oaicite:5]{index=5}
//
//// W41_LaunchedEffect
//// LaunchedEffect runs coroutine when key changes.
//// Cancels & restarts automatically on key change.
//// Use for side effects:
//// - Snackbars/toasts
//// - Delayed navigation
//// - Fetch data on first composition
//// - Listen to state changes.  :contentReference[oaicite:6]{index=6}
//
//// W41_LaunchedEffect example:
//// Runs once when this composable enters the composition, or when 'key' changes.
////var count by remember { mutableStateOf(0) }
////LaunchedEffect(count) {
////    Log.d("TEST", "Count changed → $count")
////}
//
//
//// W41_Coroutine_vs_LaunchedEffect
//// Use rememberCoroutineScope → for user-triggered events (button clicks).
//// Use LaunchedEffect → for lifecycle/state-triggered events.  :contentReference[oaicite:7]{index=7}
//
////HorizontalPager/VerticalPager:
//// W41_Pagers
//// HorizontalPager & VerticalPager create swipeable screens.
//// Used for onboarding, image galleries, walkthroughs.
//// PagerState controls current page.  :contentReference[oaicite:8]{index=8}
//
//// W41_Pagers example:
//// Creates a horizontally swipeable pager with 5 pages.
////val pagerState = rememberPagerState(pageCount = { 5 })
////HorizontalPager(state = pagerState) { page ->
////    Text("Page: $page")
////}
//
//
////Compose navigation:
//// W41_SingleActivity
//// Modern Android uses Single-Activity architecture.
//// All screens = composables managed by NavController.
//// Benefits: shared ViewModels, consistency, easier testing, no Activity recreation.  :contentReference[oaicite:9]{index=9}
//
//// W41_NavHost example:
//// NavHost maps route strings to Composable "screens".
////val nav = rememberNavController()
////NavHost(navController = nav, startDestination = "home") {
////    composable("home") { Text("Home") }
////    composable("details") { Text("Details") }
////}
//
//// W41_NavigationBasics example:
//// Forward navigation + back navigation.
////Button(onClick = { nav.navigate("details") }) {
////    Text("Go to Details")
////}
//// Inside Details screen:
////Button(onClick = { nav.popBackStack() }) {
////    Text("Back")
////}
//
//// W41_Navigation_Arguments example:
//// Route with argument placeholder "{id}".
////composable("profile/{id}") { backStackEntry ->
////    val id = backStackEntry.arguments?.getString("id")
////    Text("Profile of user $id")
////}
//
//// W41_BottomNavigation example:
//// Bottom navigation with sealed routes.
////sealed class Screen(val route: String) {
////    data object Home : Screen("home")
////    data object Search : Screen("search")
////}
////NavigationBar {
////    NavigationBarItem(
////       selected = true,
////        onClick = { nav.navigate(Screen.Home.route) },
////        icon = { Icon(Icons.Default.Home, null) }
////    )
////}
//
//
//// W41_NavController
//// NavController manages screen navigation + back stack.
//// Created with rememberNavController().  :contentReference[oaicite:10]{index=10}
//
//// W41_NavHost
//// NavHost = navigation graph in Compose.
//// Maps route strings → Composable screens.  :contentReference[oaicite:11]{index=11}
//
//// W41_NavigationBasics
//// Forward navigation: navController.navigate("details")
//// Back navigation: navController.popBackStack()
//// Stack-based navigation (like browser history).  :contentReference[oaicite:12]{index=12}
//
//// W41_Navigation_Arguments
//// Pass arguments using route placeholders: "detail/{id}"
//// Extract using NavBackStackEntry.  :contentReference[oaicite:13]{index=13}
//
//// W41_BottomNavigation
//// Use sealed class for type-safe routes.
//// NavigationBar (Material 3) for bottom tabs.
//// NavHost inside Scaffold controls tab content.
//// Navigation retains state when switching tabs.  :contentReference[oaicite:14]{index=14}
//
////Material design 3 (M3):
//// W41_M3_Intro
//// Material Design 3 = latest Google design system.
//// Goals: personalization, accessibility, expressiveness.  :contentReference[oaicite:15]{index=15}
//
//// W41_M3_Theme
//// MaterialTheme provides:
//// - colorScheme
//// - typography
//// - shapes
//// Changes automatically apply across all M3 components.  :contentReference[oaicite:16]{index=16}
//
//// W41_M3_DynamicColor
//// Dynamic colors (Android 12+): extract colors from wallpaper.
//// dynamicLightColorScheme(context)
//// dynamicDarkColorScheme(context).  :contentReference[oaicite:17]{index=17}
//
//// W41_M3_DynamicColor: example
//// Uses dynamic wallpaper-based color scheme when available.
////val colors = if (Build.VERSION.SDK_INT >= 31)
////    dynamicDarkColorScheme(LocalContext.current)
////else
////    darkColorScheme()
////MaterialTheme(colorScheme = colors) { /* UI here */ }
//
//
//// W41_M3_ColorSystem
//// Material 3 color roles:
//// primary, onPrimary, secondary, tertiary, surface, background, error.
//// Each role has an “on” variant for contrast (text/icons).  :contentReference[oaicite:18]{index=18}
//
//// W41_M3_Usage
//// Use MaterialTheme.colorScheme.<role> instead of hardcoded colors.
//// UI auto-adapts to light/dark/dynamic color.  :contentReference[oaicite:19]{index=19}
//
//// W41_M3_Components
//// Key components: Scaffold, Surface, Buttons, NavigationBar, SearchBar.
//// Scaffold provides screen structure (top bar, bottom bar, FAB, body).  :contentReference[oaicite:20]{index=20}
//
////ViewModel - Deep Dive:
//// W41_ViewModel_Why
//// ViewModel stores & manages UI state.
//// Survives configuration changes (rotation).
//// Keeps business logic separate from UI.  :contentReference[oaicite:21]{index=21}
//
//// W41_ViewModel_Flow
//// _uiState → MutableStateFlow (private)
//// uiState  → StateFlow (public, read-only)
//// Pattern: internal mutable + external immutable.  :contentReference[oaicite:22]{index=22}
//
//// W41_ViewModel_Flow example:
//// ViewModel exposes read-only uiState while keeping internal _uiState mutable.
////class MyViewModel : ViewModel() {
////    private val _uiState = MutableStateFlow(0)
////    val uiState: StateFlow<Int> = _uiState
////    fun increment() { _uiState.value++ }
////}
//
//
//// W41_ViewModel_Lifecycle
//// Created when UI first appears.
//// Cleared only when Activity/Compose lifecycle is permanently destroyed.
//// Access in Compose via viewModel().  :contentReference[oaicite:23]{index=23}
//
//// W41_ViewModel_Lifecycle example
//// Access a ViewModel in Compose using viewModel().
////val vm: MyViewModel = viewModel()
////val count by vm.uiState.collectAsState()
////Text("Count: $count")
////or
//// W41_Navigation
////nav.navigate("home")
//
////L7: DI, persistence, and networking (Android 4)
//
////Dependency injection:
//
//// W43_Hilt_WhyDI
//// DI = a way to give objects their dependencies from the outside.
//// Why: more reusable, easier to refactor, and easier to test. :contentReference[oaicite:1]{index=1}
//
//// W43_Hilt_Basics
//// Hilt = Google's recommended DI framework for Android (built on Dagger).
//// Handles DI boilerplate and integrates with Application, Activity, ViewModel, Service, etc. :contentReference[oaicite:2]{index=2}
//
////Minimal hilt setup example:
//// W43_Hilt_Application
////// @HiltAndroidApp marks the Application class as DI root.
////@HiltAndroidApp
////class MyApp : Application()
//
//// W43_Hilt_AndroidEntryPoint
//// @AndroidEntryPoint lets Hilt inject dependencies into this Activity.
////@AndroidEntryPoint
////class MainActivity : ComponentActivity() {
////    @Inject lateinit var repo: UserRepository
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
//        // repo is ready to use here
////    }
////}
//
////Constructor injection + Module example:
//
//// W43_Hilt_ConstructorInjection
//// Preferred when you own the class and Hilt can instantiate it.
////class UserRepository @Inject constructor(
////    private val api: UserApi
////)
//
//// W43_Hilt_Module_Provides
//// Use @Module + @Provides when you can't use @Inject constructor (e.g. Retrofit, Room).
////@Module
////@InstallIn(SingletonComponent::class)
////object NetworkModule {
////
////    @Provides
////    fun provideBaseUrl(): String = "https://api.example.com"
////}
//
//// W43_Hilt_Binds
//// @Binds = bind interface → implementation, more efficient than @Provides.
////interface UserRepo {
////    fun getUser(id: String): User
////}
////class UserRepoImpl @Inject constructor() : UserRepo {
////    override fun getUser(id: String) = User(id, "John")
////}
////@Module
////@InstallIn(SingletonComponent::class)
////abstract class RepoModule {
////    @Binds
////    abstract fun bindUserRepo(impl: UserRepoImpl): UserRepo
////}
//
////Room (Local database):
//// W43_Room_Concepts
//// Room is a type-safe abstraction over SQLite.
//// Pieces: @Entity (table), @Dao (queries), RoomDatabase (DB), Repository (high-level API). :contentReference[oaicite:3]{index=3}
//
////Minimal Room setup:
//// W43_Room_Entity
//// @Entity data class → table in SQLite.
////@Entity
////data class User(
////    @PrimaryKey val id: Int,
////    val name: String
////)
//
//// W43_Room_Dao
//// @Dao interface → SQL operations.
////@Dao
////interface UserDao {
////    @Query("SELECT * FROM User")
////    fun getAll(): List<User>
////    @Insert
////    suspend fun insert(user: User)
////}
//
//// W43_Room_Database
//// RoomDatabase holds DAOs; usually one instance for the whole app.
////@Database(entities = [User::class], version = 1)
////abstract class AppDb : RoomDatabase() {
////    abstract fun userDao(): UserDao
////}
//
//// W43_Room_HiltIntegration
//// Typical Hilt module to provide a singleton Room database.
////@Module
////@InstallIn(SingletonComponent::class)
////object DbModule {
////    @Provides
////    fun provideDb(@ApplicationContext ctx: Context): AppDb =
////        Room.databaseBuilder(ctx, AppDb::class.java, "app_db").build()
////    @Provides
////    fun provideUserDao(db: AppDb): UserDao = db.userDao()
////}
//
////Room + flow/ stateflow
//// W43_Flow_StateFlow
//// Flow = cold stream of values over time.
//// StateFlow = hot stream holding current state (great for UI).
//// Room + Flow → UI auto-updates when DB changes. :contentReference[oaicite:4]{index=4}
//
////Reactive Room example:
//// W43_Room_Flow
//// DAO returns Flow<List<User>> for reactive updates.
////@Dao
////interface UserDao {
////    @Query("SELECT * FROM User")
////    fun observeUsers(): Flow<List<User>>
////}
//
//// W43_Room_ViewModel_StateFlow
//// ViewModel collects Flow from Room and exposes StateFlow to UI.
////class UserViewModel @Inject constructor(
////    userDao: UserDao
////) : ViewModel() {
////    val users: StateFlow<List<User>> =
////        userDao.observeUsers()
////            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
////}
//
//// W43_Room_UI_Collect
//// Compose collects StateFlow and recomposes when data changes.
////@Composable
////fun UserScreen(vm: UserViewModel = hiltViewModel()) {
////    val users by vm.users.collectAsState()
////    LazyColumn {
////        items(users) { Text(it.name) }
////    }
////}
//
////SharedPreferences:
//// W43_SharedPreferences_Concept
//// SharedPreferences = simple key-value storage for small data:
//// settings, flags, tokens. Supports String, Int, Boolean, Float, Long, Set<String>. :contentReference[oaicite:5]{index=5}
//
////example:
//// W43_SharedPreferences_Use
//// MODE_PRIVATE → only this app can read/write the file.
////val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
//// Write (async)
////prefs.edit().putBoolean("dark_mode", true).apply()
//// Read
////val dark = prefs.getBoolean("dark_mode", false)
//
////Datastore (modern replacement for SharedPreferences):
//// W43_DataStore_Concept
//// DataStore = modern, coroutine + Flow based key-value or typed storage.
//// Safer, async, and more Compose-friendly than SharedPreferences. :contentReference[oaicite:6]{index=6}
//
////Minimal preferences datastore example
//// W43_DataStore_Definition
//// Top-level extension property → one instance per Context.
////val Context.settingsDataStore by preferencesDataStore(name = "settings")
//
//// W43_DataStore_Repository
////class SettingsRepo(private val dataStore: DataStore<Preferences>) {
////    private val DARK_KEY = booleanPreferencesKey("dark_mode")
////    val darkModeFlow: Flow<Boolean> =
////        dataStore.data.map { prefs -> prefs[DARK_KEY] ?: false }
////    suspend fun setDarkMode(enabled: Boolean) {
////        dataStore.edit { it[DARK_KEY] = enabled }
////    }
////}
//
//// W43_DataStore_Compose
////@Composable
////fun SettingsScreen(repo: SettingsRepo) {
////    val darkMode by repo.darkModeFlow.collectAsState(initial = false)
////    Switch(checked = darkMode, onCheckedChange = { enabled ->
////        // launch coroutine from composable scope
////        val scope = rememberCoroutineScope()
////        scope.launch { repo.setDarkMode(enabled) }
////    })
////}
//
////Retrofit (Networking)
//// W43_Retrofit_Concept
//// Retrofit = type-safe HTTP client for Android.
//// Converts JSON↔Kotlin data classes, supports GET/POST/PUT/DELETE,
//// usually used with OkHttp + Gson/Moshi. :contentReference[oaicite:7]{index=7}
//
////Retrofit example:
//// W43_Retrofit_Model
//// Data class representing JSON response.
////data class WordItem(
////    val id: Int,
////    val word: String
////)
//
//// W43_Retrofit_ApiInterface
//// Retrofit interface describes HTTP endpoints.
////interface WordApiService {
////    @GET("words")
////    suspend fun getWords(): List<WordItem>
////}
//
//// W43_Retrofit_Instance
//// Build Retrofit client with baseUrl + converter.
////val retrofit: Retrofit = Retrofit.Builder()
////    .baseUrl("https://example.com/api/")
////    .addConverterFactory(GsonConverterFactory.create())
////    .build()
////val api: WordApiService = retrofit.create(WordApiService::class.java)
//
//// W43_Retrofit_CallWithCoroutines
//// Make network call safely from coroutine (e.g., inside ViewModel).
////viewModelScope.launch(Dispatchers.IO) {
////    val words = api.getWords()
//    // update UI state with words
////}
//
//// W43_Retrofit_Permission
//// INTERNET permission must be in AndroidManifest.xml for network calls.
//// <uses-permission android:name="android.permission.INTERNET" />
//
//
////L8: Services, Broadcast and Receiver:
//
//
//// W44_Service_Definition
//// A Service is an Android component for long-running background tasks with no UI.
//// Typical uses: music playback, uploads/downloads, location tracking. :contentReference[oaicite:0]{index=0}
//
//// W44_Service_Types
//// Three main service types:
//// 1) Started Service (Unbound) → runs independently after startService().
//// 2) Bound Service → allows clients to bind and communicate via IBinder.
//// 3) Foreground Service → runs with a persistent notification and higher priority. :contentReference[oaicite:1]{index=1}
//
//// W44_ForegroundService_Concept
//// Foreground services must show a visible notification at all times.
//// Used for long-running, user-aware tasks (GPS tracking, media playback, big downloads).
//// Requires FOREGROUND_SERVICE permission. :contentReference[oaicite:2]{index=2}
//
//// W44_ForegroundService_Evolution
//// Android 9+ → background execution limits.
//// Android 10 → foregroundServiceType attribute introduced.
//// Android 11+ → stricter background start restrictions.
//// Android 13 → POST_NOTIFICATIONS permission required for showing notifications.
//// Android 14+ → service types must be declared in Play Console. :contentReference[oaicite:3]{index=3}
//
//// W44_ForegroundService_KeyConcepts
//// Key components to implement:
//// - NotificationChannel (Android 8+)
//// - Notification (persistent)
//// - startForeground(id, notification)
//// - foregroundServiceType (location, mediaPlayback, dataSync, etc.). :contentReference[oaicite:4]{index=4}
//
//// W44_Service_RestartFlags
//// Service restart behavior:
//// START_STICKY → system restarts service with null intent.
//// START_NOT_STICKY → service only restarts if explicitly started again.
//// START_REDELIVER_INTENT → system restarts and redelivers last intent.
//// START_STICKY_COMPATIBILITY → legacy version of START_STICKY. :contentReference[oaicite:5]{index=5}
//
//// W44_Service_Manifest
//// Manifest controls whether the system or other apps can start or bind to a service.
//// Must declare foregroundServiceType in manifest for Android 10+. :contentReference[oaicite:6]{index=6}
//
//// W44_Service_Lifecycle
//// Started Services lifecycle:
//// - onCreate() → initialization
//// - onStartCommand() → handles each service start
//// - onDestroy() → cleanup
////
//// Bound Service lifecycle adds:
//// - onBind() → returns IBinder for client communication
//// - onUnbind() → last client disconnected
//// - onRebind() → client reconnected. :contentReference[oaicite:7]{index=7}
//
//// W44_BoundService_Concept
//// Bound Services allow direct interaction between client and service via IBinder.
//// Follows a client-server architecture.
//// Service exists only while at least one client is bound.
//// Used for controlling music playback or tracking download progress. :contentReference[oaicite:8]{index=8}
//
//// W44_BackgroundService
//// Background services run invisibly (no UI or notification).
//// Android 8+ heavily restricts them; system may kill them to save resources.
//// Should use WorkManager, JobScheduler, or ForegroundService for long-running tasks. :contentReference[oaicite:9]{index=9}
//
//// W44_Background_vs_Foreground
//// Background Service: low priority, can be killed, no UI.
//// Foreground Service: high priority, must show notification, runs indefinitely.
//// Typical uses:
//// - Background → sync, cleanup, analytics
//// - Foreground → GPS, music, big downloads. :contentReference[oaicite:10]{index=10}
//
//// W44_Broadcasts_Concept
//// Broadcasts = Android's publish-subscribe event system.
//// Allow system → app, app → app, or internal app communication.
//// Examples: BOOT_COMPLETED, BATTERY_LOW, AIRPLANE_MODE. :contentReference[oaicite:11]{index=11}
//
//// W44_CustomBroadcasts
//// Apps can send custom broadcasts using an action string (e.g., ACTION_CUSTOM_BROADCAST).
//// Receivers must listen via IntentFilter matching that action.
//// setPackage() can restrict broadcast to your app only. :contentReference[oaicite:12]{index=12}
//
//// W44_BroadcastReceiver_Definition
//// A BroadcastReceiver receives system or app broadcasts.
//// Runs briefly; cannot do long work directly.
//// Must be registered either statically (manifest) or dynamically (registerReceiver()). :contentReference[oaicite:13]{index=13}
//
//// W44_Receiver_Manifest
//// Manifest-declared receivers are triggered even when the app is not running.
//// BUT Android 8+ restricts most implicit broadcast receivers to improve performance. :contentReference[oaicite:14]{index=14}
//
//// W44_Receiver_Dynamic
//// Dynamic receivers are registered at runtime using registerReceiver().
//// Active only while Activity/Fragment/Service is running.
//// Best for in-app broadcasts. :contentReference[oaicite:15]{index=15}
//
//// W44_WhyBroadcastsMatter
//// Broadcasts allow apps to react to system events:
//// battery low, connectivity changes, boot completed.
//// Also used for internal messaging between components. :contentReference[oaicite:16]{index=16}
//
//
////L9: Broadcast and receivers, sensors, permission handling:
//
////Broadcasts and receivers:
//// W45_Broadcasts_Concept
//// Broadcasts allow system → app or app → app communication using publish-subscribe pattern. :contentReference[oaicite:2]{index=2}
//
//// W45_Broadcasts_Types
//// System broadcasts: BOOT_COMPLETED, AIRPLANE_MODE, BATTERY_LOW.
//// Custom broadcasts: defined by apps using unique action strings. :contentReference[oaicite:3]{index=3}
//
//// W45_CustomBroadcasts
//// Custom broadcast requires a unique action string.
//// Receivers must register with IntentFilter for the same action.
//// setPackage() restricts broadcast delivery to your own app. :contentReference[oaicite:4]{index=4}
//
//// W45_BroadcastReceiver_Definition
//// To receive broadcasts, implement BroadcastReceiver and register it. :contentReference[oaicite:5]{index=5}
//
//// W45_Receiver_Registration
//// Two types of registration:
//// 1) Manifest-declared (static) → limited after Android 8.0 for implicit broadcasts.
//// 2) Context-registered (dynamic) → registerReceiver() / unregisterReceiver(). :contentReference[oaicite:6]{index=6}
//
//// W45_SignaturePermissionBroadcast
//// Signature permissions allow broadcasts only to apps signed with same certificate.
//// Steps: define custom permission → require permission in receiver → send protected broadcast. :contentReference[oaicite:7]{index=7}
//
////Android permission handling:
//// W45_Permissions_InstallTime
//// Install-time permissions: automatically granted, non-sensitive (e.g., INTERNET). :contentReference[oaicite:8]{index=8}
//
//// W45_Permissions_Runtime
//// Runtime permissions: required for sensitive actions (camera, location).
//// Must be requested during app usage. :contentReference[oaicite:9]{index=9}
//
//// W45_Permissions_SingleOrMulti
//// Can request permissions individually or in groups.
//// Libraries like accompanist-permissions simplify Compose permission handling. :contentReference[oaicite:10]{index=10}
//
////Android sensors:
//// W45_Sensors_Types
//// Hardware sensors: accelerometer, gyroscope, magnetometer, proximity, light, barometer.
//// Software sensors: linear acceleration, gravity, rotation vector, step detector/counter. :contentReference[oaicite:11]{index=11}
//
//// W45_Sensors_Categories
//// Categories:
//// Motion sensors → movement/rotation
//// Environmental → ambient conditions
//// Position sensors → orientation/location. :contentReference[oaicite:12]{index=12}
//
//// W45_SensorFramework
//// Android Sensor Framework components:
//// SensorManager, Sensor, SensorEvent, SensorEventListener. :contentReference[oaicite:13]{index=13}
//
//// W45_SensorAccess_Steps
//// Steps to use a sensor:
//// 1) Get SensorManager
//// 2) Select specific sensor
//// 3) Register SensorEventListener. :contentReference[oaicite:14]{index=14}
//
//// W45_Sensor_Emulator
//// Emulator provides virtual sensors for testing motion, position, environmental data. :contentReference[oaicite:15]{index=15}
//
////Location (One-time and continuous):
//// W45_Location_Permissions
//// Location requires runtime permission (ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION).
//// Must be added in Manifest + requested at runtime. :contentReference[oaicite:16]{index=16}
//
//// W45_OneTimeLocation
//// One-time location uses fusedLocationClient.getCurrentLocation()
//// Good for snapshot queries (weather, check-in, etc.). :contentReference[oaicite:17]{index=17}
//
//// W45_ContinuousLocation
//// Continuous location requires:
//// 1) LocationRequest configuration
//// 2) LocationCallback receiving updates
//// 3) requestLocationUpdates() with Looper.getMainLooper(). :contentReference[oaicite:18]{index=18}
//
//// W45_Location_UseCases
//// One-time: quick queries (map pin, one-time update).
//// Continuous: tracking movement (running app, delivery tracking). :contentReference[oaicite:19]{index=19}
//
//// W45_Summary
//// Broadcasts = event delivery system.
//// Receivers = subscribers listening to intents.
//// Permissions = install-time vs runtime.
//// Sensors = hardware/software inputs via SensorManager.
//// Location = one-time vs continuous updates using Fused Location Provider. :contentReference[oaicite:20]{index=20}
//
//
////L10: Android push notification, WorkManager and AlarmManager
//
////push notifications (FCM):
//
//// W46_PN_WhatAreNotifications
//// Push notifications allow apps to send messages even when the app is closed or in background.
//// Used for reminders, updates, promotions. :contentReference[oaicite:1]{index=1}
//
//// W46_PN_FCM_Overview
//// Android uses Firebase Cloud Messaging (FCM) for push notifications.
//// Supports: notification payloads (auto-shown) + data payloads (handled manually). :contentReference[oaicite:2]{index=2}
//
//// W46_PN_HowFCMWworks
//// Steps:
//// 1) FCM SDK initializes
//// 2) Device generates FCM Registration Token
//// 3) App sends token to backend server
//// 4) Server sends push messages to specific token(s) via FCM backend. :contentReference[oaicite:3]{index=3}
//
//// W46_PN_PayloadTypes
//// Three payload types:
//// - Notification payload → auto displayed by system
//// - Data payload → always delivered to onMessageReceived()
//// - Mixed payload → has both. :contentReference[oaicite:4]{index=4}
//
//// W46_PN_AppStateBehavior
//// Notification behavior by app state:
//// Foreground → Notification not auto-shown; data delivered
//// Background → Notification auto-shown; data still delivered
//// Killed → Notification auto-shown; data delivered only with high priority
//// Force-stopped → Nothing delivered. :contentReference[oaicite:5]{index=5}
//
//// W46_PN_LaunchingApps
//// On Android 11+ apps must use <queries> in AndroidManifest.xml
//// to allow getLaunchIntentForPackage() to work due to package visibility restrictions. :contentReference[oaicite:6]{index=6}
//
////WorkManager:
//// W46_WorkManager_WhatIsIt
//// WorkManager = Jetpack library for reliable, deferrable background work.
//// Runs tasks even if app is closed or device restarts. Battery-efficient. :contentReference[oaicite:7]{index=7}
//
//// W46_WorkManager_UseCases
//// Use when:
//// - Syncing data periodically
//// - Uploading logs or files
//// - Applying image filters
//// - Tasks that must be guaranteed to run. :contentReference[oaicite:8]{index=8}
//
//// W46_WorkManager_Features
//// Supports: One-time work, periodic work, constraints (Wi-Fi, charging), chaining,
//// and automatic retry with exponential backoff. :contentReference[oaicite:9]{index=9}
//
//// W46_WorkManager_CoreConcepts
//// Core elements:
//// - Worker / CoroutineWorker (what to do)
//// - WorkRequest (when/how to run)
//// - WorkManager (the scheduler). :contentReference[oaicite:10]{index=10}
//
//// W46_Worker_Types
//// Worker → background thread, more boilerplate
//// CoroutineWorker → suspend functions, ideal for network/DB ops. :contentReference[oaicite:11]{index=11}
//
//// W46_WorkRequest_Types
//// OneTimeWorkRequest → runs once
//// PeriodicWorkRequest → repeats (min interval 15 min)
//// Work can have constraints (charging, unmetered network, idle). :contentReference[oaicite:12]{index=12}
//
//// W46_WorkManager_Chaining
//// WorkManager supports chaining multiple tasks in sequence.
//// Failure → triggers retry policy (exponential backoff). :contentReference[oaicite:13]{index=13}
//
//// W46_WorkManager_BestPractices
//// Best practices:
//// - Use CoroutineWorker for async tasks
//// - Always set constraints for efficiency
//// - Use unique work names to prevent duplicates
//// - Handle failures with retry logic. :contentReference[oaicite:14]{index=14}
//
////AlarmManager:
//// W46_AlarmManager_WhatIsIt
//// AlarmManager schedules tasks at exact or repeating times.
//// Works even if the app isn't running. Not guaranteed for long operations. :contentReference[oaicite:15]{index=15}
//
//// W46_AlarmManager_UseCases
//// Use for:
//// - Exact reminders
//// - Calendar-based triggers
//// - Time-based one-off events
//// - Waking phone for user-visible alarms. :contentReference[oaicite:16]{index=16}
//
//// W46_AlarmManager_vs_WorkManager
//// AlarmManager:
////  - Real-time precise tasks
////  - Not guaranteed
////  - API level 1+
//// WorkManager:
////  - Guaranteed execution
////  - Battery-aware
////  - Best for deferrable tasks. :contentReference[oaicite:17]{index=17}
//
//// W46_AlarmManager_Methods
//// AlarmManager methods summary:
//// - set() → one-time, inexact
//// - setExact() → one-time, exact (not in Doze)
//// - setRepeating() → auto repeating
//// - setInexactRepeating() → battery friendly repeating
//// - setExactAndAllowWhileIdle() → runs even in Doze (exact)
//// - setAlarmClock() → visible alarm shown to user. :contentReference[oaicite:18]{index=18}
//
////Summary:
//// W46_Summary
//// Push notifications → delivered via FCM, supports notification/data payloads.
//// WorkManager → guaranteed deferred background work (periodic or one-time).
//// AlarmManager → exact/time-based scheduling, not guaranteed for long tasks.
//// Choose WorkManager for reliability, AlarmManager for precise timing. :contentReference[oaicite:19]{index=19}
//
//
////L11: Kotlin multiplatform and IOS basics:
//
////Cross-platform frameworks (2025 overview)
//// W47_CrossPlatform_2025
//// Major cross-platform frameworks:
//// Flutter, React Native, Kotlin Multiplatform (KMP), .NET MAUI, Unity. :contentReference[oaicite:1]{index=1}
//
//// W47_Framework_Comparison
//// Flutter → full shared UI, custom rendering, great for animations.
//// KMP → share logic, native UI on each platform, high performance.
//// React Native → JS-based, native components, good for web devs. :contentReference[oaicite:2]{index=2}
//
////Kotlin multiplatform (KMP):
//// W47_KMP_Concept
//// KMP shares business logic (networking, storage, models) across platforms.
//// UI is typically platform-specific but can be shared with Compose Multiplatform. :contentReference[oaicite:3]{index=3}
//
//// W47_KMP_Setup
//// Environment: IntelliJ IDEA 2025.2.2+ or Android Studio Otter 2025.2.1+ required. :contentReference[oaicite:4]{index=4}
//
//// W47_KMP_Flexibility
//// KMP supports multiple architectures:
//// - Shared logic + native UI
//// - Shared logic + shared UI (Compose Multiplatform)
//// - Sharing only modules like DB, API, domain. :contentReference[oaicite:5]{index=5}
//
////KMP / Kotlin multiplatform Project structure:
//
//// W47_KMP_ProjectStructure
//// Key modules in a KMP project:
//// - composeApp → UI using Compose Multiplatform
//// - iosApp → iOS entry point and SwiftUI integration
//// - shared or commonMain → shared Kotlin code for all platforms. :contentReference[oaicite:6]{index=6}
//
//// W47_KMP_CommonMain
//// commonMain contains models, networking, business logic, expect declarations, shared UI (Compose Multiplatform). :contentReference[oaicite:7]{index=7}
//
////Compose multiplatform vs kotlin multiplatform:
//
//// W47_KMP_vs_ComposeMP
//// KMP shares logic only; UI per platform.
//// Compose Multiplatform shares UI across Android, iOS, Desktop, Web.
//// KMP + Compose MP = full shared logic + shared UI. :contentReference[oaicite:8]{index=8}
//
////Gradle setup (build.gradle.kts deep dive):
//
//// W47_KMP_Gradle_Plugins
//// Plugins configure:
//// - Multiplatform compiler
//// - Android application
//// - Compose Multiplatform compiler integration. :contentReference[oaicite:9]{index=9}
//
//// W47_KMP_Gradle_AndroidTarget
//// Android target setup ensures Kotlin compiles to Java 11 and uses Android SDK toolchain. :contentReference[oaicite:10]{index=10}
//
//// W47_KMP_Gradle_IOSTarget
//// iOS targets: iosX64, iosArm64, iosSimulatorArm64.
//// Output can be Kotlin/Native frameworks consumed by Xcode. :contentReference[oaicite:11]{index=11}
//
////Kotlin + swiftUL integration:
//// W47_KMP_SwiftUI_Connection
//// iOS uses SwiftUI for UI.
//// composeApp provides a ComposableUIViewController() bridging Kotlin Composables to SwiftUI. :contentReference[oaicite:12]{index=12}
//
////Expect / actual (platform-specific implementation in KMP):
//
//// W47_ExpectActual_Concept
//// expect: declares API in commonMain with no implementation.
//// actual: platform-specific implementations in androidMain, iosMain, etc. :contentReference[oaicite:13]{index=13}
//
//// W47_ExpectActual_Rules
//// Rules:
//// - Signatures must match
//// - Used for platform APIs: sensors, file I/O, location
//// - expect defines contract; actual provides implementation. :contentReference[oaicite:14]{index=14}
//
////Using shared code in native UI:
//// W47_NativeUI_UsingSharedCode
//// Android: directly call shared Kotlin code from Activities/Composable UI.
//// iOS: shared code exposed through Kotlin/Native framework imported into Swift. :contentReference[oaicite:15]{index=15}
//
////IOS basics (swiftUI + xcode):
//
//// W47_iOS_XcodeBasics
//// Xcode Project = container
//// Xcode Target = defines how the app builds for device/simulator. :contentReference[oaicite:16]{index=16}
//
//// W47_iOS_AppEntry
//// @main marks the SwiftUI entry point.
//// SwiftUI apps implement 'App' protocol returning a Scene via WindowGroup. :contentReference[oaicite:17]{index=17}
//
//// W47_iOS_StructsVsClasses
//// Swift Structs → value type, fast, no inheritance, used heavily in SwiftUI.
//// Swift Classes → reference type, use ARC, support inheritance. :contentReference[oaicite:18]{index=18}
//
//// W47_iOS_Permissions
//// iOS permissions are declared in plist (Info.plist).
//// Equivalent to AndroidManifest.xml for capability requests. :contentReference[oaicite:19]{index=19}
//
////IOS vs Android Lifecycle comparison:
//// W47_iOS_vs_Android_Lifecycle
//// Entry: @main vs Application/Activity
//// Foreground: scenePhase.active vs onResume()
//// Background: scenePhase.background vs onStop()
//// Termination: iOS system-managed (no callback) vs onDestroy(). :contentReference[oaicite:20]{index=20}
//
////jetpack compose vs swiftUI comparison:
//// W47_Compose_vs_SwiftUI
//// Compose → @Composable; SwiftUI → struct View { body: some View }
// State mgmt: remember & mutableStateOf vs @State and @Binding
// Lists: LazyColumn vs List/ForEach
// Navigation: NavHost vs NavigationStack/NavigationLink. :contentReference[oaicite:21]{index=21}