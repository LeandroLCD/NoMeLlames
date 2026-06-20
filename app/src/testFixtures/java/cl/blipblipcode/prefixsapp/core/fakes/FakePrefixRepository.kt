package cl.blipblipcode.prefixsapp.core.fakes

import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakePrefixRepository : PrefixRepository {

    private val _prefixes = MutableStateFlow<Set<String>>(emptySet())
    private val _skipCallLog = MutableStateFlow(false)
    private val _skipNotification = MutableStateFlow(false)
    private val _allPrefixRules = MutableStateFlow<List<PrefixRule>>(emptyList())
    private val _blockedPrefixes = MutableStateFlow<List<PrefixRule>>(emptyList())
    private val _allowedPrefixes = MutableStateFlow<List<PrefixRule>>(emptyList())
    private val storedByValue = mutableMapOf<String, PrefixRule>()

    private var addPrefixRuleResult: Result<Unit> = Result.success(Unit)
    private var removePrefixRuleResult: Result<Unit> = Result.success(Unit)
    private var deleteAllPrefixRulesResult: Result<Unit> = Result.success(Unit)
    private var removePrefixByValueResult: Result<Unit> = Result.success(Unit)

    var lastSetSkipCallLog: Boolean? = null
        private set
    var lastSetSkipNotification: Boolean? = null
        private set
    var lastAddedPrefix: String? = null
        private set
    var lastRemovedPrefix: String? = null
        private set
    var lastAddedPrefixRule: String? = null
        private set
    var lastAddedRuleType: PrefixRule.RuleType? = null
        private set
    var lastRemovedPrefixRuleId: Long? = null
        private set
    var lastRemovedPrefixByValue: String? = null
        private set
    var getAllPrefixRulesCallCount: Int = 0
        private set
    var shouldThrowOnGetAllPrefixRules: Throwable? = null

    override val prefixes: StateFlow<Set<String>> = _prefixes.asStateFlow()
    override val skipCallLog: StateFlow<Boolean> = _skipCallLog.asStateFlow()
    override val skipNotification: StateFlow<Boolean> = _skipNotification.asStateFlow()

    override fun setSkipCallLog(value: Boolean) {
        lastSetSkipCallLog = value
        _skipCallLog.value = value
    }

    override fun setSkipNotification(value: Boolean) {
        lastSetSkipNotification = value
        _skipNotification.value = value
    }

    override fun addPrefix(prefix: String) {
        lastAddedPrefix = prefix
    }

    override fun removePrefix(prefix: String) {
        lastRemovedPrefix = prefix
    }

    override fun getAllPrefixRules(): Flow<List<PrefixRule>> {
        getAllPrefixRulesCallCount++
        shouldThrowOnGetAllPrefixRules?.let { throw it }
        return _allPrefixRules.asStateFlow()
    }

    override fun getBlockedPrefixes(): Flow<List<PrefixRule>> = _blockedPrefixes.asStateFlow()

    override fun getAllowedPrefixes(): Flow<List<PrefixRule>> = _allowedPrefixes.asStateFlow()

    override suspend fun getPrefixByValue(prefix: String): PrefixRule? = storedByValue[prefix]

    override suspend fun addPrefixRule(prefix: String, ruleType: PrefixRule.RuleType): Result<Unit> {
        lastAddedPrefixRule = prefix
        lastAddedRuleType = ruleType
        val id = (storedByValue.size + 1).toLong()
        storedByValue[prefix] = PrefixRule(id = id, prefix = prefix, ruleType = ruleType)
        return addPrefixRuleResult
    }

    override suspend fun removePrefixRule(id: Long): Result<Unit> {
        lastRemovedPrefixRuleId = id
        return removePrefixRuleResult
    }

    override suspend fun deleteAllPrefixRules(): Result<Unit> = deleteAllPrefixRulesResult

    override suspend fun removePrefixByValue(prefix: String): Result<Unit> {
        lastRemovedPrefixByValue = prefix
        return removePrefixByValueResult
    }

    fun setPrefixes(prefixes: Set<String>) {
        _prefixes.value = prefixes
    }

    fun setSkipCallLogValue(value: Boolean) {
        _skipCallLog.value = value
    }

    fun setSkipNotificationValue(value: Boolean) {
        _skipNotification.value = value
    }

    fun setAllPrefixRules(rules: List<PrefixRule>) {
        _allPrefixRules.value = rules
    }

    fun setBlockedPrefixes(rules: List<PrefixRule>) {
        _blockedPrefixes.value = rules
    }

    fun setAllowedPrefixes(rules: List<PrefixRule>) {
        _allowedPrefixes.value = rules
    }

    fun setAddPrefixRuleResult(result: Result<Unit>) {
        addPrefixRuleResult = result
    }

    fun setRemovePrefixRuleResult(result: Result<Unit>) {
        removePrefixRuleResult = result
    }

    fun setDeleteAllPrefixRulesResult(result: Result<Unit>) {
        deleteAllPrefixRulesResult = result
    }

    fun setRemovePrefixByValueResult(result: Result<Unit>) {
        removePrefixByValueResult = result
    }
}
