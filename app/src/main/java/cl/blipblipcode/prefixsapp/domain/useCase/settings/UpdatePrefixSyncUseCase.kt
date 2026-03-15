package cl.blipblipcode.prefixsapp.domain.useCase.settings

import cl.blipblipcode.prefixsapp.domain.repositories.AppSettingsRepository
import javax.inject.Inject

class UpdatePrefixSyncUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository
) : IUpdatePrefixSyncUseCase {
    
    override suspend fun invoke(prefixCount: Int): Result<Unit> {
        return appSettingsRepository.updatePrefixSync(prefixCount)
    }
}

