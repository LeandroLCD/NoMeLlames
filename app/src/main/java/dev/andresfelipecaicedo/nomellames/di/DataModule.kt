package dev.andresfelipecaicedo.nomellames.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.andresfelipecaicedo.nomellames.data.local.dao.AllowedCallDao
import dev.andresfelipecaicedo.nomellames.data.local.dao.AppSettingsDao
import dev.andresfelipecaicedo.nomellames.data.local.dao.BlockedCallDao
import dev.andresfelipecaicedo.nomellames.data.local.dao.PrefixRuleDao
import dev.andresfelipecaicedo.nomellames.data.local.database.AppDatabase
import dev.andresfelipecaicedo.nomellames.utils.AppConstants
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    private const val DATABASE_NAME = "spam_blocker_database"

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create allowed_calls table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS allowed_calls (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    phoneNumber TEXT NOT NULL,
                    timestamp INTEGER NOT NULL
                )
            """.trimIndent())
            
            // Create app_settings table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS app_settings (
                    id INTEGER PRIMARY KEY NOT NULL,
                    lastPrefixUpdateTimestamp INTEGER NOT NULL DEFAULT 0,
                    totalPrefixCount INTEGER NOT NULL DEFAULT 0,
                    lastSyncStatus TEXT NOT NULL DEFAULT 'NEVER'
                )
            """.trimIndent())
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create prefix_rules table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS prefix_rules (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    prefix TEXT NOT NULL,
                    ruleType TEXT NOT NULL,
                    createdAt INTEGER NOT NULL
                )
            """.trimIndent())
            
            // Create unique index on prefix
            db.execSQL("""
                CREATE UNIQUE INDEX IF NOT EXISTS index_prefix_rules_prefix ON prefix_rules(prefix)
            """.trimIndent())
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        )
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
        .build()
    }

    @Provides
    fun provideBlockedCallDao(database: AppDatabase): BlockedCallDao {
        return database.blockedCallDao()
    }

    @Provides
    fun provideAllowedCallDao(database: AppDatabase): AllowedCallDao {
        return database.allowedCallDao()
    }

    @Provides
    fun provideAppSettingsDao(database: AppDatabase): AppSettingsDao {
        return database.appSettingsDao()
    }

    @Provides
    fun providePrefixRuleDao(database: AppDatabase): PrefixRuleDao {
        return database.prefixRuleDao()
    }

    @Provides
    @Singleton
    @Named(AppConstants.Prefs.NAME)
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(AppConstants.Prefs.NAME, Context.MODE_PRIVATE)
    }
}
