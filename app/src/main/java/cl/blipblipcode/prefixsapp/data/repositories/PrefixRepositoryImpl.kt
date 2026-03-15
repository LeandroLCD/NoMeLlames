package cl.blipblipcode.prefixsapp.data.repositories

import dagger.Lazy
import cl.blipblipcode.prefixsapp.data.local.dao.PrefixRuleDao
import cl.blipblipcode.prefixsapp.data.local.entities.PrefixRuleEntity
import cl.blipblipcode.prefixsapp.domain.exception.PrefixAlreadyExistsException
import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import cl.blipblipcode.prefixsapp.domain.repositories.AppSettingsRepository
import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefixRepositoryImpl @Inject constructor(
    private val appSettingsRepository: Lazy<AppSettingsRepository>,
    private val prefixRuleDao: PrefixRuleDao,
    dispatcher: CoroutineDispatcher
) : BaseRepository(dispatcher), PrefixRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Legacy StateFlows - kept for interface compatibility but now backed by Room
    private val _prefixes = MutableStateFlow<Set<String>>(emptySet())
    override val prefixes: StateFlow<Set<String>> = _prefixes.asStateFlow()

    private val _skipCallLog = MutableStateFlow(true)
    override val skipCallLog: StateFlow<Boolean> = _skipCallLog.asStateFlow()

    private val _skipNotification = MutableStateFlow(true)
    override val skipNotification: StateFlow<Boolean> = _skipNotification.asStateFlow()

    init {
        // Sync prefixes from Room to StateFlow
        scope.launch {
            prefixRuleDao.getBlockedPrefixes().collect { rules ->
                _prefixes.value = rules.map { it.prefix }.toSet()
            }
        }
    }

    override fun setSkipCallLog(value: Boolean) {
        _skipCallLog.tryEmit(value)
    }

    override fun setSkipNotification(value: Boolean) {
        _skipNotification.tryEmit(value)
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

    override suspend fun addPrefixRule(prefix: String, ruleType: PrefixRule.RuleType): Result<Unit> {
        val cleanPrefix = prefix.trim().filter { it.isDigit() || it == ' ' }
        if (cleanPrefix.isEmpty()) {
            return Result.failure(IllegalArgumentException("El prefijo no puede estar vacío"))
        }
        
        return makeSuspendCall {
            // Check if prefix already exists
            val existingRule = prefixRuleDao.getPrefixByValue(cleanPrefix)
            if (existingRule != null) {
                throw PrefixAlreadyExistsException(
                    existingPrefix = cleanPrefix,
                    existingRuleType = existingRule.ruleType
                )
            }
            
            // Insert new prefix rule
            prefixRuleDao.insertPrefixRule(
                PrefixRuleEntity(
                    prefix = cleanPrefix,
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
