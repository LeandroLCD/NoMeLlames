# Save Number In Contact — Design

**Date:** 2026-06-21
**Status:** Draft — pending user review
**Platform:** Android

## Overview

Add a "save to contacts" action on each item in the History listing. When the user taps the action, the app opens the system's default contacts app with a new-contact screen pre-filled with the phone number. The user is then responsible for naming and saving the contact inside the contacts app.

If the user has not granted `READ_CONTACTS`, the app first navigates to the existing `PermissionScreen`. Once the user grants the permission and returns to the History screen, the stashed number is saved automatically — no second tap required.

## Goals & Non-Goals

**Goals**
- One-tap access to "save this number" from every history item (blocked or allowed).
- Respect the existing permission flow — never silently fail because of missing permission.
- Survive the navigation to/from the Permission screen and configuration changes.

**Non-Goals**
- We do **not** write to the contacts provider directly. The system contacts app handles persistence via an `Intent`. This means we do not need `WRITE_CONTACTS` in the runtime permission flow (only `READ_CONTACTS` is checked, because it indicates the user has set up contacts at all).
- No new icons. Use `Icons.Outlined.PersonAdd` from `androidx.compose.material.icons.outlined`.
- No new permission types. We re-use the existing `READ_CONTACTS` request flow.

## Architecture

The flow lives entirely in the `HistoryScreen` composable. No new UseCase, no new ViewModel logic, no new Repository method. The existing `MainActivity.permissionsGranted: MutableState<Boolean>` is the source of truth for the permission state and is already plumbed into `MainScreen`.

```
┌─────────────────────┐     permissionsGranted, onRequestPermissions
│   MainScreen        │ ─────────────────────────────────────────────────┐
└─────────────────────┘                                                  │
         │ tab == HISTORY                                               │
         ▼                                                             │
┌─────────────────────┐                                                  │
│   HistoryScreen     │  ◄─── permissionsGranted                          │
│                     │  ◄─── onRequestPermissions(Screen)                │
│  • pendingSaveNumber│                                                  │
│  • LaunchedEffect   │  callback when permission flips to true          │
│  • launchSaveIntent │  Result<Unit> wrapper for error handling          │
└─────────────────────┘                                                  │
         │ onSaveClick(phoneNumber)                                      │
         ▼                                                             │
┌─────────────────────┐                                                  │
│   HistoryItem       │  • always-visible IconButton(Icons.PersonAdd)    │
│  (sub-composable)   │    on the right of the status badge              │
└─────────────────────┘                                                  │
                                                                         │
        MainActivity.permissionsGranted ─────────────────────────────────┘
        (already in place; updated by permissionLauncher)
```

### Why the screen, not the ViewModel

The "save to contacts" action is a pure UI side effect: launch an `Intent` (which needs a `Context`) and optionally navigate (which needs the NavController / `onRequestPermissions` callback). There is no business state to own, no domain logic to test in isolation. Putting this in the ViewModel would force it to take a `Context` or emit a `Navigation`/`Intent` event — both are code smells in this codebase (see how the existing export CSV flow keeps the file path emission in the ViewModel but the `Intent` launch in the `Screen`).

The `HistoryViewModel` is untouched. The only permission check happens via the `permissionsGranted` flag plumbed from `MainActivity`.

## Data Flow

### Happy path — permission already granted

```
User taps PersonAdd icon
  └─> HistoryItem calls onSaveClick(phoneNumber)
        └─> HistoryScreen: permissionsGranted == true
              └─> launchSaveContactIntent(context, phoneNumber) returns Result.success
                    └─> context.startActivity(insertIntent)
                          └─> system contacts app opens with phone pre-filled
```

### Pending path — permission not granted

```
User taps PersonAdd icon
  └─> HistoryItem calls onSaveClick(phoneNumber)
        └─> HistoryScreen: permissionsGranted == false
              └─> pendingSaveNumber = phoneNumber   (rememberSaveable, survives nav)
              └─> onRequestPermissions(Screen.Permission)
                    └─> NavGraph navigates to PermissionScreen
                          └─> User taps "Conceder acceso"
                                └─> activity.requestPermissions() → permissionLauncher
                                      └─> MainActivity.permissionsGranted = true
                                            └─> NavGraph returns to Main(0) (History tab)
                                                  └─> HistoryScreen recomposes
                                                        └─> LaunchedEffect(permissionsGranted, pendingSaveNumber) fires
                                                              └─> pendingSaveNumber != null && permissionsGranted == true
                                                                    └─> launchSaveContactIntent(...) → startActivity
                                                                          └─> pendingSaveNumber = null
```

### Error path — no contacts app installed

```
launchSaveContactIntent → Result.failure(ActivityNotFoundException)
  └─> scope.launch { snackbarHostState.showSnackbar(saveErrorLabel) }
        └─> user sees snackbar, can retry
```

## Components

### `HistoryScreen.kt` (modified)

New parameters on the public `HistoryScreen`:

```kotlin
@Composable
fun HistoryScreen(
    permissionsGranted: Boolean,
    onRequestPermissions: (Screen) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
)
```

New local state + effect:

```kotlin
var pendingSaveNumber by rememberSaveable { mutableStateOf<String?>(null) }

LaunchedEffect(permissionsGranted, pendingSaveNumber) {
    if (permissionsGranted && pendingSaveNumber != null) {
        val number = pendingSaveNumber!!
        pendingSaveNumber = null
        launchSaveContactIntent(context, number).onFailure {
            scope.launch { snackbarHostState.showSnackbar(saveErrorLabel) }
        }
    }
}
```

The `onSaveClick` callback is built once and passed down through `HistoryContentContainer` → `HistoryContent` → `HistoryItem`:

```kotlin
val onSaveClick: (String) -> Unit = { phoneNumber ->
    if (permissionsGranted) {
        launchSaveContactIntent(context, phoneNumber).onFailure {
            scope.launch { snackbarHostState.showSnackbar(saveErrorLabel) }
        }
    } else {
        pendingSaveNumber = phoneNumber
        onRequestPermissions(Screen.Permission)
    }
}
```

The `runCatching` wrapper unifies the happy and error paths.

New top-level helper (testable in isolation):

```kotlin
internal fun launchSaveContactIntent(
    context: Context,
    phoneNumber: String
): Result<Unit> = runCatching {
    val intent = Intent(Intent.ACTION_INSERT).apply {
        data = ContactsContract.Contacts.CONTENT_URI
        putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}
```

### `HistoryItem` (modified)

The expanded-row copy icon stays. A new **always-visible** `IconButton` is added at the right of the main row, next to the status badge:

```kotlin
Row(verticalAlignment = Alignment.CenterVertically) {
    Text(statusText, ...)
    Spacer(Modifier.width(6.dp))
    Box(Modifier.size(8.dp).background(statusColor))
}
IconButton(
    onClick = { onSaveClick(item.phoneNumber) },
    modifier = Modifier.size(40.dp)
) {
    Icon(
        imageVector = Icons.Outlined.PersonAdd,
        contentDescription = stringResource(R.string.history_save_contact),
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(20.dp)
    )
}
```

`HistoryItem`'s signature gains:

```kotlin
private fun HistoryItem(
    item: HistoryItem,
    dateFormat: SimpleDateFormat,
    onPhoneNumberClick: (String) -> Unit,   // existing (copy)
    onSaveClick: (String) -> Unit          // new
)
```

### `MainScreen.kt` (modified)

One line change — pass the existing flags down:

```kotlin
TabIds.HISTORY -> HistoryScreen(
    permissionsGranted = permissionsGranted,
    onRequestPermissions = onRequestPermissions
)
```

`permissionsGranted` and `onRequestPermissions` are already in `MainScreen`'s parameter list. No new wiring.

## Files Changed

| File | Change |
|---|---|
| `app/src/main/java/.../ui/history/HistoryScreen.kt` | Add icon, state, `LaunchedEffect`, intent helper, plumb `onSaveClick` through the 3 levels |
| `app/src/main/java/.../ui/main/MainScreen.kt` | Pass `permissionsGranted` + `onRequestPermissions` to `HistoryScreen` |
| `app/src/main/res/values/strings.xml` | `history_save_contact`, `history_save_contact_error` |
| `app/src/main/res/values-en/strings.xml` | same |
| `app/src/main/res/values-pt/strings.xml` | same |
| `app/src/main/res/values-it/strings.xml` | same |
| `app/src/main/res/values-fr/strings.xml` | same |

**Untouched** (intentionally): `HistoryViewModel.kt`, `ContactsRepository.kt`, `MainActivity.kt`, `NavGraph.kt`, `PermissionScreen.kt`, `AndroidManifest.xml`.

## Strings

| Key | Spanish (default) | English |
|---|---|---|
| `history_save_contact` | `Guardar en contactos` | `Save to contacts` |
| `history_save_contact_error` | `No se pudo abrir la app de contactos` | `Could not open contacts app` |

These need to be added in the 5 existing locale folders (`values/`, `values-en/`, `values-pt/`, `values-it/`, `values-fr/`). For `pt`, `it`, `fr`, I'll provide best-effort translations; the user can correct them.

## Error Handling

| Failure | Response |
|---|---|
| `READ_CONTACTS` not granted | Stash number, navigate to `PermissionScreen`. On return + grant, fire `LaunchedEffect` → `startActivity` |
| `READ_CONTACTS` permanently denied (Don't ask again) | The existing `MainActivity` already routes to `openAppSettings()` when `shouldShowRequestPermissionRationale` is false. No new code. |
| No contacts app installed | `ActivityNotFoundException` caught by `runCatching` in `launchSaveContactIntent` → snackbar |
| User cancels the contacts app's "new contact" screen | Out of our control. Number is lost — this is the system's default behavior. Acceptable. |
| Process death during navigation | `pendingSaveNumber` is `rememberSaveable` so it survives process death. On relaunch, if the user is back on History with permission granted, the `LaunchedEffect` fires. If the user is on a different tab, the `LaunchedEffect` does not run (it only runs in `HistoryScreen`'s composition). Acceptable edge case. |

## Testing Strategy

Following the project's TDD convention (`mobiai-mobile-tdd` skill, already used in the data layer tests):

1. **Unit test for `launchSaveContactIntent`** (instrumented, since `Intent` is Android):
   - `should_set_action_insert_and_uri_and_phone_extra_when_phone_provided_in_invoke`
   - `should_add_flag_activity_new_task_in_invoke`
   - `should_return_failure_when_context_throws_activity_not_found_in_invoke`
   - Lives in `app/src/androidTest/.../ui/history/HistoryScreenIntentTest.kt`

2. **The permission flow itself is not unit-tested.** The screen-level orchestration (`pendingSaveNumber`, `LaunchedEffect`, snackbar on error) is straightforward Compose state machinery. The existing permission flow is already exercised end-to-end by manual smoke testing. Instrumented tests for this would require mocking the activity result contract, which is brittle.

3. **Manual smoke test checklist** (to be performed by the user before merge):
   - [ ] Tap PersonAdd with permission granted → contacts app opens
   - [ ] Tap PersonAdd without permission → navigates to `PermissionScreen`
   - [ ] Grant permission → returns to History, contacts app opens automatically
   - [ ] Deny permission with "Don't ask again" → routes to app settings (existing behavior)
   - [ ] Disable contacts app → snackbar shows

## Edge Cases & Open Questions

- **Emoji or special characters in phone number**: `Intent` extras handle UTF-16 strings fine. No sanitization needed.
- **Phone number formatting**: We pass the raw `item.phoneNumber` as-is. The system contacts app handles formatting.
- **Multiple save intents in rapid succession**: `pendingSaveNumber` is a single value, so the second tap overrides the first. The `LaunchedEffect` will fire with the most recent number. This is acceptable — the contacts app is robust to repeated `ACTION_INSERT` calls.
- **i18n for the snackbar message**: All 5 locales need `history_save_contact_error`. I will provide best-effort translations for `pt`, `it`, `fr` and ask the user to verify.

## Out of Scope (future work)

- Show a confirmation snackbar after a successful save (we don't know if the user actually saved in the contacts app).
- Allow editing the phone number before saving.
- Save multiple numbers at once.
