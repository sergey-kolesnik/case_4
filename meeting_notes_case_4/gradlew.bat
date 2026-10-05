@echo off
setlocal

where gradle >nul 2>&1
if %errorlevel%==0 (
  gradle %*
  exit /b %errorlevel%
)

set "PROJECT_DIR=%~dp0"
set "CACHE_DIR=%PROJECT_DIR%.gradle-local"
set "GRADLE_HOME=%CACHE_DIR%\gradle-8.10.2"
set "ARCHIVE=%CACHE_DIR%\gradle-8.10.2-bin.zip"

if not exist "%GRADLE_HOME%\bin\gradle.bat" (
  if not exist "%CACHE_DIR%" mkdir "%CACHE_DIR%"
  if not exist "%ARCHIVE%" powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-8.10.2-bin.zip' -OutFile '%ARCHIVE%'"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -Path '%ARCHIVE%' -DestinationPath '%CACHE_DIR%' -Force"
)

call "%GRADLE_HOME%\bin\gradle.bat" %*
exit /b %errorlevel%
