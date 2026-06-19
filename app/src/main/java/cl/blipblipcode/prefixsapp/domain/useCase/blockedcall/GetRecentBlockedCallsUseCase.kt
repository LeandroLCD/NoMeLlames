package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.domain.model.BlockedCall
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentBlockedCallsUseCase @Inject constructor(
    private val blockedCallRepository: BlockedCallRepository
) : IGetRecentBlockedCallsUseCase {
    
    override fun invoke(limit: Int): Flow<List<BlockedCall>> {
        return blockedCallRepository.getRecentBlockedCalls(limit)
    }
}

