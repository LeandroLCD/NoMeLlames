package dev.andresfelipecaicedo.nomellames.domain.repositories

import dev.andresfelipecaicedo.nomellames.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updatePrefixSync(prefixCount: Int): Result<Unit>
    suspend fun updateSyncStatus(status: AppSettings.SyncStatus): Result<Unit>
}

