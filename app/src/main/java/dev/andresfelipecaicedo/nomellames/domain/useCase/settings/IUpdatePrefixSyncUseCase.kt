package dev.andresfelipecaicedo.nomellames.domain.useCase.settings

interface IUpdatePrefixSyncUseCase {
    suspend operator fun invoke(prefixCount: Int): Result<Unit>
}

