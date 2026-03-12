package dev.andresfelipecaicedo.nomellames.domain.useCase.history

import dev.andresfelipecaicedo.nomellames.domain.repositories.AllowedCallRepository
import dev.andresfelipecaicedo.nomellames.domain.repositories.BlockedCallRepository
import javax.inject.Inject

class ClearAllHistoryUseCase @Inject constructor(
    private val blockedCallRepository: BlockedCallRepository,
    private val allowedCallRepository: AllowedCallRepository
) : IClearAllHistoryUseCase {
    
    override suspend fun invoke(): Result<Unit> {
        return runCatching {
            blockedCallRepository.deleteAllBlockedCalls()
            allowedCallRepository.deleteAllAllowedCalls()
        }
    }
}

