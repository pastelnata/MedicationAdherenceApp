# Clean Lint Cache and Rebuild
# This script cleans all caches and runs lint fresh

Write-Host "Cleaning Gradle build cache..." -ForegroundColor Yellow
Remove-Item -Recurse -Force -ErrorAction SilentlyContinue ".\app\build"
Remove-Item -Recurse -Force -ErrorAction SilentlyContinue ".\build"
Remove-Item -Recurse -Force -ErrorAction SilentlyContinue ".\.gradle"

Write-Host "`nRunning clean build..." -ForegroundColor Yellow
.\gradlew clean --no-daemon

Write-Host "`nRunning lint without cache..." -ForegroundColor Yellow
.\gradlew lint --no-daemon --no-build-cache --rerun-tasks

Write-Host "`nDone!" -ForegroundColor Green

