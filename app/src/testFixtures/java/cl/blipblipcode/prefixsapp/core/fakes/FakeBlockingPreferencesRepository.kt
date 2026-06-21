package cl.blipblipcode.prefixsapp.core.fakes

import cl.blipblipcode.prefixsapp.domain.repositories.BlockingPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeBlockingPreferencesRepository : BlockingPreferencesRepository {

    private val _blockPrivateNumbers = MutableStateFlow(false)
    private val _blockNonContacts = MutableStateFlow(false)

    private var setPrivateNumbersResult: Result<Unit> = Result.success(Unit)
    private var setNonContactsResult: Result<Unit> = Result.success(Unit)

    var lastSetBlockPrivateNumbers: Boolean? = null
        private set
    var lastSetBlockNonContacts: Boolean? = null
        private set
    var setBlockPrivateNumbersCallCount: Int = 0
        private set
    var setBlockNonContactsCallCount: Int = 0
        private set

    override fun getBlockPrivateNumbers(): Flow<Boolean> = _blockPrivateNumbers.asStateFlow()

    override fun getBlockNonContacts(): Flow<Boolean> = _blockNonContacts.asStateFlow()

    override suspend fun setBlockPrivateNumbers(value: Boolean): Result<Unit> {
        setBlockPrivateNumbersCallCount++
        lastSetBlockPrivateNumbers = value
        _blockPrivateNumbers.value = value
        return setPrivateNumbersResult
    }

    override suspend fun setBlockNonContacts(value: Boolean): Result<Unit> {
        setBlockNonContactsCallCount++
        lastSetBlockNonContacts = value
        _blockNonContacts.value = value
        return setNonContactsResult
    }

    fun setPrivateNumbersValue(value: Boolean) {
        _blockPrivateNumbers.value = value
    }

    fun setNonContactsValue(value: Boolean) {
        _blockNonContacts.value = value
    }

    fun setSetBlockPrivateNumbersResult(result: Result<Unit>) {
        setPrivateNumbersResult = result
    }

    fun setSetBlockNonContactsResult(result: Result<Unit>) {
        setNonContactsResult = result
    }
}
