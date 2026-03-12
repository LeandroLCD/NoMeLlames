package dev.andresfelipecaicedo.nomellames.domain.useCase.allowedcall

import dev.andresfelipecaicedo.nomellames.domain.repositories.AllowedCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllowedCallsCountUseCase @Inject constructor(
    private val allowedCallRepository: AllowedCallRepository
) : IGetAllowedCallsCountUseCase {
    
    override fun invoke(): Flow<Int> {
        return allowedCallRepository.getAllowedCallsCount()
    }
}

