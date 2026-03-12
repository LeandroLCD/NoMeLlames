package dev.andresfelipecaicedo.nomellames.domain.useCase.settings

import dev.andresfelipecaicedo.nomellames.domain.model.AppSettings
import dev.andresfelipecaicedo.nomellames.domain.repositories.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppSettingsUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository
) : IGetAppSettingsUseCase {
    
    override fun invoke(): Flow<AppSettings> {
        return appSettingsRepository.getSettings()
    }
}

