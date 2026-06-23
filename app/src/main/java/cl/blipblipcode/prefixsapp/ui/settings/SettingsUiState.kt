package cl.blipblipcode.prefixsapp.ui.settings

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    
    data class Content(
        val skipCallLog: Boolean = false,
        val skipNotification: Boolean = false
    ) : SettingsUiState
}
