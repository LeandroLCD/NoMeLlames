package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

import dev.andresfelipecaicedo.nomellames.domain.repositories.PrefixRepository
import javax.inject.Inject

class DeleteAllPrefixRulesUseCase @Inject constructor(
    private val repository: PrefixRepository
) : IDeleteAllPrefixRulesUseCase {
    override suspend operator fun invoke(): Result<Unit> {
        return repository.deleteAllPrefixRules()
    }
}