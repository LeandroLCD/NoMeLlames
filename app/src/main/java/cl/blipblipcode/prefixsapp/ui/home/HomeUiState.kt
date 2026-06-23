package cl.blipblipcode.prefixsapp.ui.home

import cl.blipblipcode.prefixsapp.domain.model.BlockedCall

sealed interface HomeUiState {
    data object Loading : HomeUiState
    
    data class Content(
        val prefixCount: Int = 0,
        val blockedCount: Int = 0,
        val allowedCount: Int = 0,
        val lastUpdateProgress: Int = 0,
        val lastUpdate: String?,
        val recentThreats: List<BlockedCall> = emptyList()
    ) : HomeUiState
}
