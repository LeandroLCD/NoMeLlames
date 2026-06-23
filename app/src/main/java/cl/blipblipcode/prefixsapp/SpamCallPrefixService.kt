package cl.blipblipcode.prefixsapp

import android.telecom.Call
import android.telecom.CallScreeningService
import android.telephony.TelephonyManager
import cl.blipblipcode.prefixsapp.data.local.static.CountryDialingCodeProvider
import cl.blipblipcode.prefixsapp.di.SpamCallPrefixEntryPoint
import cl.blipblipcode.prefixsapp.domain.model.BlockType
import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.MatchResult
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SpamCallPrefixService : CallScreeningService() {

    private val entryPoint by lazy {
        EntryPointAccessors.fromApplication(applicationContext, SpamCallPrefixEntryPoint::class.java)
    }

    @Inject
    lateinit var ioDispatcher: CoroutineDispatcher


    private val serviceScope by lazy { CoroutineScope(SupervisorJob() + ioDispatcher) }

    internal val skipCallLog by lazy {
        entryPoint.skipCallLogUseCase().invoke().stateIn(
            scope = serviceScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
    }
    internal val skipNotification by lazy {
        entryPoint.skipNotificationUseCase().invoke().stateIn(
            scope = serviceScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
    }
    internal val blockPrivateNumbers by lazy {
        entryPoint.blockPrivateNumbersUseCase().invoke().stateIn(
            scope = serviceScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
    }
    internal val blockNonContacts by lazy {
        entryPoint.blockNonContactsUseCase().invoke().stateIn(
            scope = serviceScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
    }
    internal val rulesStateFlow by lazy {
        entryPoint.getAllPrefixRulesUseCase().invoke().stateIn(
            scope = serviceScope,
            started = SharingStarted.Lazily,
            initialValue = null
        )
    }

    private var countryDialingCode: String? = null

    override fun onCreate() {
        super.onCreate()
        countryDialingCode = detectCountryDialingCode()



        Timber.i("Detected country dialing code: ${countryDialingCode ?: "unknown"}")

    }

    override fun onScreenCall(callDetails: Call.Details) {
        serviceScope.launch {
            val phoneNumber = callDetails.handle?.schemeSpecificPart ?: ""
            val normalizedNumber = entryPoint.normalizePhoneNumberUseCase().invoke(phoneNumber, countryDialingCode)
            val rules = rulesStateFlow.first{ it != null } ?: emptyList()
            val isSkipCallLog = skipCallLog.first{ it != null }
            val isSkipNotification = skipNotification.first{ it != null }
            val blockType = decideBlockType(phoneNumber, normalizedNumber, rules)

            val response = CallResponse.Builder().apply {
                when (blockType) {
                    BlockType.Allow, is BlockType.AllowPrefix -> {
                        Timber.i("Allowing call from $phoneNumber - $blockType")
                        setDisallowCall(false)
                        setRejectCall(false)
                        saveAllowedCall(phoneNumber)
                    }
                    else -> {
                        Timber.i("Blocking call from $phoneNumber - $blockType")
                        setDisallowCall(true)
                        setRejectCall(true)
                        setSkipCallLog(isSkipCallLog ?: false)
                        setSkipNotification(isSkipNotification ?: false)
                        saveBlockedCall(phoneNumber, blockType)
                    }
                }
            }.build()

            respondToCall(callDetails, response)
        }
    }

    internal suspend fun decideBlockType(
        phoneNumber: String,
        normalizedNumber: String,
        rules: List<PrefixRule>
    ): BlockType {
        val isBlockNonContacts = blockNonContacts.firstOrNull{ it != null } ?: false
        val isBlockPrivateNumbers = blockPrivateNumbers.firstOrNull{ it != null } ?: false
        val hasContactsPermission = entryPoint.contactsRepository().hasContactsPermission()
        Timber.d("blockNonContacts: $isBlockNonContacts, blockPrivateNumbers: $isBlockPrivateNumbers, hasContactsPermission: $hasContactsPermission")
         if (isBlockNonContacts && phoneNumber.isNotBlank() && hasContactsPermission) {
             Timber.d("Checking if $phoneNumber is a contact")
            val isContact = entryPoint.contactsRepository().isContact(phoneNumber)
             Timber.d("$isContact is a contact")
            if (!isContact) return BlockType.NonContact
        }

        if (isBlockPrivateNumbers && phoneNumber.isBlank()) {
            return BlockType.PrivateNumber
        }

        return when (val matchResult = entryPoint.matchPrefixRuleUseCase().invoke(rules, normalizedNumber)) {
            is MatchResult.Blocked -> BlockType.Prefix(matchResult.prefix)
            is MatchResult.Allowed -> BlockType.AllowPrefix(matchResult.prefix)
            MatchResult.NoMatch -> BlockType.Allow
        }
    }

    private suspend fun saveBlockedCall(phoneNumber: String, blockType: BlockType) {
        try {
            entryPoint.insertBlockedCallUseCase().invoke(phoneNumber, blockType)
        } catch (e: Exception) {
            Timber.e(e, "Error saving blocked call")
        }
    }

    private suspend fun saveAllowedCall(phoneNumber: String) {
        try {
            entryPoint.insertAllowedCallUseCase().invoke(phoneNumber)
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