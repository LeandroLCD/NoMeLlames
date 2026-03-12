package dev.andresfelipecaicedo.nomellames.domain.useCase.history

import dev.andresfelipecaicedo.nomellames.domain.model.HistoryItem

interface IExportHistoryToCsvUseCase {
    suspend operator fun invoke(history: List<HistoryItem>): Result<String>
}

