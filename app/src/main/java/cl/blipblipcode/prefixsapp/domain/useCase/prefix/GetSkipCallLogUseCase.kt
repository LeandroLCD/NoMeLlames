package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSkipCallLogUseCase @Inject constructor(
    private val repository: PrefixRepository
) : IGetSkipCallLogUseCase {
    
    override operator fun invoke(): StateFlow<Boolean> {
        return repository.skipCallLog
    }
}

