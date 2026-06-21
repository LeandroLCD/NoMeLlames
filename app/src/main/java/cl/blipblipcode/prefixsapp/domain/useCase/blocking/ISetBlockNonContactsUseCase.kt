package cl.blipblipcode.prefixsapp.domain.useCase.blocking

interface ISetBlockNonContactsUseCase {
    suspend operator fun invoke(value: Boolean): Result<Unit>
}