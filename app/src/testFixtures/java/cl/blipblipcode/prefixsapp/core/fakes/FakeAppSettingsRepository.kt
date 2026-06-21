package cl.blipblipcode.prefixsapp.core.fakes

import cl.blipblipcode.prefixsapp.domain.model.AppSettings
import cl.blipblipcode.prefixsapp.domain.model.ThemeApp
import cl.blipblipcode.prefixsapp.domain.repositories.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAppSettingsRepository : AppSettingsRepository {

    private val _settings = MutableStateFlow(AppSettings(0L, 0))
    private val _themeApp = MutableStateFlow(ThemeApp.System)

    private var setThemeAppResult: Result<Unit> = Result.success(Unit)

    var lastSetTheme: ThemeApp? = null
        private set

    override fun getSettings(): Flow<AppSettings> = _settings.asStateFlow()

    override fun getThemeApp(): Flow<ThemeApp> = _themeApp.asStateFlow()

    override suspend fun setThemeApp(theme: ThemeApp): Result<Unit> {
        lastSetTheme = theme
        return setThemeAppResult
    }

    fun setSettings(settings: AppSettings) {
        _settings.value = settings
    }

    fun setThemeAppValue(theme: ThemeApp) {
        _themeApp.value = theme
    }

    fun setSetThemeAppResult(result: Result<Unit>) {
        setThemeAppResult = result
    }
}