package dev.andresfelipecaicedo.nomellames.domain.useCase.settings

import dev.andresfelipecaicedo.nomellames.domain.repositories.AppSettingsRepository
import javax.inject.Inject

class UpdatePrefixSyncUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository
) : IUpdatePrefixSyncUseCase {
    
    override suspend fun invoke(prefixCount: Int): Result<Unit> {
        return appSettingsRepository.updatePrefixSync(prefixCount)
    }
}

