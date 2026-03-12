package dev.andresfelipecaicedo.nomellames.domain.useCase.history

import dev.andresfelipecaicedo.nomellames.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow

interface IGetCallHistoryUseCase {
    operator fun invoke(filter: HistoryFilter = HistoryFilter.ALL): Flow<List<HistoryItem>>
    
    enum class HistoryFilter {
        ALL,
        BLOCKED,
        ALLOWED
    }
}

