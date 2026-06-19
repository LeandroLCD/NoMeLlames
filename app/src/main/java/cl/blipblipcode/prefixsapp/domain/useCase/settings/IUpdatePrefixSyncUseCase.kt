package cl.blipblipcode.prefixsapp.domain.useCase.settings

interface IUpdatePrefixSyncUseCase {
    suspend operator fun invoke(prefixCount: Int): Result<Unit>
}

