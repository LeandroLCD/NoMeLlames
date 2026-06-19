package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import javax.inject.Inject

class DeleteBlockedCallUseCase @Inject constructor(
    private val repository: BlockedCallRepository
) : IDeleteBlockedCallUseCase {
    
    override suspend operator fun invoke(id: Long) {
        repository.deleteBlockedCall(id)
    }
}

