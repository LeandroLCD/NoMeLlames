package dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall

import kotlinx.coroutines.flow.Flow

interface IGetBlockedCallsCountUseCase {
    operator fun invoke(): Flow<Int>
}

