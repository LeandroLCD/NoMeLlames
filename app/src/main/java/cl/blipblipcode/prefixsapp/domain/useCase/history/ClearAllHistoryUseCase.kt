package cl.blipblipcode.prefixsapp.domain.useCase.history

import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
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

