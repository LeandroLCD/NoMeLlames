package cl.blipblipcode.prefixsapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cl.blipblipcode.prefixsapp.data.local.dao.AllowedCallDao
import cl.blipblipcode.prefixsapp.data.local.dao.BlockedCallDao
import cl.blipblipcode.prefixsapp.data.local.dao.PrefixRuleDao
import cl.blipblipcode.prefixsapp.data.local.entities.AllowedCallEntity
import cl.blipblipcode.prefixsapp.data.local.entities.BlockedCallEntity
import cl.blipblipcode.prefixsapp.data.local.entities.PrefixRuleEntity

@Database(
    entities = [
        BlockedCallEntity::class,
        AllowedCallEntity::class,
        PrefixRuleEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockedCallDao(): BlockedCallDao
    abstract fun allowedCallDao(): AllowedCallDao
    abstract fun prefixRuleDao(): PrefixRuleDao
}