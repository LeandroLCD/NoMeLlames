package cl.blipblipcode.prefixsapp.domain.useCase.blocking

import cl.blipblipcode.prefixsapp.domain.repositories.BlockingPreferencesRepository
import javax.inject.Inject

class SetBlockPrivateNumbersUseCase @Inject constructor(
    private val repository: BlockingPreferencesRepository
) : ISetBlockPrivateNumbersUseCase {

    override suspend operator fun invoke(value: Boolean): Result<Unit> =repository.setBlockPrivateNumbers(value)

}