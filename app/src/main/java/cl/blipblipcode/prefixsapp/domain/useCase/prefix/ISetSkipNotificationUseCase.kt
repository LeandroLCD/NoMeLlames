package cl.blipblipcode.prefixsapp.domain.useCase.prefix

interface ISetSkipNotificationUseCase {
    suspend operator fun invoke(value: Boolean): Result<Unit>
}
