package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.domain.model.BlockedCall
import kotlinx.coroutines.flow.Flow

interface IGetRecentBlockedCallsUseCase {
    operator fun invoke(limit: Int = DEFAULT_LIMIT): Flow<List<BlockedCall>>
    
    companion object {
        const val DEFAULT_LIMIT = 5
    }
}

