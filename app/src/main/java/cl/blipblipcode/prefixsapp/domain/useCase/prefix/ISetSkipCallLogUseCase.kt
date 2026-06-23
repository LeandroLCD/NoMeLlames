package cl.blipblipcode.prefixsapp.domain.useCase.prefix

interface ISetSkipCallLogUseCase {
    suspend operator fun invoke(value: Boolean): Result<Unit>
}
