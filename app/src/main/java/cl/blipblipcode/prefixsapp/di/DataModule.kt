package cl.blipblipcode.prefixsapp.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import cl.blipblipcode.prefixsapp.data.local.dao.AllowedCallDao
import cl.blipblipcode.prefixsapp.data.local.dao.BlockedCallDao
import cl.blipblipcode.prefixsapp.data.local.dao.PrefixRuleDao
import cl.blipblipcode.prefixsapp.data.local.database.AppDatabase
import cl.blipblipcode.prefixsapp.utils.AppConstants
import javax.inject.Named
import javax.inject.Singleton

private val Context.appPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = AppConstants.Prefs.DATASTORE_NAME
)

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    private const val DATABASE_NAME = "spam_blocker_database"

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS allowed_calls (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    phoneNumber TEXT NOT NULL,
                    timestamp INTEGER NOT NULL
                )
            """.trimIndent())
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS prefix_rules (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    prefix TEXT NOT NULL,
                    ruleType TEXT NOT NULL,
                    createdAt INTEGER NOT NULL
                )
            """.trimIndent())

            db.execSQL("""
                CREATE UNIQUE INDEX IF NOT EXISTS index_prefix_rules_prefix ON prefix_rules(prefix)
            """.trimIndent())
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS app_settings")
        }
    }

    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE blocked_calls ADD COLUMN blockType TEXT NOT NULL DEFAULT 'Prefix'"
            )
            db.execSQL(
                "UPDATE blocked_calls SET blockType = matchedPrefix, matchedPrefix = '' " +
                    "WHERE matchedPrefix IN ('PrivateNumber', 'NonContact')"
            )
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
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
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
    fun providePrefixRuleDao(database: AppDatabase): PrefixRuleDao {
        return database.prefixRuleDao()
    }

    @Provides
    @Singleton
    @Named(AppConstants.Prefs.NAME)
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(AppConstants.Prefs.NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAppPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.appPreferencesDataStore
    }

    @Provides
    @Singleton
    fun provideFirebaseRemoteConfig(@ApplicationContext context: Context): FirebaseRemoteConfig {
        return FirebaseRemoteConfig.getInstance().also { config ->
            config.setDefaultsAsync(mapOf(
                AppConstants.RemoteConfig.KEY_VERSION_CONFIG to "{\n" +
                        "  \"release\": \"1.0.0\",\n" +
                        "  \"min_supported_version\": \"1.0.0\",\n" +
                        "  \"url_download\": \"https://play.google.com/store/apps/details?id=${context.packageName}\"\n" +
                        "}",
            ))
            config.setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build()
            )
        }
    }
}