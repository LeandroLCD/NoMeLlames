package cl.blipblipcode.prefixsapp.data.repositories

import android.content.Context
import android.content.SharedPreferences
import cl.blipblipcode.prefixsapp.data.repositories.dto.VersionConfigDto
import cl.blipblipcode.prefixsapp.domain.model.VersionStatus
import cl.blipblipcode.prefixsapp.domain.repositories.VersionRepository
import cl.blipblipcode.prefixsapp.utils.AppConstants
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import androidx.core.content.edit
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import timber.log.Timber
import kotlin.concurrent.timer

@Singleton
class VersionRepositoryImpl @Inject constructor(
    dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val remoteConfig: FirebaseRemoteConfig,
    @Named(AppConstants.Prefs.NAME) private val sharedPreferences: SharedPreferences,
) : BaseRepository(dispatcher), VersionRepository {

    private companion object {
        const val FETCH_INTERVAL_MS = 24 * 60 * 60 * 1000L
        val json = Json { ignoreUnknownKeys = true }
    }


    override val versionStatusFlow: SharedFlow<VersionStatus> = MutableSharedFlow<VersionStatus>(
        replay = 1,
        extraBufferCapacity = 1,
    ).onSubscription {
        checkVersionStatus().onSuccess {
            emit(it)
        }.onFailure {
            emit(VersionStatus.UpToDate)
        }
    }

    override suspend fun checkVersionStatus(): Result<VersionStatus> = makeSuspendCall {
        val config = fetchOrGetCachedConfig()
        Timber.d("Config: $config")
        val currentVersion = getCurrentAppVersion()
        Timber.d("Current version: $currentVersion")
        resolveVersionStatus(currentVersion, config)
    }

    private suspend fun fetchOrGetCachedConfig(): VersionConfigDto {
        val lastFetch = sharedPreferences.getLong(AppConstants.Prefs.KEY_VERSION_LAST_FETCH, 0L)
        val now = System.currentTimeMillis()

        if (now - lastFetch < FETCH_INTERVAL_MS) {
            val cached =
                sharedPreferences.getString(AppConstants.Prefs.KEY_VERSION_CONFIG_JSON, null)
            if (cached != null) return json.decodeFromString(cached)
        }

        val fetched = fetchFromRemoteConfig()
        sharedPreferences.edit {
            putString(
                AppConstants.Prefs.KEY_VERSION_CONFIG_JSON,
                json.encodeToString(VersionConfigDto.serializer(), fetched)
            )
                .putLong(AppConstants.Prefs.KEY_VERSION_LAST_FETCH, now)
        }
        return fetched
    }

    private suspend fun fetchFromRemoteConfig(): VersionConfigDto {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                val fetched = remoteConfig.getString(AppConstants.RemoteConfig.KEY_VERSION_CONFIG)
                sharedPreferences.edit {
                    putString(
                        AppConstants.Prefs.KEY_VERSION_CONFIG_JSON, fetched
                    )
                        .putLong(
                            AppConstants.Prefs.KEY_VERSION_LAST_FETCH,
                            System.currentTimeMillis()
                        )
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                /** ignore error*/
            }

        })
        return suspendCancellableCoroutine { continuation ->
            remoteConfig.fetchAndActivate()
                .addOnSuccessListener {
                    val rawJson =
                        remoteConfig.getString(AppConstants.RemoteConfig.KEY_VERSION_CONFIG)
                    Timber.d("Raw JSON: $rawJson")
                    if (rawJson.isEmpty()) {
                        continuation.resumeWithException(Exception("Empty JSON"))
                        return@addOnSuccessListener
                    }
                    continuation.resume(json.decodeFromString(rawJson))
                }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }


    @Suppress("DEPRECATION")
    private fun getCurrentAppVersion(): String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "0.0.0"

    private fun resolveVersionStatus(current: String, config: VersionConfigDto): VersionStatus =
        when {
            compareVersions(current, config.minSupportedVersion) < 0 ->
                VersionStatus.UpdateRequired(config.release, config.urlDownload)

            compareVersions(current, config.release) < 0 ->
                VersionStatus.UpdateAvailable(config.release, config.urlDownload)

            else -> VersionStatus.UpToDate
        }

    private fun compareVersions(v1: String, v2: String): Int {
        val parts1 = v1.trim().split(".").map { it.toIntOrNull() ?: 0 }
        Timber.d("Parts 1: $parts1")
        val parts2 = v2.trim().split(".").map { it.toIntOrNull() ?: 0 }
        Timber.d("Parts 2: $parts2")
        val maxLen = maxOf(parts1.size, parts2.size)
        for (i in 0 until maxLen) {
            val diff = parts1.getOrElse(i) { 0 } - parts2.getOrElse(i) { 0 }
            Timber.d("Diff: $diff")
            if (diff != 0) return diff
        }
        return 0
    }
}
