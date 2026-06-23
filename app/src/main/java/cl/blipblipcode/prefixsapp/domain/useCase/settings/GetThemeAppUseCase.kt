package cl.blipblipcode.prefixsapp.domain.useCase.settings

import cl.blipblipcode.prefixsapp.domain.model.ThemeApp
import cl.blipblipcode.prefixsapp.domain.repositories.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeAppUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository
) : IGetThemeAppUseCase {

    override fun invoke(): Flow<ThemeApp> {
        return appSettingsRepository.getThemeApp()
    }
}
