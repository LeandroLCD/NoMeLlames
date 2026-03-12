package dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall

import dev.andresfelipecaicedo.nomellames.domain.repositories.BlockedCallRepository
import javax.inject.Inject

class DeleteBlockedCallUseCase @Inject constructor(
    private val repository: BlockedCallRepository
) : IDeleteBlockedCallUseCase {
    
    override suspend operator fun invoke(id: Long) {
        repository.deleteBlockedCall(id)
    }
}

