# NoMeLlames

Spam call blocker for Android. Automatically blocks incoming calls from configurable phone number prefixes using Android's `CallScreeningService` API.

## Features

- **Prefix-based call blocking** - Block calls from numbers starting with specific prefixes
- **Default Colombian spam prefixes** - Pre-configured with 315, 316, 317, 318
- **Call history** - View blocked calls with number, matched prefix, and timestamp
- **Configurable behavior** - Toggle hiding blocked calls from call log and notifications
- **Material You** - Dynamic theming on Android 12+ with dark mode support
- **Background service** - Works without the app being open via `CallScreeningService`

## How It Works

NoMeLlames registers as a call screening service on Android. When an incoming call arrives, the system routes it through `SpamCallScreeningService`, which:

1. Normalizes the phone number (strips country code `+57` for Colombia)
2. Checks if the number starts with any blocked prefix
3. If matched, rejects the call and optionally hides it from the call log

## Requirements

- Android 16 (API 36) or higher
- Permissions: `READ_PHONE_STATE`, `READ_CALL_LOG`
- Must be set as the default call screening app via system settings

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Database:** Room (blocked call history)
- **Storage:** SharedPreferences (prefixes and settings)
- **Architecture:** MVVM with Repository pattern
- **Navigation:** Jetpack Navigation Compose

## Building

1. Clone the repository:
   ```bash
   git clone https://github.com/hndresfelipe/NoMeLlames.git
   ```

2. Open the project in Android Studio (Ladybug or newer recommended).

3. Build and run on a device with API 36+.

## Project Structure

```
app/src/main/java/.../nomellames/
├── MainActivity.kt              # UI with 4 tabs (Home, Prefixes, History, Settings)
├── SpamCallScreeningService.kt  # Call screening logic
└── data/
    ├── AppDatabase.kt           # Room database singleton
    ├── BlockedCall.kt           # Room entity
    ├── BlockedCallDao.kt        # Data access object
    └── PrefixRepository.kt     # SharedPreferences wrapper
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
