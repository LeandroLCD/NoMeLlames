package dev.andresfelipecaicedo.nomellames.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.andresfelipecaicedo.nomellames.data.local.entities.AllowedCallEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AllowedCallDao {
    
    @Query("SELECT * FROM allowed_calls ORDER BY timestamp DESC")
    fun getAllAllowedCalls(): Flow<List<AllowedCallEntity>>
    
    @Query("SELECT COUNT(*) FROM allowed_calls")
    fun getAllowedCallsCount(): Flow<Int>
    
    @Insert
    suspend fun insertAllowedCall(allowedCall: AllowedCallEntity)
    
    @Query("DELETE FROM allowed_calls")
    suspend fun deleteAllAllowedCalls()
    
    @Query("DELETE FROM allowed_calls WHERE id = :id")
    suspend fun deleteAllowedCall(id: Long)
    
    @Query("SELECT * FROM allowed_calls ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentAllowedCalls(limit: Int): Flow<List<AllowedCallEntity>>
}

