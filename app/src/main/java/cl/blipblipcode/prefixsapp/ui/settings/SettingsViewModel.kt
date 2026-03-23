package cl.blipblipcode.prefixsapp.ui.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.blipblipcode.prefixsapp.domain.model.ThemeApp
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import cl.blipblipcode.prefixsapp.domain.useCase.history.IClearAllHistoryUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IDeleteAllPrefixRulesUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IGetSkipCallLogUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IGetSkipNotificationUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.ISetSkipCallLogUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.ISetSkipNotificationUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.settings.IGetThemeAppUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.settings.ISetThemeAppUseCase
import cl.blipblipcode.prefixsapp.ui.settings.state.SecurityState
import cl.blipblipcode.prefixsapp.ui.settings.state.SettingDialog
import cl.blipblipcode.prefixsapp.utils.AppConstants
import cl.blipblipcode.prefixsapp.utils.biometric.BiometricHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    getSkipCallLogUseCase: IGetSkipCallLogUseCase,
    getSkipNotificationUseCase: IGetSkipNotificationUseCase,
    getThemeAppUseCase: IGetThemeAppUseCase,
    private val setSkipCallLogUseCase: ISetSkipCallLogUseCase,
    private val setSkipNotificationUseCase: ISetSkipNotificationUseCase,
    private val clearAllHistoryUseCase: IClearAllHistoryUseCase,
    private val deleteAllPrefixesUseCase: IDeleteAllPrefixRulesUseCase,
    private val setThemeAppUseCase: ISetThemeAppUseCase,
    @Named(AppConstants.Prefs.NAME) private val prefs: SharedPreferences
) : ViewModel() {

    val themeApp = getThemeAppUseCase.invoke()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000L),
            ThemeApp.System
        )


    // Security state
    private val _securityState = MutableStateFlow(
        SecurityState(
            biometricLock = prefs.getBoolean(KEY_BIOMETRIC_LOCK, false),
            patternLock = prefs.getBoolean(KEY_PATTERN_LOCK, false),
            hasBiometricHardware = BiometricHelper.canAuthenticateWithBiometrics(context),
            hasDeviceCredential = BiometricHelper.canAuthenticateWithDeviceCredential(context),
            currentPattern = getStoredPattern()
        )
    )
    val securityState: StateFlow<SecurityState> = _securityState.asStateFlow()

    // Dialog state
    private val _dialogState = MutableStateFlow<SettingDialog>(SettingDialog.Idle)
    val dialogState: StateFlow<SettingDialog> = _dialogState.asStateFlow()

    // Events
    private val _eventFlow = MutableSharedFlow<SettingsEvent>()
    val eventFlow: SharedFlow<SettingsEvent> = _eventFlow.asSharedFlow()

    // UI State (settings preferences only)
    val uiState: StateFlow<SettingsUiState> = combine(
        getSkipCallLogUseCase(),
        getSkipNotificationUseCase()
    ) { skipLog, skipNotif ->
        SettingsUiState.Content(
            skipCallLog = skipLog,
            skipNotification = skipNotif
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1_000L), SettingsUiState.Loading)

    // Settings actions
    fun setSkipCallLog(value: Boolean) {
        setSkipCallLogUseCase(value)
    }

    fun setSkipNotification(value: Boolean) {
        setSkipNotificationUseCase(value)
    }

    // Biometric actions
    fun setBiometricLock(value: Boolean) {
        _dialogState.update {
            if (value) {
                SettingDialog.EnableBiometric(
                    onConfirm = ::onConfirmEnableBiometric,
                    onDismiss = ::dismissDialog
                )
            } else {
                SettingDialog.DisableBiometric(
                    onConfirm = ::onConfirmDisableBiometric,
                    onDismiss = ::dismissDialog
                )
            }
        }
    }

    fun setThemeApp(theme: ThemeApp) {
        viewModelScope.launch {
            setThemeAppUseCase.invoke(theme)
        }
    }

    fun onConfirmEnableBiometric() {
        viewModelScope.launch {
            _dialogState.value = SettingDialog.Idle
            _eventFlow.emit(
                SettingsEvent.RequestAuth(
                onSuccess = {
                    _securityState.update { it.copy(biometricLock = true) }
                    Timber.d(_securityState.value.toString())
                    prefs.edit { putBoolean(KEY_BIOMETRIC_LOCK, true) }
                }
            ))
        }
    }

    fun onConfirmDisableBiometric() {
        viewModelScope.launch {
            _dialogState.value = SettingDialog.Idle
            _eventFlow.emit(
                SettingsEvent.RequestAuth(
                onSuccess = {
                    _securityState.update { it.copy(biometricLock = false) }
                    prefs.edit { putBoolean(KEY_BIOMETRIC_LOCK, false) }
                }
            ))
        }
    }

    // Pattern actions
    fun setPatternLock(value: Boolean) {
        _dialogState.update {
            if (value) {
                SettingDialog.EnablePattern(
                    onConfirm = ::onPatternSet,
                    onDismiss = ::dismissDialog
                )
            } else {
                SettingDialog.DisablePattern(
                    onConfirm = ::onPatternCorrectForDisable,
                    onDismiss = ::dismissDialog,
                    currentPattern = _securityState.value.currentPattern
                )
            }
        }
    }

    fun onPatternSet(pattern: List<Int>) {
        _dialogState.value = SettingDialog.Idle
        savePattern(pattern)
        _securityState.update { it.copy(patternLock = true, currentPattern = pattern) }
        prefs.edit { putBoolean(KEY_PATTERN_LOCK, true) }
    }

    fun onPatternCorrectForDisable() {
        _dialogState.value = SettingDialog.Idle
        clearPattern()
        _securityState.update { it.copy(patternLock = false, currentPattern = emptyList()) }
        prefs.edit { putBoolean(KEY_PATTERN_LOCK, false) }
    }

    // Purge actions
    fun onPurgeClicked() {
        val securityState = _securityState.value
        when {
            // If biometric is enabled, use biometric authentication
            securityState.biometricLock -> {
                viewModelScope.launch {
                    _eventFlow.emit(
                        SettingsEvent.RequestAuth(
                        onSuccess = { showPurgeConfirmationDialog() }
                    ))
                }
            }
            // If only pattern is enabled, verify pattern first
            securityState.patternLock -> {
                _dialogState.update {
                    SettingDialog.VerifyPatternForPurge(
                        onPatternCorrect = ::showPurgeConfirmationDialog,
                        onDismiss = ::dismissDialog,
                        currentPattern = securityState.currentPattern
                    )
                }
            }
            // No security configured
            else -> {
                viewModelScope.launch {
                    _eventFlow.emit(SettingsEvent.RequireSecuritySetup)
                }
            }
        }
    }

    private fun showPurgeConfirmationDialog() {
        _dialogState.update {
            SettingDialog.PurgeConfirmation(
                onConfirm = ::confirmPurge,
                onDismiss = ::dismissDialog,
                isPurging = false
            )
        }
    }

    fun confirmPurge() {
        viewModelScope.launch {
            _dialogState.tryEmit(
                SettingDialog.PurgeConfirmation(
                    onConfirm = {},
                    onDismiss = ::dismissDialog,
                    isPurging = true
                )
            )
            clearAllHistoryUseCase().onSuccess {
                deleteAllPrefixesUseCase.invoke().onSuccess {
                    _dialogState.tryEmit(SettingDialog.Idle)
                    _eventFlow.emit(SettingsEvent.PurgeCompleted)
                }
            }
        }
    }

    // Dialog dismiss
    fun dismissDialog() {
        _dialogState.tryEmit(SettingDialog.Idle)
    }

    // Pattern storage
    private fun savePattern(pattern: List<Int>) {
        val patternString = pattern.joinToString(",")
        prefs.edit { putString(KEY_PATTERN_VALUE, patternString) }
    }

    private fun clearPattern() {
        prefs.edit { remove(KEY_PATTERN_VALUE) }
    }

    private fun getStoredPattern(): List<Int> {
        val patternString = prefs.getString(KEY_PATTERN_VALUE, null) ?: return emptyList()
        return patternString.split(",").mapNotNull { it.toIntOrNull() }
    }

    companion object {
        const val KEY_BIOMETRIC_LOCK = "biometric_lock"
        const val KEY_PATTERN_LOCK = "pattern_lock"
        const val KEY_PATTERN_VALUE = "pattern_value"
    }
}

sealed interface SettingsEvent {
    data class RequestAuth(val onSuccess: () -> Unit) : SettingsEvent
    data object RequireSecuritySetup : SettingsEvent
    data object PurgeCompleted : SettingsEvent
}
