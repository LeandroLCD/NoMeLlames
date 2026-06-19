package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBlockedCallsCountUseCase @Inject constructor(
    private val blockedCallRepository: BlockedCallRepository
) : IGetBlockedCallsCountUseCase {
    
    override fun invoke(): Flow<Int> {
        return blockedCallRepository.getBlockedCallsCount()
    }
}

