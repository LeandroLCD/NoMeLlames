package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import javax.inject.Inject

class SetSkipNotificationUseCase @Inject constructor(
    private val repository: PrefixRepository
) : ISetSkipNotificationUseCase {

    override suspend operator fun invoke(value: Boolean): Result<Unit> {
        return repository.setSkipNotification(value)
    }
}
