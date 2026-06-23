package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import javax.inject.Inject

class SetSkipCallLogUseCase @Inject constructor(
    private val repository: PrefixRepository
) : ISetSkipCallLogUseCase {

    override suspend operator fun invoke(value: Boolean): Result<Unit> {
        return repository.setSkipCallLog(value)
    }
}
