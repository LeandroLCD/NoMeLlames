package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

import dev.andresfelipecaicedo.nomellames.domain.repositories.PrefixRepository
import javax.inject.Inject

class AddPrefixUseCase @Inject constructor(
    private val repository: PrefixRepository
) : IAddPrefixUseCase {
    
    override operator fun invoke(prefix: String) {
        repository.addPrefix(prefix)
    }
}

