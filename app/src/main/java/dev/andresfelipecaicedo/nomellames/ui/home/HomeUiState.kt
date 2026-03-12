package dev.andresfelipecaicedo.nomellames.ui.home

import dev.andresfelipecaicedo.nomellames.domain.model.BlockedCall
import java.util.Date

sealed interface HomeUiState {
    data object Loading : HomeUiState
    
    data class Content(
        val prefixCount: Int = 0,
        val blockedCount: Int = 0,
        val allowedCount: Int = 0,
        val lastUpdateProgress: Int = 0,
        val lastUpdate: String?,
        val recentThreats: List<BlockedCall> = emptyList()
    ) : HomeUiState {
        val hasPrefixes: Boolean get() = prefixCount > 0

    }
}
