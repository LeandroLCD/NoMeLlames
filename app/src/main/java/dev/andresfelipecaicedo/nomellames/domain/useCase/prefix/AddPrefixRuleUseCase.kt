package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

import dev.andresfelipecaicedo.nomellames.domain.model.PrefixRule
import dev.andresfelipecaicedo.nomellames.domain.repositories.PrefixRepository
import javax.inject.Inject

class AddPrefixRuleUseCase @Inject constructor(
    private val prefixRepository: PrefixRepository
) : IAddPrefixRuleUseCase {
    
    override suspend fun invoke(prefix: String, ruleType: PrefixRule.RuleType): Result<Unit> {
        return prefixRepository.addPrefixRule(prefix, ruleType)
    }
}

