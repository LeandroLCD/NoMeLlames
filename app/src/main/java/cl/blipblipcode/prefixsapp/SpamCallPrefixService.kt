package cl.blipblipcode.prefixsapp

import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.TelephonyManager
import cl.blipblipcode.prefixsapp.data.local.static.CountryDialingCodeProvider
import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import cl.blipblipcode.prefixsapp.domain.useCase.allowedcall.IInsertAllowedCallUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blockedcall.IInsertBlockedCallUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IGetAllPrefixRulesUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IGetSkipCallLogUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IGetSkipNotificationUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IMatchPrefixRuleUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.INormalizePhoneNumberUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.MatchResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SpamCallPrefixService : CallScreeningService() {

    @Inject
    lateinit var blockedCallRepository: BlockedCallRepository

    @Inject
    lateinit var allowedCallRepository: AllowedCallRepository

    @Inject
    lateinit var skipNotificationUseCase: IGetSkipNotificationUseCase

    @Inject
    lateinit var skipCallLogUseCase: IGetSkipCallLogUseCase

    @Inject
    lateinit var matchPrefixRuleUseCase: IMatchPrefixRuleUseCase

    @Inject
    lateinit var normalizePhoneNumberUseCase: INormalizePhoneNumberUseCase

    @Inject
    lateinit var insertBlockedCallUseCase: IInsertBlockedCallUseCase

    @Inject
    lateinit var insertAllowedCallUseCase: IInsertAllowedCallUseCase

    @Inject
    lateinit var ioDispatcher: CoroutineDispatcher
    private val serviceScope by lazy { CoroutineScope(SupervisorJob() + ioDispatcher) }
    @Inject lateinit var getAllPrefixRulesUseCase: IGetAllPrefixRulesUseCase

    internal val skipCallLog = skipCallLogUseCase.invoke().stateIn(
        scope = serviceScope,
        started = kotlinx.coroutines.flow.SharingStarted.Lazily,
        initialValue = false
    )
    internal val skipNotification = skipNotificationUseCase.invoke().stateIn(
        scope = serviceScope,
        started = kotlinx.coroutines.flow.SharingStarted.Lazily,
        initialValue = false
    )
    private var countryDialingCode: String? = null

    internal val rulesStateFlow = lazy {
        getAllPrefixRulesUseCase().stateIn(
            scope = serviceScope,
            started = kotlinx.coroutines.flow.SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    override fun onCreate() {
        super.onCreate()
        countryDialingCode = detectCountryDialingCode()

        Timber.i("Detected country dialing code: ${countryDialingCode ?: "unknown"}")
        Timber.i("Settings - skipCallLog: $skipCallLog, skipNotification: $skipNotification")

    }

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: ""
        val normalizedNumber = normalizePhoneNumberUseCase(phoneNumber, countryDialingCode)
        val rules = rulesStateFlow.value.value
        val matchResult = matchPrefixRuleUseCase(rules, normalizedNumber)

        val response = CallResponse.Builder()
            .apply {
                when (matchResult) {
                    is MatchResult.Blocked -> {
                        Timber.i("Blocking call from $phoneNumber - matched prefix: ${matchResult.prefix} (${matchResult.prefix.length} chars)")
                        setDisallowCall(true)
                        setRejectCall(true)
                        setSkipCallLog(skipCallLog.value)
                        setSkipNotification(skipNotification.value)

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

    private suspend fun saveBlockedCall(phoneNumber: String, matchedPrefix: String) {
        try {
            insertBlockedCallUseCase(phoneNumber, matchedPrefix)
        } catch (e: Exception) {
            Timber.e(e, "Error saving blocked call")
        }
    }

    private suspend fun saveAllowedCall(phoneNumber: String) {
        try {
            insertAllowedCallUseCase(phoneNumber)
        } catch (e: Exception) {
            Timber.e(e, "Error saving allowed call")
        }
    }

    private fun detectCountryDialingCode(): String? {
        val tm = getSystemService(TELEPHONY_SERVICE) as? TelephonyManager
        val simIso = try { tm?.simCountryIso } catch (_: SecurityException) { null }
        val countryIso = (simIso ?: tm?.networkCountryIso)?.uppercase()
        return CountryDialingCodeProvider.getDialingCode(applicationContext, countryIso)
    }
}
