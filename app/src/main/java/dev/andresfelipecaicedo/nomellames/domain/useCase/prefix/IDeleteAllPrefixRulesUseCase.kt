package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

interface IDeleteAllPrefixRulesUseCase {
    suspend operator fun invoke(): Result<Unit>
}