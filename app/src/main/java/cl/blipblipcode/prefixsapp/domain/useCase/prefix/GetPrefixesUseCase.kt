package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetPrefixesUseCase @Inject constructor(
    private val repository: PrefixRepository
) : IGetPrefixesUseCase {
    
    override operator fun invoke(): StateFlow<Set<String>> {
        return repository.prefixes
    }
}

