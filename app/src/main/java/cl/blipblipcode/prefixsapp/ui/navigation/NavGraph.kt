package cl.blipblipcode.prefixsapp.ui.navigation

import androidx.activity.compose.LocalActivity
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
import cl.blipblipcode.prefixsapp.utils.biometric.BiometricHelper

@Composable
fun NavGraph(
    biometricLockEnabled: Boolean,
    patternLockEnabled: Boolean,
    storedPattern: List<Int>,
    isCallScreeningEnabled: Boolean,
    permissionsGranted: Boolean,
    supportsCallScreeningRole: Boolean,
    onAuthSuccess: (Boolean) -> Unit
) {
    val backStack = rememberNavBackStack(Screen.Security)

    val entries = entryProvider<NavKey> {
        entry<Screen.Security> {
            val activity = LocalActivity.current as MainActivity
            SecurityScreen(
                showBiometric = biometricLockEnabled && BiometricHelper.canAuthenticateWithBiometrics(
                    activity
                ),
                showPattern = patternLockEnabled && storedPattern.isNotEmpty(),
                storedPattern = storedPattern,
                onAuthSuccess = {
                    onAuthSuccess(true)
                    backStack.add(Screen.Main)
                    backStack.removeIf { it != Screen.Main }
                },
                onCancel = { activity.finishAffinity() }
            )
        }
        entry<Screen.Main> {
            MainScreen(
                isEnabled = isCallScreeningEnabled,
                permissionsGranted = permissionsGranted,
                supportsRoleRequest = supportsCallScreeningRole,
                onRequestPermissions = { requestPermissions() },
                onDisableRole = {
                    if (!supportsCallScreeningRole) {
                        persistLegacyScreeningConfigured(false)
                        checkCallScreeningRole()
                    }
                    openDefaultAppsSettings()
                }
            )
        }
        entry<Screen.CriticalSetting> {
            CriticalSettingScreen(
                onBackStack = {
                    backStack.removeLastOrNull()
                },
                onConfirm = {
                    backStack.removeLastOrNull()
                }
            )
        }
        entry<Screen.Permission> {
            PermissionScreen(
                onPermissionGranted = {
                    backStack.add(Screen.Main)
                    backStack.removeIf { it == Screen.Permission }
                },
                onPermissionDenied = {
                    backStack.add(Screen.Main)
                    backStack.removeIf { it == Screen.Permission }
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
        onBack = { backStack.removeLastOrNull() })

}