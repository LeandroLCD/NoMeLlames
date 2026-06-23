package cl.blipblipcode.prefixsapp.domain.repositories

import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PrefixRepository {
    val prefixes: StateFlow<Set<String>>
    val skipCallLog: StateFlow<Boolean>
    val skipNotification: StateFlow<Boolean>

    suspend fun setSkipCallLog(value: Boolean):Result<Unit>
    suspend fun setSkipNotification(value: Boolean):Result<Unit>

    suspend fun addPrefix(prefix: String):Result<Unit>
    suspend fun removePrefix(prefix: String):Result<Unit>
    
    // New methods for PrefixRule
    fun getAllPrefixRules(): Flow<List<PrefixRule>>
    fun getBlockedPrefixes(): Flow<List<PrefixRule>>
    fun getAllowedPrefixes(): Flow<List<PrefixRule>>
    suspend fun getPrefixByValue(prefix: String): PrefixRule?
    suspend fun addPrefixRule(prefix: String, ruleType: PrefixRule.RuleType): Result<Unit>
    suspend fun removePrefixRule(id: Long): Result<Unit>
    suspend fun deleteAllPrefixRules(): Result<Unit>
    suspend fun removePrefixByValue(prefix: String): Result<Unit>
}
