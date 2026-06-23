package cl.blipblipcode.prefixsapp.domain.useCase.history

import cl.blipblipcode.prefixsapp.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow

interface IGetCallHistoryUseCase {
    operator fun invoke(filter: HistoryFilter = HistoryFilter.ALL): Flow<List<HistoryItem>>
    
    enum class HistoryFilter {
        ALL,
        BLOCKED,
        ALLOWED
    }
}

