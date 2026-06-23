package cl.blipblipcode.prefixsapp.domain.useCase.settings

import cl.blipblipcode.prefixsapp.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface IGetAppSettingsUseCase {
    operator fun invoke(): Flow<AppSettings>
}

