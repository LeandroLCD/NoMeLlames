package cl.blipblipcode.prefixsapp.domain.useCase.history

import cl.blipblipcode.prefixsapp.domain.model.HistoryItem

interface IExportHistoryToCsvUseCase {
    suspend operator fun invoke(history: List<HistoryItem>): Result<String>
}

