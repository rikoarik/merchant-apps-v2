# Solusi Negeri Merchant App

A simple Android app for merchant authentication and management.

## Project Structure

```
app/src/main/java/com/solusinegeri/merchant3/
├── config/
│   └── AppConfig.kt                 # Simple app configuration
├── data/
│   ├── ApiService.kt               # API service interface and models
│   ├── NetworkClient.kt            # Retrofit setup
│   └── AuthRepository.kt           # Authentication repository
├── ui/
│   ├── MainViewModel.kt            # Main view model
│   └── LoginActivity.kt            # Login activity
├── core/
│   ├── config/
│   │   ├── AppConfig.kt            # Core app configuration
│   │   └── BuildVariantConstants.kt # Build variant constants
│   └── domain/
│       ├── Config.kt               # Simple constants
│       ├── Error.kt                # Error handling
│       ├── Logger.kt               # Logging utility
│       └── Result.kt               # Result wrapper
├── presentation/
│   └── ui/
│       └── UiState.kt              # UI state definitions
├── utils/
│   ├── Constants.kt                # App constants
│   └── Extensions.kt               # Extension functions
├── MainActivity.kt                 # Main activity
└── MerchantApplication.kt          # Application class
```

## Features

- Simple authentication system
- Clean architecture without complex dependency injection
- Material Design UI
- Retrofit for networking
- ViewBinding for UI
- MVVM pattern with LiveData

## Dependencies

- AndroidX Core
- Material Design Components
- Retrofit & OkHttp
- Kotlin Coroutines
- ViewBinding

## Setup

1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Run the app

## Usage

1. Launch the app
2. Click "Login" to go to login screen
3. Enter company ID, username, and password
4. Click "Login" to authenticate
5. Use "Logout" to sign out

## Architecture

This app follows a simplified clean architecture:

- **UI Layer**: Activities and ViewModels
- **Data Layer**: Repository and API service
- **Domain Layer**: Core business logic and models
- **Config Layer**: App configuration and constants

No complex dependency injection frameworks are used - everything is kept simple and straightforward.
