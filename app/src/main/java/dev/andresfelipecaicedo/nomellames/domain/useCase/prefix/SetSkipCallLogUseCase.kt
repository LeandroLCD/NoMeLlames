package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

import dev.andresfelipecaicedo.nomellames.domain.repositories.PrefixRepository
import javax.inject.Inject

class SetSkipCallLogUseCase @Inject constructor(
    private val repository: PrefixRepository
) : ISetSkipCallLogUseCase {
    
    override operator fun invoke(value: Boolean) {
        repository.setSkipCallLog(value)
    }
}

