package dev.andresfelipecaicedo.nomellames.domain.useCase.history

interface IClearAllHistoryUseCase {
    suspend operator fun invoke(): Result<Unit>
}

