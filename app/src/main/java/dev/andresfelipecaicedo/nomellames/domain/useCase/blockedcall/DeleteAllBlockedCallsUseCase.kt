package dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall

import dev.andresfelipecaicedo.nomellames.domain.repositories.BlockedCallRepository
import javax.inject.Inject

class DeleteAllBlockedCallsUseCase @Inject constructor(
    private val repository: BlockedCallRepository
) : IDeleteAllBlockedCallsUseCase {
    
    override suspend operator fun invoke() {
        repository.deleteAllBlockedCalls()
    }
}

