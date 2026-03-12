package dev.andresfelipecaicedo.nomellames.ui.home

import dev.andresfelipecaicedo.nomellames.domain.model.BlockedCall

sealed interface HomeUiState {
    data object Loading : HomeUiState
    
    data class Content(
        val prefixCount: Int = 0,
        val isEnabled: Boolean = false,
        val permissionsGranted: Boolean = false,
        val supportsRoleRequest: Boolean = false,
        val blockedCount: Int = 0,
        val allowedCount: Int = 0,
        val lastUpdateProgress: Int = 0,
        val recentThreats: List<BlockedCall> = emptyList()
    ) : HomeUiState {
        val hasPrefixes: Boolean get() = prefixCount > 0
        val needsPermissions: Boolean get() = !permissionsGranted
        val needsConfiguration: Boolean get() = permissionsGranted && !isEnabled
        val isActive: Boolean get() = permissionsGranted && isEnabled
    }
}
