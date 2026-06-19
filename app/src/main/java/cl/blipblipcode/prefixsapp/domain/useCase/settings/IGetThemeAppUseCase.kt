package cl.blipblipcode.prefixsapp.domain.useCase.settings

import cl.blipblipcode.prefixsapp.domain.model.ThemeApp
import kotlinx.coroutines.flow.Flow

interface IGetThemeAppUseCase {
    operator fun invoke(): Flow<ThemeApp>
}
