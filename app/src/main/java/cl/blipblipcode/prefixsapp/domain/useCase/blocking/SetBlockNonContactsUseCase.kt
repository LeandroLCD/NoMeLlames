package cl.blipblipcode.prefixsapp.domain.useCase.blocking

import cl.blipblipcode.prefixsapp.domain.repositories.BlockingPreferencesRepository
import javax.inject.Inject

class SetBlockNonContactsUseCase @Inject constructor(
    private val repository: BlockingPreferencesRepository
) : ISetBlockNonContactsUseCase {

    override suspend operator fun invoke(value: Boolean): Result<Unit> = repository.setBlockNonContacts(value)

}