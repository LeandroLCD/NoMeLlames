# AGENTS.md - PreFixApp

## Project Overview
Android spam call blocker app using `CallScreeningService` API. Blocks incoming calls from configurable phone prefixes with BLOCK/ALLOW rules. Colombian market focus (defaults to +57 prefixes).

## Architecture

### Clean Architecture Layers
```
ui/          → Jetpack Compose screens + ViewModels (@HiltViewModel)
domain/      → Interfaces (IUseCase), models, repository contracts
data/        → Room entities, DAOs, repository implementations
di/          → Hilt modules (DataModule, RepositoryModule, UseCaseModule, DispatcherModule)
utils/       → Constants (AppConstants), biometric auth (BiometricHelper)
```

### Key Components
- **SpamCallPrefixService** - Core service handling call screening via Android's CallScreeningService API
- **MainActivity** - Single activity hosting Navigation3 with screens: Splash, Main (4 tabs), Permission, CriticalSetting, Security
- **PrefixsApp** - Hilt application entry point

### Data Flow
1. `SpamCallPrefixService.onScreenCall()` receives incoming calls
2. Loads `PrefixRule` list, matches by longest prefix wins (BLOCK vs ALLOW)
3. Blocked calls saved via `BlockedCallRepository`, allowed via `AllowedCallRepository`
4. UI observes changes through `Flow<List<T>>` from DAOs

## Conventions

### UseCase Pattern
Every use case has interface + implementation pair:

```kotlin
// Interface: IAddPrefixRuleUseCase.kt
interface IAddPrefixRuleUseCase {
    suspend operator fun invoke(prefix: String, ruleType: PrefixRule.RuleType): Result<Unit>
}

// Implementation: AddPrefixRuleUseCase.kt
class AddPrefixRuleUseCase @Inject constructor(
    private val prefixRepository: PrefixRepository
) : IAddPrefixRuleUseCase {
    override suspend operator fun invoke(prefix: String, ruleType: PrefixRule.RuleType): Result<Unit> {
        
    }}
```

### Repository Pattern
- Interfaces in `domain/repositories/`
- Implementations in `data/repository/` extending `BaseRepository`
- Use `makeSuspendCall()` for coroutine context switching

### Room Database
- Entities in `data/local/entities/` with `Entity` suffix
- DAOs in `data/local/dao/` with `Dao` suffix
- Migrations defined in `DataModule.kt` (current version: 3)
- Static data in `data/local/static/` (e.g., `CountryDialingCodeProvider`)

### Firebase Integration
- **Remote Config** - Version checking via `VersionRepository` with `version_config` key
- **Crashlytics** - Native symbol upload enabled for release builds

### UI Conventions
- Screens are stateless composables receiving state + callbacks
- ViewModels expose `StateFlow<UiState>` (Loading/Content/Error pattern)
- Theme colors defined in `ui/theme/Color.kt` (CyanAccent, DarkBg, BlockedRed, etc.)
- Custom bottom bar component in `:specialbottombar` module

## Build & Run

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Build release (requires signing config)
./gradlew assembleRelease
```

**Requirements:** Android Studio Ladybug+, API 24+ device (target API 36)

## Module Structure
- `:app` - Main application module
- `:specialbottombar` - Reusable custom bottom navigation component

## Key Files Reference
| Purpose                  | Location                                         |
|--------------------------|--------------------------------------------------|
| Call screening logic     | `SpamCallPrefixService.kt`                       |
| DI setup                 | `di/DataModule.kt`, `di/RepositoryModule.kt`, `di/DispatcherModule.kt` |
| Database schema          | `data/local/database/AppDatabase.kt`             |
| Theme/colors             | `ui/theme/Color.kt`, `ui/theme/Theme.kt`         |
| String resources         | `res/values/strings.xml` (Spanish)               |
| Navigation screens       | `ui/navigation/Screen.kt`                        |
| Biometric authentication | `utils/biometric/BiometricHelper.kt`             |
| App constants            | `utils/AppConstants.kt`                          |

