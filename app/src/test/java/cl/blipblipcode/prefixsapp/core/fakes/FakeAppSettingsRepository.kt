package cl.blipblipcode.prefixsapp.core.fakes

import cl.blipblipcode.prefixsapp.domain.model.AppSettings
import cl.blipblipcode.prefixsapp.domain.model.ThemeApp
import cl.blipblipcode.prefixsapp.domain.repositories.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAppSettingsRepository : AppSettingsRepository {

    private val _settings = MutableStateFlow(AppSettings(0L, 0, AppSettings.SyncStatus.NEVER))
    private val _themeApp = MutableStateFlow(ThemeApp.System)

    private var updatePrefixSyncResult: Result<Unit> = Result.success(Unit)
    private var updateSyncStatusResult: Result<Unit> = Result.success(Unit)
    private var setThemeAppResult: Result<Unit> = Result.success(Unit)

    var lastUpdatedPrefixCount: Int? = null
        private set
    var lastUpdatedSyncStatus: AppSettings.SyncStatus? = null
        private set
    var lastSetTheme: ThemeApp? = null
        private set

    override fun getSettings(): Flow<AppSettings> = _settings.asStateFlow()

    override suspend fun updatePrefixSync(prefixCount: Int): Result<Unit> {
        lastUpdatedPrefixCount = prefixCount
        return updatePrefixSyncResult
    }

    override suspend fun updateSyncStatus(status: AppSettings.SyncStatus): Result<Unit> {
        lastUpdatedSyncStatus = status
        return updateSyncStatusResult
    }

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

    fun setUpdatePrefixSyncResult(result: Result<Unit>) {
        updatePrefixSyncResult = result
    }

    fun setUpdateSyncStatusResult(result: Result<Unit>) {
        updateSyncStatusResult = result
    }

    fun setSetThemeAppResult(result: Result<Unit>) {
        setThemeAppResult = result
    }
}
