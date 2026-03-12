package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

import kotlinx.coroutines.flow.StateFlow

interface IGetSkipCallLogUseCase {
    operator fun invoke(): StateFlow<Boolean>
}

