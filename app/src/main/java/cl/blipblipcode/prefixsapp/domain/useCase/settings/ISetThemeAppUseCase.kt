package cl.blipblipcode.prefixsapp.domain.useCase.settings

import cl.blipblipcode.prefixsapp.domain.model.ThemeApp

interface ISetThemeAppUseCase {
    suspend operator fun invoke(theme: ThemeApp): Result<Unit>
}
