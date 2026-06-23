package cl.blipblipcode.prefixsapp.domain.useCase.allowedcall

import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllowedCallsCountUseCase @Inject constructor(
    private val allowedCallRepository: AllowedCallRepository
) : IGetAllowedCallsCountUseCase {
    override fun invoke(): Flow<Int> {
        return allowedCallRepository.getAllowedCallsCount()
    }
}

