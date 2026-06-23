package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.exception.PrefixAlreadyExistsException
import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import javax.inject.Inject

class AddPrefixRuleUseCase @Inject constructor(
    private val prefixRepository: PrefixRepository
) : IAddPrefixRuleUseCase {

    override suspend fun invoke(prefix: String, ruleType: PrefixRule.RuleType): Result<Unit> {
        val cleanPrefix = prefix.trim().filter { it.isDigit() || it == ' ' }

        if (cleanPrefix.isEmpty()) {
            return Result.failure(IllegalArgumentException("El prefijo no puede estar vacío"))
        }

        val existingRule = prefixRepository.getPrefixByValue(cleanPrefix)
        if (existingRule != null) {
            return Result.failure(
                PrefixAlreadyExistsException(
                    existingPrefix = cleanPrefix,
                    existingRuleType = existingRule.ruleType.name
                )
            )
        }

        return prefixRepository.addPrefixRule(cleanPrefix, ruleType)
    }
}
