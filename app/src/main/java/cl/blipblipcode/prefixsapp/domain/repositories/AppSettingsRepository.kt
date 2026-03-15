package cl.blipblipcode.prefixsapp.domain.repositories

import cl.blipblipcode.prefixsapp.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updatePrefixSync(prefixCount: Int): Result<Unit>
    suspend fun updateSyncStatus(status: AppSettings.SyncStatus): Result<Unit>
}

