package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.domain.model.BlockedCall
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllBlockedCallsUseCase @Inject constructor(
    private val repository: BlockedCallRepository
) : IGetAllBlockedCallsUseCase {
    
    override operator fun invoke(): Flow<List<BlockedCall>> {
        return repository.getAllBlockedCalls()
    }
}

