package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import javax.inject.Inject

class AddPrefixRuleUseCase @Inject constructor(
    private val prefixRepository: PrefixRepository
) : IAddPrefixRuleUseCase {
    
    override suspend fun invoke(prefix: String, ruleType: PrefixRule.RuleType): Result<Unit> {
        return prefixRepository.addPrefixRule(prefix, ruleType)
    }
}

