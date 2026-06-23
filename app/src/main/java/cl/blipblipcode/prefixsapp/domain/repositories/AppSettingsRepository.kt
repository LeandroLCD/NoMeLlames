package cl.blipblipcode.prefixsapp.domain.repositories

import cl.blipblipcode.prefixsapp.domain.model.AppSettings
import cl.blipblipcode.prefixsapp.domain.model.ThemeApp
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun getSettings(): Flow<AppSettings>
    fun getThemeApp(): Flow<ThemeApp>
    suspend fun setThemeApp(theme: ThemeApp): Result<Unit>
}