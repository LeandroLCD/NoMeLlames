package cl.blipblipcode.prefixsapp.domain.repositories

import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PrefixRepository {
    val prefixes: StateFlow<Set<String>>
    val skipCallLog: StateFlow<Boolean>
    val skipNotification: StateFlow<Boolean>

    fun setSkipCallLog(value: Boolean)
    fun setSkipNotification(value: Boolean)
    
    // Legacy methods for backward compatibility
    fun addPrefix(prefix: String)
    fun removePrefix(prefix: String)
    
    // New methods for PrefixRule
    fun getAllPrefixRules(): Flow<List<PrefixRule>>
    fun getBlockedPrefixes(): Flow<List<PrefixRule>>
    fun getAllowedPrefixes(): Flow<List<PrefixRule>>
    suspend fun addPrefixRule(prefix: String, ruleType: PrefixRule.RuleType): Result<Unit>
    suspend fun removePrefixRule(id: Long): Result<Unit>
    suspend fun deleteAllPrefixRules(): Result<Unit>
    suspend fun removePrefixByValue(prefix: String): Result<Unit>
}
