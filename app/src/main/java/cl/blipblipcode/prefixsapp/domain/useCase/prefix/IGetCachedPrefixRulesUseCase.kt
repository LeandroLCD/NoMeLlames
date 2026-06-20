package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.model.PrefixRule

interface IGetCachedPrefixRulesUseCase {
    operator fun invoke(): List<PrefixRule>
}
