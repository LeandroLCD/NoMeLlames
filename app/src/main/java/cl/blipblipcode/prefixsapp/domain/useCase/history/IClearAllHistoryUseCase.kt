package cl.blipblipcode.prefixsapp.domain.useCase.history

interface IClearAllHistoryUseCase {
    suspend operator fun invoke(): Result<Unit>
}

