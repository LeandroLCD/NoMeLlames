package dev.andresfelipecaicedo.nomellames.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.andresfelipecaicedo.nomellames.data.local.entities.BlockedCallEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedCallDao {
    @Query("SELECT * FROM blocked_calls ORDER BY timestamp DESC")
    fun getAllBlockedCalls(): Flow<List<BlockedCallEntity>>

    @Insert
    suspend fun insertBlockedCall(blockedCall: BlockedCallEntity)

    @Query("DELETE FROM blocked_calls")
    suspend fun deleteAllBlockedCalls()

    @Query("DELETE FROM blocked_calls WHERE id = :id")
    suspend fun deleteBlockedCall(id: Long)

    @Query("SELECT COUNT(*) FROM blocked_calls WHERE seen = 0")
    fun getUnseenCount(): Flow<Int>

    @Query("UPDATE blocked_calls SET seen = 1 WHERE seen = 0")
    suspend fun markAllAsSeen()

    @Query("SELECT COUNT(*) FROM blocked_calls")
    fun getBlockedCallsCount(): Flow<Int>

    @Query("SELECT * FROM blocked_calls ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentBlockedCalls(limit: Int): Flow<List<BlockedCallEntity>>
}
