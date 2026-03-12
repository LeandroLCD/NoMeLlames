package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

interface IRemovePrefixRuleUseCase {
    suspend operator fun invoke(id: Long): Result<Unit>
}

