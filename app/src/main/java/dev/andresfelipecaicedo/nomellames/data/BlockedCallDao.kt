package dev.andresfelipecaicedo.nomellames.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedCallDao {
    @Query("SELECT * FROM blocked_calls ORDER BY timestamp DESC")
    fun getAllBlockedCalls(): Flow<List<BlockedCall>>

    @Insert
    suspend fun insertBlockedCall(blockedCall: BlockedCall)

    @Query("DELETE FROM blocked_calls")
    suspend fun deleteAllBlockedCalls()

    @Query("DELETE FROM blocked_calls WHERE id = :id")
    suspend fun deleteBlockedCall(id: Long)
}
