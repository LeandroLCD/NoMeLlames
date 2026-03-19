package cl.blipblipcode.prefixsapp.ui.settings

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.blipblipcode.prefixsapp.R
import cl.blipblipcode.prefixsapp.ui.settings.components.DangerousZone
import cl.blipblipcode.prefixsapp.ui.settings.components.SettingsSectionHeader
import cl.blipblipcode.prefixsapp.ui.settings.components.SettingsToggleItem
import cl.blipblipcode.prefixsapp.ui.settings.components.dialog.DisableBiometricDialog
import cl.blipblipcode.prefixsapp.ui.settings.components.dialog.DisablePatternDialog
import cl.blipblipcode.prefixsapp.ui.settings.components.dialog.EnableBiometricDialog
import cl.blipblipcode.prefixsapp.ui.settings.components.dialog.EnablePatternDialog
import cl.blipblipcode.prefixsapp.ui.settings.components.dialog.PurgeConfirmationDialog
import cl.blipblipcode.prefixsapp.ui.settings.components.dialog.VerifyPatternDialog
import cl.blipblipcode.prefixsapp.ui.settings.state.SecurityState
import cl.blipblipcode.prefixsapp.ui.settings.state.SettingDialog
import cl.blipblipcode.prefixsapp.ui.theme.PrefixsAppTheme
import cl.blipblipcode.prefixsapp.utils.biometric.BiometricHelper

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    onError: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val securityState by viewModel.securityState.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = LocalActivity.current as? FragmentActivity

    val authTitle = stringResource(R.string.settings_auth_title)
    val authSubtitle = stringResource(R.string.settings_auth_subtitle)
    val requireSecurityMsg = stringResource(R.string.settings_require_security)
    val purgeCompletedMsg = stringResource(R.string.settings_purge_completed)
    val eventFlow by viewModel.eventFlow.collectAsStateWithLifecycle(null)

    // Handle events
    LaunchedEffect(eventFlow) {
        if (eventFlow != null) {
            when (val event = eventFlow) {
                is SettingsEvent.RequestAuth -> {
                    if (activity != null) {
                        BiometricHelper.authenticate(
                            activity = activity,
                            title = authTitle,
                            subtitle = authSubtitle,
                            onSuccess = event.onSuccess,
                            onError = { onError.invoke(it) }
                        )
                    }
                }
                is SettingsEvent.RequireSecuritySetup -> {
                    Toast.makeText(context, requireSecurityMsg, Toast.LENGTH_LONG).show()
                }
                is SettingsEvent.PurgeCompleted -> {
                    Toast.makeText(context, purgeCompletedMsg, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        when (val state = uiState) {
            is SettingsUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is SettingsUiState.Content -> {
                SettingsContent(
                    state = state,
                    securityState = securityState,
                    dialogState = dialogState,
                    onSkipCallLogChanged = viewModel::setSkipCallLog,
                    onSkipNotificationChanged = viewModel::setSkipNotification,
                    onBiometricLockChanged = viewModel::setBiometricLock,
                    onPatternLockChanged = viewModel::setPatternLock,
                    onPurgeDatabase = viewModel::onPurgeClicked
                )
            }
        }
    }
}

@Composable
private fun SettingsContent(
    state: SettingsUiState.Content,
    securityState: SecurityState,
    dialogState: SettingDialog,
    onSkipCallLogChanged: (Boolean) -> Unit,
    onSkipNotificationChanged: (Boolean) -> Unit,
    onBiometricLockChanged: (Boolean) -> Unit,
    onPatternLockChanged: (Boolean) -> Unit,
    onPurgeDatabase: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp)
        ) {
            SettingsSectionHeader(title = stringResource(R.string.settings_section_motor))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggleItem(
                title = stringResource(R.string.settings_skip_call_log_title),
                description = stringResource(R.string.settings_skip_call_log_description),
                checked = state.skipCallLog,
                onCheckedChange = onSkipCallLogChanged
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

            SettingsToggleItem(
                title = stringResource(R.string.settings_skip_notification_title),
                description = stringResource(R.string.settings_skip_notification_description),
                checked = state.skipNotification,
                onCheckedChange = onSkipNotificationChanged
            )

            Spacer(modifier = Modifier.height(32.dp))

            SettingsSectionHeader(title = stringResource(R.string.settings_section_security))

            // Only show biometric option if device has biometric hardware
            if (securityState.showBiometricOption) {
                SettingsToggleItem(
                    title = stringResource(R.string.settings_biometric_lock_title),
                    description = stringResource(R.string.settings_biometric_lock_description),
                    checked = securityState.biometricLock,
                    onCheckedChange = onBiometricLockChanged
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
            }

            SettingsToggleItem(
                title = stringResource(R.string.settings_pattern_lock_title),
                description = stringResource(R.string.settings_pattern_lock_description),
                checked = securityState.patternLock,
                onCheckedChange = onPatternLockChanged
            )

            Spacer(modifier = Modifier.height(48.dp))

            DangerousZone(
                onPurgeClick = onPurgeDatabase,
                isPurging = dialogState is SettingDialog.PurgeConfirmation && dialogState.isPurging
            )
        }

        // Dialogs based on dialogState
        when (dialogState) {
            is SettingDialog.EnableBiometric -> {
                EnableBiometricDialog(
                    onConfirm = dialogState.onConfirm,
                    onDismiss = dialogState.onDismiss
                )
            }
            is SettingDialog.DisableBiometric -> {
                DisableBiometricDialog(
                    onConfirm = dialogState.onConfirm,
                    onDismiss = dialogState.onDismiss
                )
            }
            is SettingDialog.EnablePattern -> {
                EnablePatternDialog(
                    onPatternSet = dialogState.onConfirm,
                    onDismiss = dialogState.onDismiss
                )
            }
            is SettingDialog.DisablePattern -> {
                DisablePatternDialog(
                    currentPattern = dialogState.currentPattern,
                    onPatternCorrect = dialogState.onConfirm,
                    onDismiss = dialogState.onDismiss
                )
            }
            is SettingDialog.PurgeConfirmation -> {
                PurgeConfirmationDialog(
                    isPurging = dialogState.isPurging,
                    onConfirm = dialogState.onConfirm,
                    onDismiss = dialogState.onDismiss
                )
            }
            is SettingDialog.VerifyPatternForPurge -> {
                VerifyPatternDialog(
                    currentPattern = dialogState.currentPattern,
                    onPatternCorrect = dialogState.onPatternCorrect,
                    onDismiss = dialogState.onDismiss
                )
            }
            is SettingDialog.Idle -> { /* No dialog */ }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    PrefixsAppTheme(darkTheme = true) {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            SettingsContent(
                state = SettingsUiState.Content(
                    skipCallLog = true,
                    skipNotification = true
                ),
                securityState = SecurityState(
                    biometricLock = true,
                    patternLock = false,
                    hasBiometricHardware = true,
                    hasDeviceCredential = true,
                    currentPattern = emptyList()
                ),
                dialogState = SettingDialog.Idle,
                onSkipCallLogChanged = {},
                onSkipNotificationChanged = {},
                onBiometricLockChanged = {},
                onPatternLockChanged = {},
                onPurgeDatabase = {}
            )
        }
    }
}
