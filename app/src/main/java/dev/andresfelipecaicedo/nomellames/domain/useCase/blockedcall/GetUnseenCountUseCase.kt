package dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall

import dev.andresfelipecaicedo.nomellames.domain.repositories.BlockedCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUnseenCountUseCase @Inject constructor(
    private val repository: BlockedCallRepository
) : IGetUnseenCountUseCase {
    
    override operator fun invoke(): Flow<Int> {
        return repository.getUnseenCount()
    }
}

