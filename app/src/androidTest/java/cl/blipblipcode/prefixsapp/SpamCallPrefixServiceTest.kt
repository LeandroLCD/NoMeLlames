package cl.blipblipcode.prefixsapp

import android.Manifest
import android.app.Instrumentation
import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.data.repositories.BlockingPreferencesRepositoryImpl
import cl.blipblipcode.prefixsapp.domain.model.BlockType
import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import cl.blipblipcode.prefixsapp.domain.repositories.PrefixRepository
import cl.blipblipcode.prefixsapp.domain.useCase.allowedcall.IInsertAllowedCallUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blockedcall.IInsertBlockedCallUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IGetAllPrefixRulesUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IMatchPrefixRuleUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.INormalizePhoneNumberUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.MatchResult
import cl.blipblipcode.prefixsapp.rules.MainDispatcherRule
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SpamCallPrefixServiceTest {

    @get:Rule(order = 0)
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var prefixRepository: PrefixRepository
    @Inject lateinit var blockedCallRepository: BlockedCallRepository
    @Inject lateinit var allowedCallRepository: AllowedCallRepository

    @Inject lateinit var getAllPrefixRulesUseCase: IGetAllPrefixRulesUseCase
    @Inject lateinit var matchPrefixRuleUseCase: IMatchPrefixRuleUseCase
    @Inject lateinit var normalizePhoneNumberUseCase: INormalizePhoneNumberUseCase
    @Inject lateinit var insertBlockedCallUseCase: IInsertBlockedCallUseCase
    @Inject lateinit var insertAllowedCallUseCase: IInsertAllowedCallUseCase

    @Inject lateinit var contactsRepository: cl.blipblipcode.prefixsapp.domain.repositories.ContactsRepository
    @Inject lateinit var blockingPreferencesRepositoryImpl: BlockingPreferencesRepositoryImpl

    @Inject @ApplicationContext
    lateinit var appContext: Context

    private val instrumentation: Instrumentation
        get() = InstrumentationRegistry.getInstrumentation()

    private val createdRawContactIds = mutableListOf<Long>()

    @Before
    fun setUp() {
        hiltRule.inject()
        instrumentation.uiAutomation.grantRuntimePermission(
            appContext.packageName,
            Manifest.permission.READ_CONTACTS
        )
        instrumentation.uiAutomation.grantRuntimePermission(
            appContext.packageName,
            Manifest.permission.WRITE_CONTACTS
        )
    }

    @After
    fun tearDown() {
        runBlocking {
            createdRawContactIds.forEach { id ->
                appContext.contentResolver.delete(
                    ContactsContract.RawContacts.CONTENT_URI,
                    "${ContactsContract.RawContacts._ID} = ?",
                    arrayOf(id.toString())
                )
            }
            createdRawContactIds.clear()
            blockingPreferencesRepositoryImpl.setBlockNonContacts(false).getOrThrow()
        }
    }

    private fun insertContact(phoneNumber: String): Long {
        val rawContactValues = ContentValues().apply {
            put(ContactsContract.RawContacts.ACCOUNT_TYPE, "com.google")
            put(ContactsContract.RawContacts.ACCOUNT_NAME, "test_account")
        }
        val rawContactUri = appContext.contentResolver.insert(
            ContactsContract.RawContacts.CONTENT_URI,
            rawContactValues
        )
        val rawContactId = rawContactUri?.lastPathSegment?.toLongOrNull() ?: 0L

        val phoneValues = ContentValues().apply {
            put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
            put(
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            )
            put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
        }
        appContext.contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)

        createdRawContactIds.add(rawContactId)
        return rawContactId
    }

    @Test
    fun should_return_blocked_match_when_number_matches_block_rule_in_invoke() = runTest {
        //GIVEN
        prefixRepository.addPrefixRule("57", PrefixRule.RuleType.BLOCK).getOrThrow()

        //WHEN
        val rules = getAllPrefixRulesUseCase().first()
        val normalized = normalizePhoneNumberUseCase("+57123456789", null)
        val match = matchPrefixRuleUseCase(rules, normalized)

        //THEN
        assertTrue("expected Blocked but was $match", match is MatchResult.Blocked)
        assertEquals("57", (match as MatchResult.Blocked).prefix)
    }

    @Test
    fun should_return_allowed_match_when_number_matches_allow_rule_in_invoke() = runTest {
        //GIVEN
        prefixRepository.addPrefixRule("1", PrefixRule.RuleType.ALLOW).getOrThrow()

        //WHEN
        val rules = getAllPrefixRulesUseCase().first()
        val normalized = normalizePhoneNumberUseCase("+1234567890", null)
        val match = matchPrefixRuleUseCase(rules, normalized)

        //THEN
        assertTrue("expected Allowed but was $match", match is MatchResult.Allowed)
        assertEquals("1", (match as MatchResult.Allowed).prefix)
    }

    @Test
    fun should_return_no_match_when_no_rule_matches_in_invoke() = runTest {
        //GIVEN
        prefixRepository.addPrefixRule("57", PrefixRule.RuleType.BLOCK).getOrThrow()

        //WHEN
        val rules = getAllPrefixRulesUseCase().first()
        val normalized = normalizePhoneNumberUseCase("+9999999999", null)
        val match = matchPrefixRuleUseCase(rules, normalized)

        //THEN
        assertTrue("expected NoMatch but was $match", match is MatchResult.NoMatch)
    }

    @Test
    fun should_strip_country_code_when_present_in_invoke() = runTest {
        //WHEN
        val normalized = normalizePhoneNumberUseCase("+57123456789", "57")

        //THEN
        assertEquals("123456789", normalized)
    }

    @Test
    fun should_keep_digits_only_when_country_code_absent_in_invoke() = runTest {
        //WHEN
        val normalized = normalizePhoneNumberUseCase("+1 (555) 123-4567", null)

        //THEN
        assertEquals("15551234567", normalized)
    }

    @Test
    fun should_pick_longest_matching_block_prefix_when_multiple_rules_in_invoke() = runTest {
        //GIVEN
        prefixRepository.addPrefixRule("5", PrefixRule.RuleType.ALLOW).getOrThrow()
        prefixRepository.addPrefixRule("57", PrefixRule.RuleType.BLOCK).getOrThrow()

        //WHEN
        val rules = getAllPrefixRulesUseCase().first()
        val normalized = normalizePhoneNumberUseCase("+57123456789", null)
        val match = matchPrefixRuleUseCase(rules, normalized)

        //THEN
        assertTrue("expected Blocked(57) but was $match", match is MatchResult.Blocked)
        assertEquals("57", (match as MatchResult.Blocked).prefix)
    }

    @Test
    fun should_persist_blocked_call_to_repository_when_invoked() = runTest {
        //GIVEN
        val phoneNumber = "+57123456789"
        val matchedPrefix = "57"

        //WHEN
        insertBlockedCallUseCase(phoneNumber, BlockType.Prefix(matchedPrefix))
        advanceUntilIdle()

        //THEN
        blockedCallRepository.getAllBlockedCalls().test {
            val calls = awaitItem()
            assertEquals(1, calls.size)
            assertEquals(phoneNumber, calls.first().phoneNumber)
            assertEquals(matchedPrefix, calls.first().matchedPrefix)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_persist_allowed_call_to_repository_when_invoked() = runTest {
        //GIVEN
        val phoneNumber = "+9999999999"

        //WHEN
        insertAllowedCallUseCase(phoneNumber).getOrThrow()
        advanceUntilIdle()

        //THEN
        allowedCallRepository.getAllAllowedCalls().test {
            val calls = awaitItem()
            assertEquals(1, calls.size)
            assertEquals(phoneNumber, calls.first().phoneNumber)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_return_true_when_contact_exists_with_full_phone_number_in_invoke() = runTest {
        //GIVEN
        val phoneNumber = "+56911111111"
        insertContact(phoneNumber)

        //WHEN
        val isContact = contactsRepository.isContact(phoneNumber)

        //THEN
        assertTrue(
            "expected isContact to return true for stored contact $phoneNumber",
            isContact
        )
    }

    @Test
    fun should_return_false_when_no_contact_exists_in_invoke() = runTest {
        //GIVEN
        val phoneNumber = "+56922222222"

        //WHEN
        val isContact = contactsRepository.isContact(phoneNumber)

        //THEN
        assertFalse(
            "expected isContact to return false for non-existing contact $phoneNumber",
            isContact
        )
    }
}
