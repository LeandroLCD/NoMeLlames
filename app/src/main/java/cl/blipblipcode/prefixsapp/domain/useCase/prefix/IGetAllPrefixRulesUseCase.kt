package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import kotlinx.coroutines.flow.Flow

interface IGetAllPrefixRulesUseCase {
    operator fun invoke(): Flow<List<PrefixRule>>
}

