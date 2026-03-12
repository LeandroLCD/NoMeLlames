package dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall

import dev.andresfelipecaicedo.nomellames.domain.model.BlockedCall
import dev.andresfelipecaicedo.nomellames.domain.repositories.BlockedCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentBlockedCallsUseCase @Inject constructor(
    private val blockedCallRepository: BlockedCallRepository
) : IGetRecentBlockedCallsUseCase {
    
    override fun invoke(limit: Int): Flow<List<BlockedCall>> {
        return blockedCallRepository.getRecentBlockedCalls(limit)
    }
}

