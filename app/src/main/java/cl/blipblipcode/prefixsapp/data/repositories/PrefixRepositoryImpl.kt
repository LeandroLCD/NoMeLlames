package cl.blipblipcode.prefixsapp.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import dagger.Lazy
import cl.blipblipcode.prefixsapp.data.local.dao.PrefixRuleDao
import cl.blipblipcode.prefixsapp.data.local.entities.PrefixRuleEntity
import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import cl.blipblipcode.prefixsapp.domain.repositories.AppSettingsRepository
import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefixRepositoryImpl @Inject constructor(
    private val appSettingsRepository: Lazy<AppSettingsRepository>,
    private val prefixRuleDao: PrefixRuleDao,
    private val scope: CoroutineScope,
    dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>
) : BaseRepository(dispatcher), PrefixRepository {

    private companion object {
        val KEY_SKIP_CALL_LOG = booleanPreferencesKey("skip_call_log")
        val KEY_SKIP_NOTIFICATION = booleanPreferencesKey("skip_notification")
    }
    
    override val prefixes: StateFlow<Set<String>> = prefixRuleDao.getBlockedPrefixes().map { rules ->
        rules.map { it.prefix }.toSet()
    }.stateIn(scope, SharingStarted.WhileSubscribed(5_000L), emptySet())

    override val skipCallLog: StateFlow<Boolean> = dataStore.data
        .map { preferences -> preferences[KEY_SKIP_CALL_LOG] ?: true }
        .stateIn(scope, SharingStarted.Eagerly, true)

    override val skipNotification: StateFlow<Boolean> = dataStore.data
        .map { preferences -> preferences[KEY_SKIP_NOTIFICATION] ?: true }
        .stateIn(scope, SharingStarted.Eagerly, true)

    override fun setSkipCallLog(value: Boolean) {
        scope.launch {
            dataStore.edit { preferences ->
                preferences[KEY_SKIP_CALL_LOG] = value
            }
        }
    }

    override fun setSkipNotification(value: Boolean) {
        scope.launch {
            dataStore.edit { preferences ->
                preferences[KEY_SKIP_NOTIFICATION] = value
            }
        }
    }

    // Legacy methods - now use Room
    override fun addPrefix(prefix: String) {
        scope.launch {
            addPrefixRule(prefix, PrefixRule.RuleType.BLOCK)
        }
    }

    override fun removePrefix(prefix: String) {
        scope.launch {
            removePrefixByValue(prefix)
        }
    }

    // Room-based methods
    override fun getAllPrefixRules(): Flow<List<PrefixRule>> {
        return prefixRuleDao.getAllPrefixRules().map { entities ->
            entities.map { it.mapToDomain() }
        }
    }

    override fun getBlockedPrefixes(): Flow<List<PrefixRule>> {
        return prefixRuleDao.getBlockedPrefixes().map { entities ->
            entities.map { it.mapToDomain() }
        }
    }

    override fun getAllowedPrefixes(): Flow<List<PrefixRule>> {
        return prefixRuleDao.getAllowedPrefixes().map { entities ->
            entities.map { it.mapToDomain() }
        }
    }

    override suspend fun getPrefixByValue(prefix: String): PrefixRule? {
        return prefixRuleDao.getPrefixByValue(prefix)?.mapToDomain()
    }

    override suspend fun addPrefixRule(prefix: String, ruleType: PrefixRule.RuleType): Result<Unit> {
        return makeSuspendCall {
            prefixRuleDao.insertPrefixRule(
                PrefixRuleEntity(
                    prefix = prefix,
                    ruleType = ruleType.name
                )
            )
            updateSyncStatus()
        }
    }

    override suspend fun removePrefixRule(id: Long): Result<Unit> {
        return makeSuspendCall {
            prefixRuleDao.deletePrefixRule(id)
            updateSyncStatus()
        }
    }

    override suspend fun deleteAllPrefixRules(): Result<Unit> {
        return makeSuspendCall {
            prefixRuleDao.deleteAllPrefixRules()
            updateSyncStatus()
        }
    }

    override suspend fun removePrefixByValue(prefix: String): Result<Unit> {
        return makeSuspendCall {
            prefixRuleDao.deletePrefixByValue(prefix)
            updateSyncStatus()
        }
    }

    private suspend fun updateSyncStatus() {
        try {
            val rules = prefixRuleDao.getAllPrefixRules().first()
            appSettingsRepository.get().updatePrefixSync(rules.size)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
