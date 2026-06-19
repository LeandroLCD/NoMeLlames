package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.domain.model.BlockedCall
import kotlinx.coroutines.flow.Flow

interface IGetAllBlockedCallsUseCase {
    operator fun invoke(): Flow<List<BlockedCall>>
}

