package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import javax.inject.Inject

class RemovePrefixRuleUseCase @Inject constructor(
    private val prefixRepository: PrefixRepository
) : IRemovePrefixRuleUseCase {
    
    override suspend fun invoke(id: Long): Result<Unit> {
        return prefixRepository.removePrefixRule(id)
    }
}

