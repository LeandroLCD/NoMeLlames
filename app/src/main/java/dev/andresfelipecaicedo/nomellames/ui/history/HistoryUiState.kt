package dev.andresfelipecaicedo.nomellames.ui.history

import dev.andresfelipecaicedo.nomellames.domain.model.HistoryItem
import dev.andresfelipecaicedo.nomellames.domain.useCase.history.IGetCallHistoryUseCase

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    
    data class Content(
        val historyItems: List<HistoryItem> = emptyList(),
        val selectedFilter: IGetCallHistoryUseCase.HistoryFilter = IGetCallHistoryUseCase.HistoryFilter.ALL,
        val isExporting: Boolean = false,
        val exportMessage: String? = null
    ) : HistoryUiState {
        val isEmpty: Boolean get() = historyItems.isEmpty()
        val canExport: Boolean get() = historyItems.isNotEmpty() && !isExporting
        
        // Group items by date for sticky headers
        val groupedByDate: Map<String, List<HistoryItem>> by lazy {
            historyItems.groupBy { item ->
                java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                    .format(java.util.Date(item.timestamp))
            }
        }
    }
}
