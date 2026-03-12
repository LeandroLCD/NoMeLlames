package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

import dev.andresfelipecaicedo.nomellames.domain.repositories.PrefixRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetPrefixesUseCase @Inject constructor(
    private val repository: PrefixRepository
) : IGetPrefixesUseCase {
    
    override operator fun invoke(): StateFlow<Set<String>> {
        return repository.prefixes
    }
}

