package cl.blipblipcode.prefixsapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val id: Int = 1, // Single row for app settings
    val lastPrefixUpdateTimestamp: Long = 0,
    val totalPrefixCount: Int = 0,
    val lastSyncStatus: String = SyncStatus.NEVER.name
) {
    enum class SyncStatus {
        NEVER,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
}

