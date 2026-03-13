package dev.andresfelipecaicedo.nomellames.ui.settings.state

data class SecurityState(
    val biometricLock: Boolean = false,
    val patternLock: Boolean = false,
    val hasBiometricHardware: Boolean = false,
    val hasDeviceCredential: Boolean = false,
    val currentPattern: List<Int> = emptyList()
) {
    val hasAnySecurity: Boolean get() = biometricLock || patternLock
    val showBiometricOption: Boolean get() = hasBiometricHardware
}

