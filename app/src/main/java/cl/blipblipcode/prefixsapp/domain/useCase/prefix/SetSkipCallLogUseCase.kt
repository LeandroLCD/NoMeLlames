package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import javax.inject.Inject

class SetSkipCallLogUseCase @Inject constructor(
    private val repository: PrefixRepository
) : ISetSkipCallLogUseCase {
    
    override operator fun invoke(value: Boolean) {
        repository.setSkipCallLog(value)
    }
}

