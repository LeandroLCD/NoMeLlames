package dev.andresfelipecaicedo.nomellames.ui.settings

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    
    data class Content(
        val skipCallLog: Boolean = false,
        val skipNotification: Boolean = false,
        val biometricLock: Boolean = false,
        val patternLock: Boolean = false,
        val hasBiometricHardware: Boolean = false,
        val hasDeviceCredential: Boolean = false,
        val showPurgeDialog: Boolean = false,
        val isPurging: Boolean = false,
        val showEnableBiometricDialog: Boolean = false,
        val showDisableBiometricDialog: Boolean = false,
        val showEnablePatternDialog: Boolean = false,
        val showDisablePatternDialog: Boolean = false,
        val currentPattern: List<Int> = emptyList()
    ) : SettingsUiState {
        val hasAnySecurity: Boolean get() = biometricLock || patternLock
        val showBiometricOption: Boolean get() = hasBiometricHardware
    }
}
