package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import javax.inject.Inject

class MarkAllAsSeenUseCase @Inject constructor(
    private val repository: BlockedCallRepository
) : IMarkAllAsSeenUseCase {
    
    override suspend operator fun invoke() {
        repository.markAllAsSeen()
    }
}

