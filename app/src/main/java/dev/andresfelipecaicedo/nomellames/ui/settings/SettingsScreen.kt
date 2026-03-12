package dev.andresfelipecaicedo.nomellames.ui.settings

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.andresfelipecaicedo.nomellames.R
import dev.andresfelipecaicedo.nomellames.ui.security.DisableBiometricDialog
import dev.andresfelipecaicedo.nomellames.ui.security.DisablePatternDialog
import dev.andresfelipecaicedo.nomellames.ui.security.EnableBiometricDialog
import dev.andresfelipecaicedo.nomellames.ui.security.EnablePatternDialog
import dev.andresfelipecaicedo.nomellames.ui.theme.AllowedCyan
import dev.andresfelipecaicedo.nomellames.ui.theme.BlockedRed
import dev.andresfelipecaicedo.nomellames.ui.theme.CyanAccent
import dev.andresfelipecaicedo.nomellames.ui.theme.DarkBg
import dev.andresfelipecaicedo.nomellames.ui.theme.DividerColor
import dev.andresfelipecaicedo.nomellames.ui.theme.NoMeLlamesTheme
import dev.andresfelipecaicedo.nomellames.ui.theme.TextGray
import dev.andresfelipecaicedo.nomellames.utils.biometric.BiometricHelper

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = LocalActivity.current

    val authTitle = stringResource(R.string.settings_auth_title)
    val authSubtitle = stringResource(R.string.settings_auth_subtitle)
    val requireSecurityMsg = stringResource(R.string.settings_require_security)
    val purgeCompletedMsg = stringResource(R.string.settings_purge_completed)
    val eventFlow by viewModel.eventFlow.collectAsStateWithLifecycle(null)

    // Handle events
    LaunchedEffect(eventFlow) {
        if(eventFlow != null){
            when (val event = eventFlow) {
                is SettingsEvent.RequestAuth -> {
                    if (activity != null) {
                        BiometricHelper.authenticate(
                            activity = activity as FragmentActivity,
                            title = authTitle,
                            subtitle = authSubtitle,
                            onSuccess = event.onSuccess,
                            onError = { /* User cancelled or error - do nothing */ }
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBg,
        topBar = {
            SettingsTopBar()
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is SettingsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyanAccent)
                    }
                }
                is SettingsUiState.Content -> {
                    SettingsContent(
                        state = state,
                        onSkipCallLogChanged = viewModel::setSkipCallLog,
                        onSkipNotificationChanged = viewModel::setSkipNotification,
                        onBiometricLockChanged = viewModel::setBiometricLock,
                        onPatternLockChanged = viewModel::setPatternLock,
                        onPurgeDatabase = viewModel::onPurgeClicked,
                        onConfirmPurge = viewModel::confirmPurge,
                        onDismissPurge = viewModel::dismissPurgeDialog,
                        onConfirmEnableBiometric = viewModel::onConfirmEnableBiometric,
                        onDismissEnableBiometricDialog = viewModel::onDismissEnableBiometricDialog,
                        onConfirmDisableBiometric = viewModel::onConfirmDisableBiometric,
                        onDismissDisableBiometricDialog = viewModel::onDismissDisableBiometricDialog,
                        onPatternSet = viewModel::onPatternSet,
                        onDismissEnablePatternDialog = viewModel::onDismissEnablePatternDialog,
                        onPatternCorrectForDisable = viewModel::onPatternCorrectForDisable,
                        onDismissDisablePatternDialog = viewModel::onDismissDisablePatternDialog
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsTopBar() {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Handle back */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_arrow),
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = CyanAccent,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(48.dp))
        }
        HorizontalDivider(color = DividerColor, thickness = 1.dp)
    }
}

@Composable
private fun SettingsContent(
    state: SettingsUiState.Content,
    onSkipCallLogChanged: (Boolean) -> Unit,
    onSkipNotificationChanged: (Boolean) -> Unit,
    onBiometricLockChanged: (Boolean) -> Unit,
    onPatternLockChanged: (Boolean) -> Unit,
    onPurgeDatabase: () -> Unit,
    onConfirmPurge: () -> Unit,
    onDismissPurge: () -> Unit,
    onConfirmEnableBiometric: () -> Unit,
    onDismissEnableBiometricDialog: () -> Unit,
    onConfirmDisableBiometric: () -> Unit,
    onDismissDisableBiometricDialog: () -> Unit,
    onPatternSet: (List<Int>) -> Unit,
    onDismissEnablePatternDialog: () -> Unit,
    onPatternCorrectForDisable: () -> Unit,
    onDismissDisablePatternDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Purge confirmation dialog
    if (state.showPurgeDialog) {
        PurgeConfirmationDialog(
            isPurging = state.isPurging,
            onConfirm = onConfirmPurge,
            onDismiss = onDismissPurge
        )
    }

    // Security dialogs
    if (state.showEnableBiometricDialog) {
        EnableBiometricDialog(
            onConfirm = onConfirmEnableBiometric,
            onDismiss = onDismissEnableBiometricDialog
        )
    }

    if (state.showDisableBiometricDialog) {
        DisableBiometricDialog(
            onConfirm = onConfirmDisableBiometric,
            onDismiss = onDismissDisableBiometricDialog
        )
    }

    if (state.showEnablePatternDialog) {
        EnablePatternDialog(
            onPatternSet = onPatternSet,
            onDismiss = onDismissEnablePatternDialog
        )
    }

    if (state.showDisablePatternDialog) {
        DisablePatternDialog(
            currentPattern = state.currentPattern,
            onPatternCorrect = onPatternCorrectForDisable,
            onDismiss = onDismissDisablePatternDialog
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
    ) {
        SettingsSectionHeader(title = stringResource(R.string.settings_section_motor))

        HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

        SettingsToggleItem(
            title = stringResource(R.string.settings_skip_call_log_title),
            description = stringResource(R.string.settings_skip_call_log_description),
            checked = state.skipCallLog,
            onCheckedChange = onSkipCallLogChanged
        )

        HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

        SettingsToggleItem(
            title = stringResource(R.string.settings_skip_notification_title),
            description = stringResource(R.string.settings_skip_notification_description),
            checked = state.skipNotification,
            onCheckedChange = onSkipNotificationChanged
        )

        Spacer(modifier = Modifier.height(32.dp))

        SettingsSectionHeader(title = stringResource(R.string.settings_section_security))

        // Only show biometric option if device has biometric hardware
        if (state.showBiometricOption) {
            SettingsToggleItem(
                title = stringResource(R.string.settings_biometric_lock_title),
                description = stringResource(R.string.settings_biometric_lock_description),
                checked = state.biometricLock,
                onCheckedChange = onBiometricLockChanged
            )

            HorizontalDivider(color = DividerColor, thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
        }

        SettingsToggleItem(
            title = stringResource(R.string.settings_pattern_lock_title),
            description = stringResource(R.string.settings_pattern_lock_description),
            checked = state.patternLock,
            onCheckedChange = onPatternLockChanged
        )

        Spacer(modifier = Modifier.height(48.dp))

        DangerousZone(
            onPurgeClick = onPurgeDatabase,
            isPurging = state.isPurging
        )
    }
}

@Composable
private fun PurgeConfirmationDialog(
    isPurging: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { if (!isPurging) onDismiss() },
        containerColor = DarkBg,
        shape = RoundedCornerShape(4.dp),
        title = {
            Text(
                text = stringResource(R.string.settings_purge_dialog_title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = BlockedRed
            )
        },
        text = {
            Text(
                text = stringResource(R.string.settings_purge_dialog_message),
                style = MaterialTheme.typography.bodyMedium,
                color = TextGray
            )
        },
        confirmButton = {
            Surface(
                onClick = onConfirm,
                enabled = !isPurging,
                color = BlockedRed,
                shape = RoundedCornerShape(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isPurging) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = stringResource(R.string.settings_purge_dialog_confirm),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isPurging
            ) {
                Text(
                    text = stringResource(R.string.settings_purge_dialog_cancel),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = CyanAccent
                )
            }
        }
    )
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = CyanAccent
        )
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = DividerColor, thickness = 1.dp)
    }
}

@Composable
private fun SettingsToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextGray
            )
        }
        
        RuleTypeToggle(
            isAllowed = checked,
            onToggle = { onCheckedChange(!checked) }
        )
    }
}

@Composable
private fun RuleTypeToggle(
    isAllowed: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width = 54.dp, height = 28.dp)
            .border(1.dp, CyanAccent, RoundedCornerShape(2.dp))
            .clickable { onToggle() }
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(24.dp)
                .align(if (isAllowed) Alignment.CenterEnd else Alignment.CenterStart)
                .background(if (isAllowed) AllowedCyan else BlockedRed)
        )
    }
}

@Composable
private fun DangerousZone(
    onPurgeClick: () -> Unit,
    isPurging: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "⚠",
                color = BlockedRed,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.settings_section_dangerous),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = BlockedRed
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
            onClick = onPurgeClick,
            enabled = !isPurging,
            color = Color.Transparent,
            border = BorderStroke(1.dp, BlockedRed),
            shape = RoundedCornerShape(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isPurging) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = BlockedRed,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = null,
                        tint = BlockedRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.settings_purge_database),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = BlockedRed
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = stringResource(R.string.settings_purge_description),
            style = MaterialTheme.typography.bodySmall,
            color = TextGray,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    NoMeLlamesTheme(darkTheme = true) {
        Box(modifier = Modifier.background(DarkBg)) {
            SettingsContent(
                state = SettingsUiState.Content(
                    biometricLock = true,
                    patternLock = false,
                    skipCallLog = true,
                    skipNotification = true,
                    hasBiometricHardware = true,
                    hasDeviceCredential = true
                ),
                onSkipCallLogChanged = {},
                onSkipNotificationChanged = {},
                onBiometricLockChanged = {},
                onPatternLockChanged = {},
                onPurgeDatabase = {},
                onConfirmPurge = {},
                onDismissPurge = {},
                onConfirmEnableBiometric = {},
                onDismissEnableBiometricDialog = {},
                onConfirmDisableBiometric = {},
                onDismissDisableBiometricDialog = {},
                onPatternSet = {},
                onDismissEnablePatternDialog = {},
                onPatternCorrectForDisable = {},
                onDismissDisablePatternDialog = {}
            )
        }
    }
}
