package dev.andresfelipecaicedo.nomellames.domain.useCase.settings

import dev.andresfelipecaicedo.nomellames.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface IGetAppSettingsUseCase {
    operator fun invoke(): Flow<AppSettings>
}

