# Solusi Negeri Merchant App

A comprehensive Android app for merchant authentication, transaction management, and business analytics.

## Project Structure

```
app/src/main/java/com/solusinegeri/merchant3/
├── config/
│   ├── AppConfig.kt                 # App configuration
│   └── BuildConfig.kt               # Build configuration
├── core/
│   ├── base/                        # Base classes for MVVM architecture
│   │   ├── BaseActivity.kt
│   │   ├── BaseFragment.kt
│   │   └── BaseViewModel.kt
│   ├── config/
│   │   ├── AppConfig.kt            # Core app configuration
│   │   ├── BaseActivityConfig.kt
│   │   └── BuildVariantConstants.kt # Build variant constants
│   ├── di/                         # Dependency injection (if used)
│   ├── domain/
│   │   ├── Config.kt               # Domain constants
│   │   ├── Error.kt                # Error handling
│   │   ├── Logger.kt               # Logging utility
│   │   └── Result.kt               # Result wrapper
│   ├── error/                      # Error handling components
│   ├── network/                    # Network utilities
│   ├── security/                   # Security components
│   │   ├── InputValidator.kt
│   │   ├── SecureStorage.kt
│   │   ├── SecurityChecker.kt
│   │   ├── SecurityLogger.kt
│   │   ├── SecurityManager.kt
│   │   └── TokenManager.kt
│   └── utils/                      # Core utilities
│       ├── AutoFontApplier.kt
│       ├── BackPressedHandler.kt
│       ├── BaseActivityExtensions.kt
│       ├── ChuckerHelper.kt
│       ├── DateUtils.kt
│       ├── DynamicColors.kt
│       ├── ErrorParser.kt
│       ├── FontHelper.kt
│       ├── PreferenceManager.kt
│       └── UIThemeUpdater.kt
├── data/
│   ├── interceptor/
│   │   └── AuthInterceptor.kt      # Authentication interceptor
│   ├── model/
│   │   ├── ProfileMenuItem.kt
│   │   └── UserData.kt
│   ├── network/
│   │   ├── AnalyticsApi.kt         # Analytics API endpoints
│   │   ├── AuthService.kt          # Authentication service
│   │   ├── BalanceApi.kt           # Balance management API
│   │   ├── MenuApi.kt              # Menu API
│   │   ├── NetworkClient.kt        # Retrofit setup
│   │   ├── NewsApi.kt              # News API
│   │   └── TokenManager.kt         # Token management
│   ├── repository/
│   │   ├── AnalyticsRepository.kt  # Analytics data repository
│   │   ├── AuthRepository.kt       # Authentication repository
│   │   ├── BalanceRepository.kt    # Balance repository
│   │   ├── CompanyRepository.kt    # Company repository
│   │   ├── MenuRepository.kt       # Menu repository
│   │   └── NewsInfoRepository.kt   # News repository
│   ├── requests/
│   │   └── LoginRequest.kt         # Login request models
│   └── responses/
│       ├── BalanceResponse.kt
│       ├── ErrorResponse.kt
│       ├── InitialCompanyResponse.kt
│       ├── LoginResponse.kt
│       ├── MenuListResponse.kt
│       ├── NewsDetailResponse.kt
│       ├── NewsListResponse.kt
│       ├── SummaryAnalyticsResponse.kt    # Analytics summary
│       └── TransactionAnalyticsResponse.kt # Transaction analytics
├── presentation/
│   ├── component/                  # Reusable UI components
│   │   ├── autofont/              # Auto-font components
│   │   ├── button/                # Custom button components
│   │   ├── dialog/                # Dialog components
│   │   └── loading/               # Loading components
│   ├── ui/
│   │   ├── adapters/              # RecyclerView adapters
│   │   ├── auth/                  # Authentication UI
│   │   ├── compose/               # Jetpack Compose UI
│   │   ├── initial/               # Initial setup UI
│   │   ├── main/                  # Main app UI
│   │   │   ├── fragments/         # Main fragments
│   │   │   │   ├── AnalyticsFragment.kt    # Analytics dashboard
│   │   │   │   ├── HomeFragment.kt         # Home dashboard
│   │   │   │   ├── NewsFragment.kt         # News feed
│   │   │   │   ├── ProfileFragment.kt      # User profile
│   │   │   │   └── QRScannerFragment.kt    # QR code scanner
│   │   │   └── MainActivity.kt    # Main activity
│   │   ├── menu/                  # Menu management UI
│   │   ├── settings/              # Settings UI
│   │   └── splash/                # Splash screen UI
│   └── viewmodel/
│       ├── AnalyticsViewModel.kt   # Analytics business logic
│       ├── AuthViewModel.kt        # Authentication logic
│       ├── HomeViewModel.kt        # Home logic
│       ├── NewsInfoViewModel.kt    # News logic
│       ├── ProfileViewModel.kt     # Profile logic
│       ├── QRScannerViewModel.kt   # QR scanner logic
│       └── SplashViewModel.kt      # Splash logic
├── utils/
│   ├── Constants.kt               # App constants
│   └── Extensions.kt              # Extension functions
└── MerchantApplication.kt         # Application class
```

## Features

- **Authentication & Security**
  - Secure login system with token management
  - Input validation and security checks
  - Secure storage for sensitive data
  - Security logging and monitoring

- **Business Analytics**
  - Transaction analytics dashboard
  - Summary analytics with transaction insights
  - Date range filtering for analytics data
  - Real-time data visualization

- **Transaction Management**
  - QR code scanning for transactions
  - Balance management and monitoring
  - Transaction history and tracking

- **News & Information**
  - News feed integration
  - Company information management
  - Menu management system

- **User Experience**
  - Material Design 3 UI components
  - Jetpack Compose integration
  - Auto-font components for consistent typography
  - Dynamic theming support
  - Pull-to-refresh functionality

- **Technical Features**
  - Clean MVVM architecture
  - Retrofit for networking with interceptors
  - Coroutines for asynchronous operations
  - ViewBinding and Jetpack Compose
  - Network debugging with Chucker
  - Image loading with Glide
  - Navigation Component integration

## Dependencies

### Core Android
- AndroidX Core KTX
- AndroidX AppCompat
- AndroidX Activity
- AndroidX ConstraintLayout
- AndroidX CoordinatorLayout
- AndroidX SwipeRefreshLayout

### UI Components
- Material Design Components
- Material 3 Components
- Jetpack Compose UI
- Jetpack Compose Material3
- Jetpack Compose Activity
- Jetpack Compose Navigation
- Lottie (Animations)

### Architecture & State Management
- AndroidX Lifecycle ViewModel
- AndroidX Lifecycle LiveData
- AndroidX Lifecycle Runtime
- Kotlin Coroutines Core
- Kotlin Coroutines Android

### Networking
- Retrofit
- Retrofit Gson Converter
- OkHttp
- OkHttp Logging Interceptor
- Gson
- Chucker (Network Inspector)

### Image Loading
- Glide

### Security
- AndroidX Security Crypto

### Navigation
- AndroidX Navigation Fragment KTX
- AndroidX Navigation UI KTX

### Development Tools
- Chucker (Debug builds only)
- Chucker No-op (Release builds)

## Setup

1. Clone the repository
2. Open in Android Studio
3. Sync project with Gradle files
4. Choose build variant (dev/prod) based on your needs
5. Run the app

### Build Variants

- **dev**: Development environment with debug logging enabled
  - Base URL: `https://api.stg.solusinegeri.com/`
  - Application ID suffix: `.dev`
  - Debug logging enabled

- **prod**: Production environment with optimized settings
  - Base URL: `https://api.solusinegeri.com/`
  - Debug logging disabled
  - Code minification enabled

## Usage

### Authentication
1. Launch the app
2. Enter your company ID, username, and password
3. Click "Login" to authenticate
4. Access the main dashboard upon successful login

### Main Features
1. **Home Dashboard**: View account balance and quick actions
2. **Analytics**: Access transaction analytics and business insights
3. **QR Scanner**: Scan QR codes for transactions
4. **News**: Read company news and updates
5. **Profile**: Manage account settings and logout

### Analytics Dashboard
1. Navigate to the Analytics tab
2. Select date range for analysis
3. View transaction summaries and detailed analytics
4. Filter data by balance codes

## Architecture

This app follows a clean MVVM architecture with the following layers:

### Presentation Layer
- **Activities**: UI controllers (MainActivity, LoginActivity, etc.)
- **Fragments**: Screen components (HomeFragment, AnalyticsFragment, etc.)
- **ViewModels**: Business logic and state management
- **UI Components**: Reusable UI elements (AutoFont components, SmartButton, etc.)

### Data Layer
- **Repository**: Data access abstraction (AuthRepository, AnalyticsRepository, etc.)
- **API Services**: Network communication (AuthService, AnalyticsApi, etc.)
- **Models**: Data transfer objects (requests and responses)
- **Interceptors**: Network middleware (AuthInterceptor)

### Core Layer
- **Base Classes**: Common functionality (BaseActivity, BaseFragment, BaseViewModel)
- **Security**: Security utilities and token management
- **Utils**: Helper functions and extensions
- **Domain**: Business rules and error handling

### Key Architectural Patterns
- **MVVM**: Model-View-ViewModel pattern for UI logic
- **Repository Pattern**: Data access abstraction
- **Observer Pattern**: LiveData for reactive programming
- **Builder Pattern**: Network client configuration
- **Strategy Pattern**: Security and validation strategies

### Security Features
- Secure token storage using AndroidX Security Crypto
- Input validation and sanitization
- Security logging and monitoring
- Network request interception for authentication
- Biometric authentication support (if implemented)
