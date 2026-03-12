package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

import kotlinx.coroutines.flow.StateFlow

interface IGetPrefixesUseCase {
    operator fun invoke(): StateFlow<Set<String>>
}

