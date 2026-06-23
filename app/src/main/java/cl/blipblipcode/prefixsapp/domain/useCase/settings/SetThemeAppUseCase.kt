package cl.blipblipcode.prefixsapp.domain.useCase.settings

import cl.blipblipcode.prefixsapp.domain.model.ThemeApp
import cl.blipblipcode.prefixsapp.domain.repositories.AppSettingsRepository
import javax.inject.Inject

class SetThemeAppUseCase @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository
) : ISetThemeAppUseCase {

    override suspend fun invoke(theme: ThemeApp): Result<Unit> {
        return appSettingsRepository.setThemeApp(theme)
    }
}
