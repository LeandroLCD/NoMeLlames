package cl.blipblipcode.prefixsapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cl.blipblipcode.prefixsapp.data.local.dao.AllowedCallDao
import cl.blipblipcode.prefixsapp.data.local.dao.AppSettingsDao
import cl.blipblipcode.prefixsapp.data.local.dao.BlockedCallDao
import cl.blipblipcode.prefixsapp.data.local.dao.PrefixRuleDao
import cl.blipblipcode.prefixsapp.data.local.entities.AllowedCallEntity
import cl.blipblipcode.prefixsapp.data.local.entities.AppSettingsEntity
import cl.blipblipcode.prefixsapp.data.local.entities.BlockedCallEntity
import cl.blipblipcode.prefixsapp.data.local.entities.PrefixRuleEntity

@Database(
    entities = [
        BlockedCallEntity::class,
        AllowedCallEntity::class,
        AppSettingsEntity::class,
        PrefixRuleEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockedCallDao(): BlockedCallDao
    abstract fun allowedCallDao(): AllowedCallDao
    abstract fun appSettingsDao(): AppSettingsDao
    abstract fun prefixRuleDao(): PrefixRuleDao
}
