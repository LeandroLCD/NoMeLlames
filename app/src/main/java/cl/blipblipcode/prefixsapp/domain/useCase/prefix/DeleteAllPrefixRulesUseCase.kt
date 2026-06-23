package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import javax.inject.Inject

class DeleteAllPrefixRulesUseCase @Inject constructor(
    private val repository: PrefixRepository
) : IDeleteAllPrefixRulesUseCase {
    override suspend operator fun invoke(): Result<Unit> {
        return repository.deleteAllPrefixRules()
    }
}