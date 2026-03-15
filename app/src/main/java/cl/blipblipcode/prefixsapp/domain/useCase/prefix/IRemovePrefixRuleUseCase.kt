package cl.blipblipcode.prefixsapp.domain.useCase.prefix

interface IRemovePrefixRuleUseCase {
    suspend operator fun invoke(id: Long): Result<Unit>
}

