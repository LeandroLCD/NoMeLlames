package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

import dev.andresfelipecaicedo.nomellames.domain.model.PrefixRule

interface IAddPrefixRuleUseCase {
    suspend operator fun invoke(prefix: String, ruleType: PrefixRule.RuleType): Result<Unit>
}

