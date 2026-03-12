package dev.andresfelipecaicedo.nomellames.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.andresfelipecaicedo.nomellames.data.local.dao.AllowedCallDao
import dev.andresfelipecaicedo.nomellames.data.local.dao.AppSettingsDao
import dev.andresfelipecaicedo.nomellames.data.local.dao.BlockedCallDao
import dev.andresfelipecaicedo.nomellames.data.local.dao.PrefixRuleDao
import dev.andresfelipecaicedo.nomellames.data.local.entities.AllowedCallEntity
import dev.andresfelipecaicedo.nomellames.data.local.entities.AppSettingsEntity
import dev.andresfelipecaicedo.nomellames.data.local.entities.BlockedCallEntity
import dev.andresfelipecaicedo.nomellames.data.local.entities.PrefixRuleEntity

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
