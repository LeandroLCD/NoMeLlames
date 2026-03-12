package dev.andresfelipecaicedo.nomellames.ui.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.andresfelipecaicedo.nomellames.domain.useCase.history.IClearAllHistoryUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.prefix.IGetSkipCallLogUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.prefix.IGetSkipNotificationUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.prefix.ISetSkipCallLogUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.prefix.ISetSkipNotificationUseCase
import dev.andresfelipecaicedo.nomellames.utils.AppConstants
import dev.andresfelipecaicedo.nomellames.utils.biometric.BiometricHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    getSkipCallLogUseCase: IGetSkipCallLogUseCase,
    getSkipNotificationUseCase: IGetSkipNotificationUseCase,
    private val setSkipCallLogUseCase: ISetSkipCallLogUseCase,
    private val setSkipNotificationUseCase: ISetSkipNotificationUseCase,
    private val clearAllHistoryUseCase: IClearAllHistoryUseCase,
    @Named(AppConstants.Prefs.NAME) private val prefs: SharedPreferences
) : ViewModel() {

    private val _biometricLock = MutableStateFlow(prefs.getBoolean(KEY_BIOMETRIC_LOCK, false))
    private val _patternLock = MutableStateFlow(prefs.getBoolean(KEY_PATTERN_LOCK, false))
    private val _showPurgeDialog = MutableStateFlow(false)
    private val _isPurging = MutableStateFlow(false)
    
    // Dialog states
    private val _showEnableBiometricDialog = MutableStateFlow(false)
    private val _showDisableBiometricDialog = MutableStateFlow(false)
    private val _showEnablePatternDialog = MutableStateFlow(false)
    private val _showDisablePatternDialog = MutableStateFlow(false)

    private val _eventFlow = MutableSharedFlow<SettingsEvent>()
    val eventFlow: SharedFlow<SettingsEvent> = _eventFlow.asSharedFlow()

    val uiState = combine(
        getSkipCallLogUseCase(),
        getSkipNotificationUseCase(),
        combine(_biometricLock, _patternLock, _showPurgeDialog, _isPurging) { b, p, d, purging ->
            SecurityState(b, p, d, purging)
        },
        combine(_showEnableBiometricDialog, _showDisableBiometricDialog, _showEnablePatternDialog, _showDisablePatternDialog) { eb, db, ep, dp ->
            DialogState(eb, db, ep, dp)
        }
    ) { skipLog, skipNotif, security, dialogs ->
        SettingsUiState.Content(
            skipCallLog = skipLog,
            skipNotification = skipNotif,
            biometricLock = security.biometricLock,
            patternLock = security.patternLock,
            hasBiometricHardware = BiometricHelper.canAuthenticateWithBiometrics(context),
            hasDeviceCredential = BiometricHelper.canAuthenticateWithDeviceCredential(context),
            showPurgeDialog = security.showPurgeDialog,
            isPurging = security.isPurging,
            showEnableBiometricDialog = dialogs.showEnableBiometric,
            showDisableBiometricDialog = dialogs.showDisableBiometric,
            showEnablePatternDialog = dialogs.showEnablePattern,
            showDisablePatternDialog = dialogs.showDisablePattern,
            currentPattern = getStoredPattern()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1_000L), SettingsUiState.Loading)

    fun setSkipCallLog(value: Boolean) {
        setSkipCallLogUseCase(value)
    }

    fun setSkipNotification(value: Boolean) {
        setSkipNotificationUseCase(value)
    }

    fun setBiometricLock(value: Boolean) {
        if (value) {
            // Show enable biometric dialog
            _showEnableBiometricDialog.value = true
        } else {
            // Show disable biometric dialog
            _showDisableBiometricDialog.value = true
        }
    }

    fun onConfirmEnableBiometric() {
        viewModelScope.launch {
            _showEnableBiometricDialog.value = false
            _eventFlow.emit(SettingsEvent.RequestAuth(
                onSuccess = {
                    _biometricLock.value = true
                    prefs.edit { putBoolean(KEY_BIOMETRIC_LOCK, true) }
                }
            ))
        }
    }

    fun onDismissEnableBiometricDialog() {
        _showEnableBiometricDialog.value = false
    }

    fun onConfirmDisableBiometric() {
        viewModelScope.launch {
            _showDisableBiometricDialog.value = false
            _eventFlow.emit(SettingsEvent.RequestAuth(
                onSuccess = {
                    _biometricLock.value = false
                    prefs.edit { putBoolean(KEY_BIOMETRIC_LOCK, false) }
                }
            ))
        }
    }

    fun onDismissDisableBiometricDialog() {
        _showDisableBiometricDialog.value = false
    }

    fun setPatternLock(value: Boolean) {
        if (value) {
            // Show enable pattern dialog
            _showEnablePatternDialog.value = true
        } else {
            // Show disable pattern dialog
            _showDisablePatternDialog.value = true
        }
    }

    fun onPatternSet(pattern: List<Int>) {
        _showEnablePatternDialog.value = false
        savePattern(pattern)
        _patternLock.value = true
        prefs.edit { putBoolean(KEY_PATTERN_LOCK, true) }
    }

    fun onDismissEnablePatternDialog() {
        _showEnablePatternDialog.value = false
    }

    fun onPatternCorrectForDisable() {
        _showDisablePatternDialog.value = false
        clearPattern()
        _patternLock.value = false
        prefs.edit { putBoolean(KEY_PATTERN_LOCK, false) }
    }

    fun onDismissDisablePatternDialog() {
        _showDisablePatternDialog.value = false
    }

    private fun savePattern(pattern: List<Int>) {
        val patternString = pattern.joinToString(",")
        prefs.edit { putString(KEY_PATTERN_VALUE, patternString) }
    }

    private fun clearPattern() {
        prefs.edit { remove(KEY_PATTERN_VALUE) }
    }

    fun getStoredPattern(): List<Int> {
        val patternString = prefs.getString(KEY_PATTERN_VALUE, null) ?: return emptyList()
        return patternString.split(",").mapNotNull { it.toIntOrNull() }
    }

    fun onPurgeClicked() {
        val hasSecurity = _biometricLock.value || _patternLock.value
        if (hasSecurity) {
            // Security is configured: authenticate first, then show dialog
            viewModelScope.launch {
                _eventFlow.emit(SettingsEvent.RequestAuth(
                    onSuccess = { _showPurgeDialog.tryEmit(true) }
                ))
            }
        } else {
            // No security configured: require the user to enable one
            viewModelScope.launch {
                _eventFlow.emit(SettingsEvent.RequireSecuritySetup)
            }
        }
    }

    fun confirmPurge() {
        viewModelScope.launch {
            _showPurgeDialog.value = false
            _isPurging.value = true
            clearAllHistoryUseCase()
            _isPurging.value = false
            _eventFlow.emit(SettingsEvent.PurgeCompleted)
        }
    }

    fun dismissPurgeDialog() {
        _showPurgeDialog.value = false
    }

    companion object {
        const val KEY_BIOMETRIC_LOCK = "biometric_lock"
        const val KEY_PATTERN_LOCK = "pattern_lock"
        const val KEY_PATTERN_VALUE = "pattern_value"
    }
}

private data class SecurityState(
    val biometricLock: Boolean,
    val patternLock: Boolean,
    val showPurgeDialog: Boolean,
    val isPurging: Boolean
)

private data class DialogState(
    val showEnableBiometric: Boolean,
    val showDisableBiometric: Boolean,
    val showEnablePattern: Boolean,
    val showDisablePattern: Boolean
)

sealed interface SettingsEvent {
    data class RequestAuth(val onSuccess: () -> Unit) : SettingsEvent
    data object RequireSecuritySetup : SettingsEvent
    data object PurgeCompleted : SettingsEvent
}
