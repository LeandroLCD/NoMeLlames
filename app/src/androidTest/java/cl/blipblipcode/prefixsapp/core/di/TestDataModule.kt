package cl.blipblipcode.prefixsapp.core.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import cl.blipblipcode.prefixsapp.data.local.dao.AllowedCallDao
import cl.blipblipcode.prefixsapp.data.local.dao.AppSettingsDao
import cl.blipblipcode.prefixsapp.data.local.dao.BlockedCallDao
import cl.blipblipcode.prefixsapp.data.local.dao.PrefixRuleDao
import cl.blipblipcode.prefixsapp.data.local.database.AppDatabase
import cl.blipblipcode.prefixsapp.di.DataModule
import cl.blipblipcode.prefixsapp.utils.AppConstants
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Named
import javax.inject.Singleton

private val Context.testAppPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "${AppConstants.Prefs.DATASTORE_NAME}_test"
)

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
object TestDataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    fun provideBlockedCallDao(database: AppDatabase): BlockedCallDao =
        database.blockedCallDao()

    @Provides
    fun provideAllowedCallDao(database: AppDatabase): AllowedCallDao =
        database.allowedCallDao()

    @Provides
    fun provideAppSettingsDao(database: AppDatabase): AppSettingsDao =
        database.appSettingsDao()

    @Provides
    fun providePrefixRuleDao(database: AppDatabase): PrefixRuleDao =
        database.prefixRuleDao()

    @Provides
    @Singleton
    @Named(AppConstants.Prefs.NAME)
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            "${AppConstants.Prefs.NAME}_test",
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideAppPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.testAppPreferencesDataStore
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
                        "}"
            ))
            config.setConfigSettingsAsync(
                FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600)
                    .build()
            )
        }
    }
}
