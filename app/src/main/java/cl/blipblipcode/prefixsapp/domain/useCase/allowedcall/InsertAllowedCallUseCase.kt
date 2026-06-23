package cl.blipblipcode.prefixsapp.domain.useCase.allowedcall

import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import javax.inject.Inject

class InsertAllowedCallUseCase @Inject constructor(
    private val allowedCallRepository: AllowedCallRepository
) : IInsertAllowedCallUseCase {
    
    override suspend fun invoke(phoneNumber: String): Result<Unit> {
        return allowedCallRepository.insertAllowedCall(phoneNumber)
    }
}

