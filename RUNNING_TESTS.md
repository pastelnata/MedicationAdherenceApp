# Running Tests - Quick Start Guide

## Prerequisites

Before running tests, ensure dependencies are synced:

### In Android Studio
1. Click **File** → **Sync Project with Gradle Files**
2. Wait for sync to complete
3. Build the project: **Build** → **Make Project**

### From Command Line (PowerShell)
```powershell
cd C:\Users\habib\StudioProjects\MedicationAdherenceApp
.\gradlew build -x test
```

## Running Unit Tests

Unit tests are fast and don't require a device/emulator.

### Option 1: Android Studio (Recommended)
1. Open **Project** view (Alt+1)
2. Navigate to `app/src/test/java`
3. Right-click on the `test` folder
4. Select **Run 'Tests in 'test''**

### Option 2: Run Specific Test Class
1. Open a test file (e.g., `LoginViewModelTest.kt`)
2. Click the green ▶️ icon next to the class name
3. Or right-click in the editor → **Run 'LoginViewModelTest'**

### Option 3: Command Line
```powershell
# Run all unit tests
.\gradlew test

# Run specific test class
.\gradlew test --tests "com.example.medicationadherenceapp.ui.viewmodel.LoginViewModelTest"

# Run with detailed output
.\gradlew test --info
```

## Running UI/Instrumented Tests

UI tests require a connected Android device or emulator.

### Step 1: Start Emulator
1. Open **Device Manager** (Tools → Device Manager)
2. Start an emulator (or connect a physical device)
3. Wait for device to boot completely

### Step 2: Run Tests

#### Option A: Android Studio
1. Navigate to `app/src/androidTest/java`
2. Right-click on `androidTest` folder
3. Select **Run 'Tests in 'androidTest''**

#### Option B: Command Line
```powershell
# Run all instrumented tests
.\gradlew connectedAndroidTest

# Run specific test
.\gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.medicationadherenceapp.ui.components.login.LoginPageTest
```

## Test Files Overview

### Unit Tests (`app/src/test/`)
```
test/
└── com/example/medicationadherenceapp/
    ├── ui/viewmodel/
    │   ├── LoginViewModelTest.kt           ✅ Tests login logic
    │   ├── DashboardViewModelTest.kt       ✅ Tests medication status
    │   └── ProgressViewModelTest.kt        ✅ Tests progress selection
    ├── repository/
    │   └── MedicationRepositoryTest.kt     ✅ Tests data layer
    └── test/
        └── TestDataFactory.kt              🔧 Test utilities
```

### UI Tests (`app/src/androidTest/`)
```
androidTest/
└── com/example/medicationadherenceapp/
    └── ui/components/
        ├── login/
        │   └── LoginPageTest.kt            ✅ Tests login UI
        └── dashboard/
            └── DashboardComponentsTest.kt  ✅ Tests dashboard UI
```

## Expected Results

### All Tests Passing
```
✓ LoginViewModelTest (11 tests) - PASSED
✓ DashboardViewModelTest (9 tests) - PASSED
✓ ProgressViewModelTest (8 tests) - PASSED
✓ MedicationRepositoryTest (13 tests) - PASSED
✓ LoginPageTest (10 tests) - PASSED
✓ DashboardComponentsTest (9 tests) - PASSED

Total: 60 tests - ALL PASSED ✅
```

## Troubleshooting

### Issue: "Unresolved reference" errors in tests

**Solution:**
1. Sync Gradle dependencies:
   ```powershell
   .\gradlew --refresh-dependencies
   ```
2. In Android Studio: **File** → **Invalidate Caches** → Restart
3. Rebuild project: **Build** → **Rebuild Project**

### Issue: Tests won't run

**Solution:**
1. Check that test dependencies are downloaded:
   ```powershell
   .\gradlew dependencies --configuration testRuntimeClasspath
   ```
2. Clean and rebuild:
   ```powershell
   .\gradlew clean build -x test
   ```

### Issue: Instrumented tests can't find device

**Solution:**
1. Verify device is connected:
   ```powershell
   adb devices
   ```
2. If emulator isn't listed, restart it
3. Ensure USB debugging is enabled (physical device)

### Issue: Compose tests fail with "No compose hierarchies found"

**Solution:**
1. Add to `build.gradle.kts`:
   ```kotlin
   debugImplementation("androidx.compose.ui:ui-test-manifest")
   ```
2. Sync and rebuild

## Viewing Test Results

### Android Studio
- Results appear in **Run** tool window
- Green ✓ = passed, Red ✗ = failed
- Click test name to see details
- Click stack trace to jump to code

### HTML Report
After running from command line:
```powershell
.\gradlew test

# Open report
start app\build\reports\tests\testDebugUnitTest\index.html
```

## Code Coverage

### Generate Coverage Report
```powershell
.\gradlew testDebugUnitTest jacocoTestReport
```

### View Coverage in Android Studio
1. Run tests with coverage: **Run** → **Run 'Tests' with Coverage**
2. Coverage tool window shows % for each class/method
3. Editor shows covered (green) and uncovered (red) lines

## Quick Reference

| Action | Command |
|--------|---------|
| Run all unit tests | `.\gradlew test` |
| Run all UI tests | `.\gradlew connectedAndroidTest` |
| Run specific test | `.\gradlew test --tests "ClassName"` |
| Clean build | `.\gradlew clean` |
| Rebuild | `.\gradlew build -x test` |
| Sync dependencies | `.\gradlew --refresh-dependencies` |
| View test report | Open `app/build/reports/tests/` |

## CI/CD Integration

Tests can be automated in CI/CD pipelines:

```yaml
# Example GitHub Actions workflow
- name: Run unit tests
  run: ./gradlew test

- name: Run instrumented tests
  uses: reactivecircus/android-emulator-runner@v2
  with:
    api-level: 29
    script: ./gradlew connectedAndroidTest
```

## Need Help?

- See [TESTING_GUIDE.md](TESTING_GUIDE.md) for detailed information
- Check [Android Testing Documentation](https://developer.android.com/training/testing)
- Review test files for examples

---

**Remember:** Tests must pass before merging code! 🚀

