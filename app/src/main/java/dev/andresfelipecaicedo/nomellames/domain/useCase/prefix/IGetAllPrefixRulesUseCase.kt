package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

import dev.andresfelipecaicedo.nomellames.domain.model.PrefixRule
import kotlinx.coroutines.flow.Flow

interface IGetAllPrefixRulesUseCase {
    operator fun invoke(): Flow<List<PrefixRule>>
}

