package dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall

import dev.andresfelipecaicedo.nomellames.domain.repositories.BlockedCallRepository
import javax.inject.Inject

class MarkAllAsSeenUseCase @Inject constructor(
    private val repository: BlockedCallRepository
) : IMarkAllAsSeenUseCase {
    
    override suspend operator fun invoke() {
        repository.markAllAsSeen()
    }
}

