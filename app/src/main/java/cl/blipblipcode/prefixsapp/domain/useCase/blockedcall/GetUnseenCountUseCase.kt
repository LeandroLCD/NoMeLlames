package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUnseenCountUseCase @Inject constructor(
    private val repository: BlockedCallRepository
) : IGetUnseenCountUseCase {
    
    override operator fun invoke(): Flow<Int> {
        return repository.getUnseenCount()
    }
}

