package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import javax.inject.Inject

class SetSkipNotificationUseCase @Inject constructor(
    private val repository: PrefixRepository
) : ISetSkipNotificationUseCase {
    
    override operator fun invoke(value: Boolean) {
        repository.setSkipNotification(value)
    }
}

