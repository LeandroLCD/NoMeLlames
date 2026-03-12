package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

import dev.andresfelipecaicedo.nomellames.domain.repositories.PrefixRepository
import javax.inject.Inject

class RemovePrefixUseCase @Inject constructor(
    private val repository: PrefixRepository
) : IRemovePrefixUseCase {
    
    override operator fun invoke(prefix: String) {
        repository.removePrefix(prefix)
    }
}

