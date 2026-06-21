package cl.blipblipcode.prefixsapp.core.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import cl.blipblipcode.prefixsapp.data.local.dao.AllowedCallDao
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
import java.io.File
import java.util.UUID
import javax.inject.Named
import javax.inject.Singleton


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
    fun providePrefixRuleDao(database: AppDatabase): PrefixRuleDao =
        database.prefixRuleDao()

    @Provides
    @Singleton
    @Named(AppConstants.Prefs.NAME)
    fun provideSharedPreferences(): SharedPreferences {

        val context = ApplicationProvider.getApplicationContext<Context>()
        return context.getSharedPreferences(
            "${AppConstants.Prefs.NAME}_${UUID.randomUUID()}_test",
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun provideAppPreferencesDataStore(): DataStore<Preferences> {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val name = "test_settings_${UUID.randomUUID()}.preferences_pb"

        return PreferenceDataStoreFactory.create(
            produceFile = {
                File(context.cacheDir, name)
            },
        )
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