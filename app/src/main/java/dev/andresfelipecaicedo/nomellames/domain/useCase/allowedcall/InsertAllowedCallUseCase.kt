package dev.andresfelipecaicedo.nomellames.domain.useCase.allowedcall

import dev.andresfelipecaicedo.nomellames.domain.repositories.AllowedCallRepository
import javax.inject.Inject

class InsertAllowedCallUseCase @Inject constructor(
    private val allowedCallRepository: AllowedCallRepository
) : IInsertAllowedCallUseCase {
    
    override suspend fun invoke(phoneNumber: String): Result<Unit> {
        return allowedCallRepository.insertAllowedCall(phoneNumber)
    }
}

