package dev.andresfelipecaicedo.nomellames.domain.useCase.prefix

import dev.andresfelipecaicedo.nomellames.domain.repositories.PrefixRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetSkipCallLogUseCase @Inject constructor(
    private val repository: PrefixRepository
) : IGetSkipCallLogUseCase {
    
    override operator fun invoke(): StateFlow<Boolean> {
        return repository.skipCallLog
    }
}

