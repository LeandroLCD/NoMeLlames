package cl.blipblipcode.prefixsapp.core.fakes

import cl.blipblipcode.prefixsapp.domain.model.BlockType
import cl.blipblipcode.prefixsapp.domain.model.BlockedCall
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeBlockedCallRepository : BlockedCallRepository {

    private val _allBlockedCalls = MutableStateFlow<List<BlockedCall>>(emptyList())
    private val _recentBlockedCalls = MutableStateFlow<List<BlockedCall>>(emptyList())
    private val _unseenCount = MutableStateFlow(0)
    private val _blockedCallsCount = MutableStateFlow(0)

    private var insertResult: Result<Unit> = Result.success(Unit)
    private var deleteAllResult: Result<Unit> = Result.success(Unit)
    private var deleteResult: Result<Unit> = Result.success(Unit)
    private var markAllAsSeenResult: Result<Unit> = Result.success(Unit)

    var lastInsertedPhone: String? = null
        private set
    var lastInsertedBlockType: BlockType? = null
        private set
    var lastDeletedId: Long? = null
        private set
    var lastRequestedRecentLimit: Int? = null
        private set
    var insertCallCount: Int = 0
        private set
    var deleteAllCallCount: Int = 0
        private set
    var markAllAsSeenCallCount: Int = 0
        private set
    var deleteAllShouldThrow: Throwable? = null

    override fun getAllBlockedCalls(): Flow<List<BlockedCall>> = _allBlockedCalls.asStateFlow()

    override suspend fun insertBlockedCall(phoneNumber: String, blockType: BlockType): Result<Unit> {
        insertCallCount++
        lastInsertedPhone = phoneNumber
        lastInsertedBlockType = blockType
        return insertResult
    }

    override suspend fun deleteAllBlockedCalls(): Result<Unit> {
        deleteAllCallCount++
        deleteAllShouldThrow?.let { throw it }
        return deleteAllResult
    }

    override suspend fun deleteBlockedCall(id: Long): Result<Unit> {
        lastDeletedId = id
        return deleteResult
    }

    override fun getUnseenCount(): Flow<Int> = _unseenCount.asStateFlow()

    override suspend fun markAllAsSeen(): Result<Unit> {
        markAllAsSeenCallCount++
        return markAllAsSeenResult
    }

    override fun getBlockedCallsCount(): Flow<Int> = _blockedCallsCount.asStateFlow()

    override fun getRecentBlockedCalls(limit: Int): Flow<List<BlockedCall>> {
        lastRequestedRecentLimit = limit
        return _recentBlockedCalls.asStateFlow()
    }

    fun setAllBlockedCalls(calls: List<BlockedCall>) {
        _allBlockedCalls.value = calls
        _blockedCallsCount.value = calls.size
    }

    fun setRecentBlockedCalls(calls: List<BlockedCall>) {
        _recentBlockedCalls.value = calls
    }

    fun setUnseenCount(value: Int) {
        _unseenCount.value = value
    }

    fun setBlockedCallsCount(value: Int) {
        _blockedCallsCount.value = value
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

    fun setMarkAllAsSeenResult(result: Result<Unit>) {
        markAllAsSeenResult = result
    }
}