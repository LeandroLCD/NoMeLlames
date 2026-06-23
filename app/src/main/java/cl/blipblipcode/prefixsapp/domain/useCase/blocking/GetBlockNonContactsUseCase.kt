package cl.blipblipcode.prefixsapp.domain.useCase.blocking

import cl.blipblipcode.prefixsapp.domain.repositories.BlockingPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBlockNonContactsUseCase @Inject constructor(
    private val repository: BlockingPreferencesRepository
) : IGetBlockNonContactsUseCase {

    override operator fun invoke(): Flow<Boolean> =
        repository.getBlockNonContacts()
}