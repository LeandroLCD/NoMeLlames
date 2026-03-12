package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

import dev.andresfelipecaicedo.nomellames.domain.repositories.PrefixRepository
import javax.inject.Inject

class SetSkipNotificationUseCase @Inject constructor(
    private val repository: PrefixRepository
) : ISetSkipNotificationUseCase {
    
    override operator fun invoke(value: Boolean) {
        repository.setSkipNotification(value)
    }
}

