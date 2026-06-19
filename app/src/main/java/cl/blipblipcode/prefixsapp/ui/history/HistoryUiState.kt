package cl.blipblipcode.prefixsapp.ui.history

import cl.blipblipcode.prefixsapp.domain.model.HistoryItem
import cl.blipblipcode.prefixsapp.domain.useCase.history.IGetCallHistoryUseCase

sealed interface HistoryUiState {
    data object Loading : HistoryUiState
    
    data class Content(
        val historyItems: List<HistoryItem> = emptyList(),
        val selectedFilter: IGetCallHistoryUseCase.HistoryFilter = IGetCallHistoryUseCase.HistoryFilter.ALL,
    ) : HistoryUiState {
        val isEmpty: Boolean get() = historyItems.isEmpty()
        val canExport: Boolean get() = historyItems.isNotEmpty()
        
        // Group items by date for sticky headers
        val groupedByDate: Map<String, List<HistoryItem>> by lazy {
            historyItems.groupBy { item ->
                java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
                    .format(java.util.Date(item.timestamp))
            }
        }
    }
}
