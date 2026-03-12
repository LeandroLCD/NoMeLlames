package dev.andresfelipecaicedo.nomellames.domain.useCase.allowedcall

import kotlinx.coroutines.flow.Flow

interface IGetAllowedCallsCountUseCase {
    operator fun invoke(): Flow<Int>
}

