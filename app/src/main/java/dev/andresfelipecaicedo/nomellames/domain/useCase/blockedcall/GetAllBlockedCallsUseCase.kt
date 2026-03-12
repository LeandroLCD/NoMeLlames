package dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall

import dev.andresfelipecaicedo.nomellames.domain.model.BlockedCall
import dev.andresfelipecaicedo.nomellames.domain.repositories.BlockedCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBlockedCallsUseCase @Inject constructor(
    private val repository: BlockedCallRepository
) : IGetAllBlockedCallsUseCase {
    
    override operator fun invoke(): Flow<List<BlockedCall>> {
        return repository.getAllBlockedCalls()
    }
}

