package cl.blipblipcode.prefixsapp.ui.prefix

import cl.blipblipcode.prefixsapp.domain.model.PrefixRule

sealed interface PrefixUiState {
    data object Loading : PrefixUiState
    
    data class Content(
        val prefixRules: List<PrefixRule> = emptyList(),
        val newPrefixInput: String = "",
        val isAllowedRule: Boolean = false
    ) : PrefixUiState {
        val isEmpty: Boolean get() = prefixRules.isEmpty()
    }
}
