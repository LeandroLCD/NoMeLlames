package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import kotlinx.coroutines.flow.Flow

interface IGetUnseenCountUseCase {
    operator fun invoke(): Flow<Int>
}

