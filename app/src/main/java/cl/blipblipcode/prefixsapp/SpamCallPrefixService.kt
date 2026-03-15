package cl.blipblipcode.prefixsapp

import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.TelephonyManager
import dagger.hilt.android.AndroidEntryPoint
import cl.blipblipcode.prefixsapp.data.local.static.CountryDialingCodeProvider
import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SpamCallPrefixService : CallScreeningService() {

    @Inject
    lateinit var blockedCallRepository: BlockedCallRepository

    @Inject
    lateinit var allowedCallRepository: AllowedCallRepository

    @Inject
    lateinit var prefixRepository: PrefixRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private var cachedPrefixRules: List<PrefixRule> = emptyList()
    private var skipCallLog: Boolean = true
    private var skipNotification: Boolean = true
    private var countryDialingCode: String? = null

    override fun onCreate() {
        super.onCreate()
        loadPrefixRules()
        countryDialingCode = detectCountryDialingCode()
        
        Timber.i("Detected country dialing code: ${countryDialingCode ?: "unknown"}")
        Timber.i("Loaded ${cachedPrefixRules.size} prefix rules from database")
    }

    private fun loadPrefixRules() {
        try {
            cachedPrefixRules = runBlocking {
                prefixRepository.getAllPrefixRules().first()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error loading prefix rules")
            cachedPrefixRules = emptyList()
        }
    }

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: ""
        val normalizedNumber = normalizePhoneNumber(phoneNumber)
        val matchResult = findBestMatchingRule(normalizedNumber)

        val response = CallResponse.Builder()
            .apply {
                when (matchResult) {
                    is MatchResult.Blocked -> {
                        Timber.i("Blocking call from $phoneNumber - matched prefix: ${matchResult.prefix} (${matchResult.prefix.length} chars)")
                        setDisallowCall(true)
                        setRejectCall(true)
                        setSkipCallLog(skipCallLog)
                        setSkipNotification(skipNotification)

                        serviceScope.launch {
                            saveBlockedCall(phoneNumber, matchResult.prefix)
                        }
                    }
                    is MatchResult.Allowed -> {
                        Timber.i("Allowing call from $phoneNumber - matched prefix: ${matchResult.prefix} (${matchResult.prefix.length} chars)")
                        setDisallowCall(false)
                        setRejectCall(false)

                        serviceScope.launch {
                            saveAllowedCall(phoneNumber)
                        }
                    }
                    is MatchResult.NoMatch -> {
                        Timber.i("No rule matched for $phoneNumber - allowing call")
                        setDisallowCall(false)
                        setRejectCall(false)

                        serviceScope.launch {
                            saveAllowedCall(phoneNumber)
                        }
                    }
                }
            }
            .build()

        respondToCall(callDetails, response)
    }

    /**
     * Find the best matching rule for a phone number.
     * The rule with the longest matching prefix wins.
     * 
     * Example:
     * - Phone: 123456789
     * - ALLOW rule: 1234 (4 chars) ← This wins
     * - BLOCK rule: 123 (3 chars)
     * - Result: ALLOWED because 1234 > 123
     */
    private fun findBestMatchingRule(normalizedNumber: String): MatchResult {
        var bestMatch: PrefixRule? = null
        var bestMatchLength = 0

        for (rule in cachedPrefixRules) {
            val cleanPrefix = rule.prefix.filter { it.isDigit() }
            if (normalizedNumber.startsWith(cleanPrefix) && cleanPrefix.length > bestMatchLength) {
                bestMatch = rule
                bestMatchLength = cleanPrefix.length
            }
        }

        return when {
            bestMatch == null -> MatchResult.NoMatch
            bestMatch.isBlocked -> MatchResult.Blocked(bestMatch.prefix)
            else -> MatchResult.Allowed(bestMatch.prefix)
        }
    }

    private suspend fun saveBlockedCall(phoneNumber: String, matchedPrefix: String) {
        try {
            blockedCallRepository.insertBlockedCall(phoneNumber, matchedPrefix)
        } catch (e: Exception) {
            Timber.e(e, "Error saving blocked call")
        }
    }

    private suspend fun saveAllowedCall(phoneNumber: String) {
        try {
            allowedCallRepository.insertAllowedCall(phoneNumber)
        } catch (e: Exception) {
            Timber.e(e, "Error saving allowed call")
        }
    }

    private fun normalizePhoneNumber(phoneNumber: String): String {
        val digitsOnly = phoneNumber.replace(Regex("[^0-9]"), "")
        val code = countryDialingCode
        return if (code != null && digitsOnly.startsWith(code) && digitsOnly.length > code.length) {
            digitsOnly.substring(code.length)
        } else {
            digitsOnly
        }
    }

    private fun detectCountryDialingCode(): String? {
        val tm = getSystemService(TELEPHONY_SERVICE) as? TelephonyManager
        val countryIso = (tm?.simCountryIso ?: tm?.networkCountryIso)?.uppercase()
        return CountryDialingCodeProvider.getDialingCode(applicationContext, countryIso)
    }

    private sealed class MatchResult {
        data class Blocked(val prefix: String) : MatchResult()
        data class Allowed(val prefix: String) : MatchResult()
        data object NoMatch : MatchResult()
    }
}
