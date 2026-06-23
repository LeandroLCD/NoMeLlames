package cl.blipblipcode.prefixsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import cl.blipblipcode.prefixsapp.MainActivity
import cl.blipblipcode.prefixsapp.ui.main.MainScreen
import cl.blipblipcode.prefixsapp.ui.permission.CriticalSettingScreen
import cl.blipblipcode.prefixsapp.ui.permission.PermissionScreen
import cl.blipblipcode.prefixsapp.ui.security.SecurityScreen
import cl.blipblipcode.prefixsapp.ui.splash.SplashScreen
import cl.blipblipcode.prefixsapp.utils.biometric.BiometricHelper

@Composable
fun MainActivity.NavGraph() {
    val backStack = rememberNavBackStack(Screen.Splash)
    val activity = this

    val entries = entryProvider<NavKey> {
        entry<Screen.Splash> {
            SplashScreen(
                allPermission = isCallScreeningEnabled && permissionsGranted,
                requiresAuth = biometricLockEnabled || patternLockEnabled,
                onNavigation = { screen ->
                    backStack.add(screen)
                    backStack.removeIf { it == Screen.Splash }
                },
            )

        }
        entry<Screen.Security> {
            SecurityScreen(
                showBiometric = biometricLockEnabled && BiometricHelper.canAuthenticateWithBiometrics(
                    activity
                ),
                showPattern = patternLockEnabled && storedPattern.isNotEmpty(),
                storedPattern = storedPattern,
                onAuthSuccess = {
                    if (permissionsGranted && isCallScreeningEnabled) {
                        backStack.add(Screen.Main(0))
                    } else {
                        backStack.add(Screen.CriticalSetting)
                    }
                    backStack.removeIf { it == Screen.Security }
                },
                onCancel = { activity.finishAffinity() }
            )
        }
        entry<Screen.Main> { entry ->
            MainScreen(
                index = entry.index,
                isCallScreeningEnabled = isCallScreeningEnabled,
                permissionsGranted = permissionsGranted,
                supportsRoleRequest = supportsCallScreeningRole,
                onRequestPermissions = { screen ->
                    backStack.add(screen)
                },
                onDisableRole = {
                    if (!supportsCallScreeningRole) {
                        activity.persistLegacyScreeningConfigured(false)
                        activity.checkCallScreeningRole()
                    }
                    activity.openDefaultAppsSettings()
                }
            )
        }
        entry<Screen.CriticalSetting> {
            CriticalSettingScreen(
                onBackStack = {
                    backStack.removeLastOrNull()
                },
                onConfirm = {
                    activity.requestCallScreeningRole()
                    backStack.removeIf { it == Screen.CriticalSetting }
                    backStack.add(Screen.Permission)
                }
            )
        }
        entry<Screen.Permission> {
            PermissionScreen(
                onPermissionGranted = {
                    activity.requestPermissions()
                    backStack.clear()
                    backStack.add(Screen.Main(0))
                },
                onPermissionDenied = {
                    backStack.removeLastOrNull()
                }
            )
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entries,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        onBack = { backStack.removeLastOrNull() }
    )
}