package dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall

import dev.andresfelipecaicedo.nomellames.domain.model.BlockedCall
import kotlinx.coroutines.flow.Flow

interface IGetAllBlockedCallsUseCase {
    operator fun invoke(): Flow<List<BlockedCall>>
}

