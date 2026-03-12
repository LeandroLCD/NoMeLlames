package dev.andresfelipecaicedo.nomellames.domain.repositories

import dev.andresfelipecaicedo.nomellames.domain.model.BlockedCall
import kotlinx.coroutines.flow.Flow

interface BlockedCallRepository {
    fun getAllBlockedCalls(): Flow<List<BlockedCall>>
    suspend fun insertBlockedCall(phoneNumber: String, matchedPrefix: String): Result<Unit>
    suspend fun deleteAllBlockedCalls(): Result<Unit>
    suspend fun deleteBlockedCall(id: Long): Result<Unit>
    fun getUnseenCount(): Flow<Int>
    suspend fun markAllAsSeen(): Result<Unit>
    fun getBlockedCallsCount(): Flow<Int>
    fun getRecentBlockedCalls(limit: Int): Flow<List<BlockedCall>>
}
