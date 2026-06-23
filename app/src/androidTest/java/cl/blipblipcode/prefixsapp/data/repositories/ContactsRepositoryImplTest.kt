package cl.blipblipcode.prefixsapp.data.repositories

import android.Manifest
import android.app.Instrumentation
import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import cl.blipblipcode.prefixsapp.domain.repositories.ContactsRepository
import cl.blipblipcode.prefixsapp.rules.MainDispatcherRule
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
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
class ContactsRepositoryImplTest {

    @get:Rule(order = 0)
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: ContactsRepository

    @Inject
    @ApplicationContext
    lateinit var context: Context

    private val instrumentation: Instrumentation
        get() = InstrumentationRegistry.getInstrumentation()

    private val createdRawContactIds = mutableListOf<Long>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        runBlocking {
            createdRawContactIds.forEach { id ->
                context.contentResolver.delete(
                    ContactsContract.RawContacts.CONTENT_URI,
                    "${ContactsContract.RawContacts._ID} = ?",
                    arrayOf(id.toString())
                )
            }
            createdRawContactIds.clear()
        }
    }

    private fun grantContactsPermission() {
        instrumentation.uiAutomation.grantRuntimePermission(
            context.packageName,
            Manifest.permission.READ_CONTACTS
        )
        instrumentation.uiAutomation.grantRuntimePermission(
            context.packageName,
            Manifest.permission.WRITE_CONTACTS
        )
    }

    private fun insertContact(phoneNumber: String): Long {
        grantContactsPermission()
        val rawContactValues = ContentValues().apply {
            put(ContactsContract.RawContacts.ACCOUNT_TYPE, "com.google")
            put(ContactsContract.RawContacts.ACCOUNT_NAME, "test_account")
        }
        val rawContactUri = context.contentResolver.insert(
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
        context.contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneValues)

        createdRawContactIds.add(rawContactId)
        return rawContactId
    }

    @Test
    fun should_return_false_when_phone_number_is_empty_in_invoke() = runTest {
        //WHEN
        val result = repository.isContact("")

        //THEN
        assertFalse(result)
    }

    @Test
    fun should_return_false_when_phone_number_is_blank_in_invoke() = runTest {
        //WHEN
        val result = repository.isContact("   ")

        //THEN
        assertFalse(result)
    }

    @Test
    fun should_return_true_when_contact_exists_and_permission_granted_in_invoke() = runTest {
        //GIVEN
        grantContactsPermission()
        val phoneNumber = "+573001234567"
        insertContact(phoneNumber)

        //WHEN
        val result = repository.isContact(phoneNumber)

        //THEN
        assertTrue(result)
    }

    @Test
    fun should_return_false_when_contact_does_not_exist_and_permission_granted_in_invoke() = runTest {
        //GIVEN
        grantContactsPermission()

        //WHEN
        val result = repository.isContact("+573009999999")

        //THEN
        assertFalse(result)
    }

    @Test
    fun should_match_phone_number_by_exact_value_when_contact_exists_in_invoke() = runTest {
        //GIVEN
        insertContact("+573001234567")

        //WHEN
        val result = repository.isContact("+573001234568")

        //THEN
        assertFalse(result)
    }

    @Test
    fun should_report_current_permission_state_granted_in_has_contacts_permission() {
        //GIVEN
        grantContactsPermission()

        //WHEN
        val granted = repository.hasContactsPermission()

        //THEN
        assertTrue(granted)
    }
}
