package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import javax.inject.Inject

class RemovePrefixUseCase @Inject constructor(
    private val repository: PrefixRepository
) : IRemovePrefixUseCase {
    
    override operator fun invoke(prefix: String) {
        repository.removePrefix(prefix)
    }
}

