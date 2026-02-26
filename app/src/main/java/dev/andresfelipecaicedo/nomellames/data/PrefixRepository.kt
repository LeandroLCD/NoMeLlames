package dev.andresfelipecaicedo.nomellames.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PrefixRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _prefixes = MutableStateFlow(loadPrefixes())
    val prefixes: StateFlow<Set<String>> = _prefixes.asStateFlow()

    private val _skipCallLog = MutableStateFlow(prefs.getBoolean(KEY_SKIP_CALL_LOG, true))
    val skipCallLog: StateFlow<Boolean> = _skipCallLog.asStateFlow()

    private val _skipNotification = MutableStateFlow(prefs.getBoolean(KEY_SKIP_NOTIFICATION, true))
    val skipNotification: StateFlow<Boolean> = _skipNotification.asStateFlow()

    private fun loadPrefixes(): Set<String> {
        val stored = prefs.getStringSet(KEY_PREFIXES, null)
        return stored ?: DEFAULT_PREFIXES
    }

    fun setSkipCallLog(value: Boolean) {
        prefs.edit().putBoolean(KEY_SKIP_CALL_LOG, value).apply()
        _skipCallLog.value = value
    }

    fun setSkipNotification(value: Boolean) {
        prefs.edit().putBoolean(KEY_SKIP_NOTIFICATION, value).apply()
        _skipNotification.value = value
    }

    fun addPrefix(prefix: String) {
        val cleanPrefix = prefix.trim().filter { it.isDigit() }
        if (cleanPrefix.isNotEmpty()) {
            val current = _prefixes.value.toMutableSet()
            current.add(cleanPrefix)
            savePrefixes(current)
            _prefixes.value = current
        }
    }

    fun removePrefix(prefix: String) {
        val current = _prefixes.value.toMutableSet()
        current.remove(prefix)
        savePrefixes(current)
        _prefixes.value = current
    }

    private fun savePrefixes(prefixes: Set<String>) {
        prefs.edit().putStringSet(KEY_PREFIXES, prefixes).apply()
    }

    companion object {
        private const val PREFS_NAME = "spam_blocker_prefs"
        private const val KEY_PREFIXES = "blocked_prefixes"
        private const val KEY_SKIP_CALL_LOG = "skip_call_log"
        private const val KEY_SKIP_NOTIFICATION = "skip_notification"
        private val DEFAULT_PREFIXES = setOf("315", "316", "317", "318")

        @Volatile
        private var INSTANCE: PrefixRepository? = null

        fun getInstance(context: Context): PrefixRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = PrefixRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
