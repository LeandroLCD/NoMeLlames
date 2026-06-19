package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import kotlinx.coroutines.flow.Flow

interface IGetBlockedCallsCountUseCase {
    operator fun invoke(): Flow<Int>
}

