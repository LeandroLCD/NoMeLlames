package cl.blipblipcode.prefixsapp.domain.useCase.prefix

interface IAddPrefixUseCase {
    suspend operator fun invoke(prefix: String): Result<Unit>
}
