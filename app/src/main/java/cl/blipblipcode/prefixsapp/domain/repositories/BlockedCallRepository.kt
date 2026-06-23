package cl.blipblipcode.prefixsapp.domain.repositories

import cl.blipblipcode.prefixsapp.domain.model.BlockType
import cl.blipblipcode.prefixsapp.domain.model.BlockedCall
import kotlinx.coroutines.flow.Flow

interface BlockedCallRepository {
    fun getAllBlockedCalls(): Flow<List<BlockedCall>>
    suspend fun insertBlockedCall(phoneNumber: String, blockType: BlockType): Result<Unit>
    suspend fun deleteAllBlockedCalls(): Result<Unit>
    suspend fun deleteBlockedCall(id: Long): Result<Unit>
    fun getUnseenCount(): Flow<Int>
    suspend fun markAllAsSeen(): Result<Unit>
    fun getBlockedCallsCount(): Flow<Int>
    fun getRecentBlockedCalls(limit: Int): Flow<List<BlockedCall>>
}