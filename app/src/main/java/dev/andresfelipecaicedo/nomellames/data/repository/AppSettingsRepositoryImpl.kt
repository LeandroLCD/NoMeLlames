package dev.andresfelipecaicedo.nomellames.data.repository

import dev.andresfelipecaicedo.nomellames.data.local.dao.AppSettingsDao
import dev.andresfelipecaicedo.nomellames.data.local.entities.AppSettingsEntity
import dev.andresfelipecaicedo.nomellames.domain.model.AppSettings
import dev.andresfelipecaicedo.nomellames.domain.repositories.AppSettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepositoryImpl @Inject constructor(
    dispatcher: CoroutineDispatcher,
    private val appSettingsDao: AppSettingsDao
) : BaseRepository(dispatcher), AppSettingsRepository {

    override fun getSettings(): Flow<AppSettings> {
        return appSettingsDao.getSettings().map { entity ->
            entity?.let {
                AppSettings(
                    lastPrefixUpdateTimestamp = it.lastPrefixUpdateTimestamp,
                    totalPrefixCount = it.totalPrefixCount,
                    syncStatus = AppSettings.SyncStatus.valueOf(it.lastSyncStatus)
                )
            } ?: AppSettings(
                lastPrefixUpdateTimestamp = 0,
                totalPrefixCount = 0,
                syncStatus = AppSettings.SyncStatus.NEVER
            )
        }
    }

    override suspend fun updatePrefixSync(prefixCount: Int): Result<Unit> {
        return makeSuspendCall {
            // Ensure settings row exists first
            val settings = AppSettingsEntity(
                id = 1,
                lastPrefixUpdateTimestamp = System.currentTimeMillis(),
                totalPrefixCount = prefixCount,
                lastSyncStatus = AppSettingsEntity.SyncStatus.COMPLETED.name
            )
            appSettingsDao.updateSettings(settings)
        }
    }

    override suspend fun updateSyncStatus(status: AppSettings.SyncStatus): Result<Unit> {
        return makeSuspendCall {
            // Create default settings if doesn't exist
            val settings = AppSettingsEntity(
                id = 1,
                lastSyncStatus = status.name
            )
            appSettingsDao.updateSettings(settings)
        }
    }
}

