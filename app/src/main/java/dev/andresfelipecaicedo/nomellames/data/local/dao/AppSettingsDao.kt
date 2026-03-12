package dev.andresfelipecaicedo.nomellames.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.andresfelipecaicedo.nomellames.data.local.entities.AppSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
    
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettings(): Flow<AppSettingsEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSettings(settings: AppSettingsEntity)
    
    @Query("UPDATE app_settings SET lastPrefixUpdateTimestamp = :timestamp, totalPrefixCount = :count, lastSyncStatus = :status WHERE id = 1")
    suspend fun updatePrefixSync(timestamp: Long, count: Int, status: String)
    
    @Query("UPDATE app_settings SET lastSyncStatus = :status WHERE id = 1")
    suspend fun updateSyncStatus(status: String)
}

