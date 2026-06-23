package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import javax.inject.Inject

class AddPrefixUseCase @Inject constructor(
    private val repository: PrefixRepository
) : IAddPrefixUseCase {

    override suspend operator fun invoke(prefix: String): Result<Unit> {
        return repository.addPrefix(prefix)
    }
}
