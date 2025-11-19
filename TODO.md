# Project Checklist

## App Structure --> DONE
- Create navigation graph with multiple screens
- Implement screen routes with argument passing
- Add a ViewModel for each screen
- Expose UI state from each ViewModel
- Apply unidirectional data flow across UI and ViewModel

## UI (Jetpack Compose)
- Build screens using composable functions
- Hoist state to ViewModels when needed
- Use remember and rememberSaveable correctly
- Add a consistent Material theme
- Implement interactive UI elements (buttons, inputs, lists)
- Use LazyColumn or LazyRow for dynamic lists
- Break UI into reusable composables

## Data Layer --> DONE
- Set up Room database
- Create entities, DAOs and queries
- Add a repository that connects Room and network
- Expose Flow or LiveData from Room
- Add DataStore for preferences

## Networking --> DONE
- Add Retrofit service interface ✅
- Create suspend functions for API calls ✅
- Parse JSON responses ✅
- Handle network errors ✅
- Cache network results locally when needed ✅

## Architecture
- Follow MVVM pattern
- Keep UI logic out of composables
- Keep data logic in repository layer
- Represent loading, success and error states

## Permissions
- Request runtime permissions when required
- Add rationale dialog for denied permissions
- Handle "never ask again" cases

## Sensors --> DONE
- Access sensors through SensorManager ✅
- Register sensor listeners in correct lifecycle events ✅
- Unregister sensor listeners properly ✅
- Use sensor data in UI or logic ✅

## Background Work
- Add WorkManager to schedule tasks
- Create workers with required constraints
- Observe worker results from UI

## Notifications
- Create notification channels
- Build notifications with actions
- Add PendingIntent to open screens when tapped
- Trigger notifications from app or background work

## Accessibility
- Add content descriptions to visuals
- Ensure touch targets are large enough
- Support TalkBack navigation

## Testing --> DONE
- Add ViewModel unit tests ✅
- Add Compose UI tests ✅
- Mock data sources in tests ✅

## Performance & Lifecycle
- Use ViewModel to survive configuration changes
- Avoid holding activity references in long-lived objects
- Keep composables lightweight and recomposition-friendly

