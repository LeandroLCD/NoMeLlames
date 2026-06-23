package cl.blipblipcode.prefixsapp.domain.useCase.prefix

interface IRemovePrefixUseCase {
    suspend operator fun invoke(prefix: String): Result<Unit>
}
