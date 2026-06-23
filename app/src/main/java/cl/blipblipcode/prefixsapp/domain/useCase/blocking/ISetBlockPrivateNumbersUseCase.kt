package cl.blipblipcode.prefixsapp.domain.useCase.blocking

interface ISetBlockPrivateNumbersUseCase {
    suspend operator fun invoke(value: Boolean): Result<Unit>
}