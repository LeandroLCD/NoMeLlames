package cl.blipblipcode.prefixsapp

import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.TelephonyManager
import cl.blipblipcode.prefixsapp.data.local.static.CountryDialingCodeProvider
import cl.blipblipcode.prefixsapp.domain.model.BlockType
import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.ContactsRepository
import cl.blipblipcode.prefixsapp.domain.useCase.allowedcall.IInsertAllowedCallUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blockedcall.IInsertBlockedCallUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blocking.IGetBlockNonContactsUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blocking.IGetBlockPrivateNumbersUseCase
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
import kotlinx.coroutines.flow.SharingStarted
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
    lateinit var blockPrivateNumbersUseCase: IGetBlockPrivateNumbersUseCase

    @Inject
    lateinit var blockNonContactsUseCase: IGetBlockNonContactsUseCase

    @Inject
    lateinit var contactsRepository: ContactsRepository

    @Inject
    lateinit var ioDispatcher: CoroutineDispatcher

    @Inject
    lateinit var getAllPrefixRulesUseCase: IGetAllPrefixRulesUseCase

    private val serviceScope by lazy { CoroutineScope(SupervisorJob() + ioDispatcher) }

    internal val skipCallLog = skipCallLogUseCase.invoke().stateIn(
        scope = serviceScope,
        started = SharingStarted.Lazily,
        initialValue = false
    )
    internal val skipNotification = skipNotificationUseCase.invoke().stateIn(
        scope = serviceScope,
        started = SharingStarted.Lazily,
        initialValue = false
    )
    internal val blockPrivateNumbers = blockPrivateNumbersUseCase.invoke().stateIn(
        scope = serviceScope,
        started = SharingStarted.Lazily,
        initialValue = false
    )
    internal val blockNonContacts = blockNonContactsUseCase.invoke().stateIn(
        scope = serviceScope,
        started = SharingStarted.Lazily,
        initialValue = false
    )
    internal val rulesStateFlow = lazy {
        getAllPrefixRulesUseCase().stateIn(
            scope = serviceScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    private var countryDialingCode: String? = null

    override fun onCreate() {
        super.onCreate()
        countryDialingCode = detectCountryDialingCode()

        Timber.i("Detected country dialing code: ${countryDialingCode ?: "unknown"}")
        Timber.i("Settings - skipCallLog: $skipCallLog, skipNotification: $skipNotification")
    }

    override fun onScreenCall(callDetails: Call.Details) {
        serviceScope.launch {
            val phoneNumber = callDetails.handle?.schemeSpecificPart ?: ""
            val normalizedNumber = normalizePhoneNumberUseCase(phoneNumber, countryDialingCode)
            val rules = rulesStateFlow.value.value
            val blockType = decideBlockType(phoneNumber, normalizedNumber, rules)

            val response = CallResponse.Builder().apply {
                when (blockType) {
                    BlockType.Allow -> {
                        Timber.i("Allowing call from $phoneNumber")
                        setDisallowCall(false)
                        setRejectCall(false)
                        saveAllowedCall(phoneNumber)
                    }
                    else -> {
                        Timber.i("Blocking call from $phoneNumber - $blockType")
                        setDisallowCall(true)
                        setRejectCall(true)
                        setSkipCallLog(skipCallLog.value)
                        setSkipNotification(skipNotification.value)
                        saveBlockedCall(phoneNumber, blockType)
                    }
                }
            }.build()

            respondToCall(callDetails, response)
        }
    }

    private suspend fun decideBlockType(
        phoneNumber: String,
        normalizedNumber: String,
        rules: List<cl.blipblipcode.prefixsapp.domain.model.PrefixRule>
    ): BlockType {
        if (blockNonContacts.value && phoneNumber.isNotBlank() && contactsRepository.hasContactsPermission()) {
            val isContact = contactsRepository.isContact(normalizedNumber)
            if (!isContact) return BlockType.NonContact
        }

        if (blockPrivateNumbers.value && phoneNumber.isBlank()) {
            return BlockType.PrivateNumber
        }

        return when (val matchResult = matchPrefixRuleUseCase(rules, normalizedNumber)) {
            is MatchResult.Blocked -> BlockType.Prefix(matchResult.prefix)
            is MatchResult.Allowed -> BlockType.AllowPrefix(matchResult.prefix)
            MatchResult.NoMatch -> BlockType.Allow
        }
    }

    private suspend fun saveBlockedCall(phoneNumber: String, blockType: BlockType) {
        try {
            insertBlockedCallUseCase.invoke(phoneNumber, blockType)
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