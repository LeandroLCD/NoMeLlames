package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.model.PrefixRule

interface IAddPrefixRuleUseCase {
    suspend operator fun invoke(prefix: String, ruleType: PrefixRule.RuleType): Result<Unit>
}

