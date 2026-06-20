package cl.blipblipcode.prefixsapp.core.fakes

import cl.blipblipcode.prefixsapp.domain.model.AllowedCall
import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAllowedCallRepository : AllowedCallRepository {

    private val _allowedCalls = MutableStateFlow<List<AllowedCall>>(emptyList())
    private val _count = MutableStateFlow(0)
    private val _recentAllowedCalls = MutableStateFlow<List<AllowedCall>>(emptyList())

    private var insertResult: Result<Unit> = Result.success(Unit)
    private var deleteAllResult: Result<Unit> = Result.success(Unit)
    private var deleteResult: Result<Unit> = Result.success(Unit)

    var lastInsertedPhone: String? = null
        private set
    var lastDeletedId: Long? = null
        private set
    var insertCallCount: Int = 0
        private set
    var lastDeleteAllCalled: Boolean = false
        private set
    var deleteAllShouldThrow: Throwable? = null

    override fun getAllAllowedCalls(): Flow<List<AllowedCall>> = _allowedCalls.asStateFlow()

    override fun getAllowedCallsCount(): Flow<Int> = _count.asStateFlow()

    override suspend fun insertAllowedCall(phoneNumber: String): Result<Unit> {
        insertCallCount++
        lastInsertedPhone = phoneNumber
        return insertResult
    }

    override suspend fun deleteAllAllowedCalls(): Result<Unit> {
        lastDeleteAllCalled = true
        deleteAllShouldThrow?.let { throw it }
        return deleteAllResult
    }

    override suspend fun deleteAllowedCall(id: Long): Result<Unit> {
        lastDeletedId = id
        return deleteResult
    }

    override fun getRecentAllowedCalls(limit: Int): Flow<List<AllowedCall>> =
        _recentAllowedCalls.asStateFlow()

    fun setAllowedCalls(calls: List<AllowedCall>) {
        _allowedCalls.value = calls
        _count.value = calls.size
    }

    fun setCount(value: Int) {
        _count.value = value
    }

    fun setRecentAllowedCalls(calls: List<AllowedCall>) {
        _recentAllowedCalls.value = calls
    }

    fun setInsertResult(result: Result<Unit>) {
        insertResult = result
    }

    fun setDeleteAllResult(result: Result<Unit>) {
        deleteAllResult = result
    }

    fun setDeleteResult(result: Result<Unit>) {
        deleteResult = result
    }
}
