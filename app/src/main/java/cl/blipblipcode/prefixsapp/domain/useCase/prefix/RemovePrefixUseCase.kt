package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import javax.inject.Inject

class RemovePrefixUseCase @Inject constructor(
    private val repository: PrefixRepository
) : IRemovePrefixUseCase {

    override suspend operator fun invoke(prefix: String): Result<Unit> {
        return repository.removePrefix(prefix)
    }
}
