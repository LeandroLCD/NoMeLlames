package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.model.PrefixRule

interface IMatchPrefixRuleUseCase {
    operator fun invoke(rules: List<PrefixRule>, phoneNumber: String): MatchResult
}
