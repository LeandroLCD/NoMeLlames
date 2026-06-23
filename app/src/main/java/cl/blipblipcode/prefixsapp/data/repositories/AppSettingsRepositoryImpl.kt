package cl.blipblipcode.prefixsapp.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cl.blipblipcode.prefixsapp.data.local.dao.PrefixRuleDao
import cl.blipblipcode.prefixsapp.domain.model.AppSettings
import cl.blipblipcode.prefixsapp.domain.model.ThemeApp
import cl.blipblipcode.prefixsapp.domain.repositories.AppSettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepositoryImpl @Inject constructor(
    dispatcher: CoroutineDispatcher,
    private val prefixRuleDao: PrefixRuleDao,
    private val dataStore: DataStore<Preferences>
) : BaseRepository(dispatcher), AppSettingsRepository {

    companion object {
        private val KEY_THEME_APP = stringPreferencesKey("theme_app")
    }

    override fun getSettings(): Flow<AppSettings> {
        return prefixRuleDao.getPrefixStats().map { stats ->
            AppSettings(
                lastPrefixUpdateTimestamp = stats.lastTimestamp ?: 0L,
                totalPrefixCount = stats.count
            )
        }
    }

    override fun getThemeApp(): Flow<ThemeApp> {
        return dataStore.data.map { prefs ->
            val name = prefs[KEY_THEME_APP] ?: ThemeApp.System.name
            ThemeApp.entries.firstOrNull { it.name == name } ?: ThemeApp.System
        }
    }

    override suspend fun setThemeApp(theme: ThemeApp): Result<Unit> {
        return makeSuspendCall {
            dataStore.edit { prefs ->
                prefs[KEY_THEME_APP] = theme.name
            }
        }
    }
}