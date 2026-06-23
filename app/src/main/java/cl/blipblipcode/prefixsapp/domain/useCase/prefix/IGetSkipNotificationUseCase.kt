package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import kotlinx.coroutines.flow.StateFlow

interface IGetSkipNotificationUseCase {
    operator fun invoke(): StateFlow<Boolean>
}

