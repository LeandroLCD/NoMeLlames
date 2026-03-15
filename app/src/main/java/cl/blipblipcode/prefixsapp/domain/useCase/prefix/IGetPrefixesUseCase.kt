package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import kotlinx.coroutines.flow.StateFlow

interface IGetPrefixesUseCase {
    operator fun invoke(): StateFlow<Set<String>>
}

