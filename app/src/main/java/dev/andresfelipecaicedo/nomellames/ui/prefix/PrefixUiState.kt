package dev.andresfelipecaicedo.nomellames.ui.prefix

import dev.andresfelipecaicedo.nomellames.domain.model.PrefixRule

sealed interface PrefixUiState {
    data object Loading : PrefixUiState
    
    data class Content(
        val prefixRules: List<PrefixRule> = emptyList(),
        val newPrefixInput: String = "",
        val isAllowedRule: Boolean = false
    ) : PrefixUiState {
        val isEmpty: Boolean get() = prefixRules.isEmpty()
        val blockedCount: Int get() = prefixRules.count { it.isBlocked }
        val allowedCount: Int get() = prefixRules.count { it.isAllowed }
        val canAdd: Boolean get() = newPrefixInput.isNotBlank()
    }
}
