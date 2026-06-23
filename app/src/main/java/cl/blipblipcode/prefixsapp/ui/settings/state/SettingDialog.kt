package cl.blipblipcode.prefixsapp.ui.settings.state

sealed interface SettingDialog {
    
    data object Idle : SettingDialog

    data class EnableBiometric(
        val onConfirm: () -> Unit,
        val onDismiss: () -> Unit
    ) : SettingDialog

    data class DisableBiometric(
        val onConfirm: () -> Unit,
        val onDismiss: () -> Unit
    ) : SettingDialog

    data class EnablePattern(
        val onConfirm: (pattern: List<Int>) -> Unit,
        val onDismiss: () -> Unit
    ) : SettingDialog

    data class DisablePattern(
        val onConfirm: () -> Unit,
        val onDismiss: () -> Unit,
        val currentPattern: List<Int>
    ) : SettingDialog

    data class PurgeConfirmation(
        val onConfirm: () -> Unit,
        val onDismiss: () -> Unit,
        val isPurging: Boolean
    ) : SettingDialog

    data class VerifyPatternForPurge(
        val onPatternCorrect: () -> Unit,
        val onDismiss: () -> Unit,
        val currentPattern: List<Int>
    ) : SettingDialog
}