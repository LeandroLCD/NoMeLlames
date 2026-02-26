package dev.andresfelipecaicedo.nomellames

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import dev.andresfelipecaicedo.nomellames.data.AppDatabase
import dev.andresfelipecaicedo.nomellames.data.BlockedCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SpamCallScreeningService : CallScreeningService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var cachedPrefixes: Set<String> = emptySet()
    private var skipCallLog: Boolean = true
    private var skipNotification: Boolean = true

    companion object {
        private const val TAG = "NoMeLlames"
    }

    override fun onCreate() {
        super.onCreate()
        val prefs = applicationContext.getSharedPreferences("spam_blocker_prefs", MODE_PRIVATE)
        cachedPrefixes = prefs.getStringSet("blocked_prefixes", setOf("315", "316", "317", "318"))
            ?: setOf("315", "316", "317", "318")
        skipCallLog = prefs.getBoolean("skip_call_log", true)
        skipNotification = prefs.getBoolean("skip_notification", true)
    }

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: ""
        val normalizedNumber = normalizePhoneNumber(phoneNumber)

        var matchedPrefix: String? = null
        val shouldBlock = cachedPrefixes.any { prefix ->
            if (normalizedNumber.startsWith(prefix)) {
                matchedPrefix = prefix
                true
            } else {
                false
            }
        }

        val response = CallResponse.Builder()
            .apply {
                if (shouldBlock) {
                    setDisallowCall(true)
                    setRejectCall(true)
                    setSkipCallLog(skipCallLog)
                    setSkipNotification(skipNotification)

                    serviceScope.launch {
                        saveBlockedCall(phoneNumber, matchedPrefix!!)
                    }
                } else {
                    setDisallowCall(false)
                    setRejectCall(false)
                }
            }
            .build()

        respondToCall(callDetails, response)
    }

    private suspend fun saveBlockedCall(phoneNumber: String, matchedPrefix: String) {
        try {
            val database = AppDatabase.getDatabase(applicationContext)
            database.blockedCallDao().insertBlockedCall(
                BlockedCall(
                    phoneNumber = phoneNumber,
                    matchedPrefix = matchedPrefix
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error saving blocked call", e)
        }
    }

    private fun normalizePhoneNumber(phoneNumber: String): String {
        val digitsOnly = phoneNumber.replace(Regex("[^0-9]"), "")
        return when {
            digitsOnly.startsWith("57") && digitsOnly.length > 2 -> digitsOnly.substring(2)
            else -> digitsOnly
        }
    }
}
