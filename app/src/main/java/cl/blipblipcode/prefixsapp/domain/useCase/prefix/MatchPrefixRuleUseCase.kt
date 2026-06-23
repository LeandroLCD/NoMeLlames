package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import javax.inject.Inject

class MatchPrefixRuleUseCase @Inject constructor() : IMatchPrefixRuleUseCase {

    override fun invoke(rules: List<PrefixRule>, phoneNumber: String): MatchResult {
        val digitsOnly = phoneNumber.filter { it.isDigit() }
        var bestMatch: PrefixRule? = null
        var bestMatchLength = 0

        for (rule in rules) {
            val cleanPrefix = rule.prefix.filter { it.isDigit() }
            if (digitsOnly.startsWith(cleanPrefix) && cleanPrefix.length > bestMatchLength) {
                bestMatch = rule
                bestMatchLength = cleanPrefix.length
            }
        }

        return when {
            bestMatch == null -> MatchResult.NoMatch
            bestMatch.isBlocked -> MatchResult.Blocked(bestMatch.prefix)
            else -> MatchResult.Allowed(bestMatch.prefix)
        }
    }
}
