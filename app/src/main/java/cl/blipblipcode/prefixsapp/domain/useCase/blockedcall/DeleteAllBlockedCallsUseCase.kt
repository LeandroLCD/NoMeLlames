package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import javax.inject.Inject

class DeleteAllBlockedCallsUseCase @Inject constructor(
    private val repository: BlockedCallRepository
) : IDeleteAllBlockedCallsUseCase {
    
    override suspend operator fun invoke() {
        repository.deleteAllBlockedCalls()
    }
}

