package cl.blipblipcode.prefixsapp.ui.history

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.provider.ContactsContract
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistoryScreenIntentTest {

    private val context: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun should_set_action_insert_and_uri_and_phone_extra_when_phone_provided_in_invoke() {
        //GIVEN
        val phoneNumber = "+573001234567"
        val capturedIntents = mutableListOf<Intent>()
        val capturingContext = object : ContextWrapper(context) {
            override fun startActivity(intent: Intent) {
                capturedIntents.add(intent)
            }
        }

        //WHEN
        launchSaveContactIntent(capturingContext, phoneNumber)

        //THEN
        assertEquals(1, capturedIntents.size)
        val intent = capturedIntents.first()
        assertEquals(Intent.ACTION_INSERT, intent.action)
        assertEquals(ContactsContract.Contacts.CONTENT_URI, intent.data)
        assertEquals(phoneNumber, intent.getStringExtra(ContactsContract.Intents.Insert.PHONE))
    }

    @Test
    fun should_add_flag_activity_new_task_in_invoke() {
        //GIVEN
        val phoneNumber = "+573001234567"
        val capturedIntents = mutableListOf<Intent>()
        val capturingContext = object : ContextWrapper(context) {
            override fun startActivity(intent: Intent) {
                capturedIntents.add(intent)
            }
        }

        //WHEN
        launchSaveContactIntent(capturingContext, phoneNumber)

        //THEN
        assertEquals(1, capturedIntents.size)
        val intent = capturedIntents.first()
        assertTrue(
            "expected FLAG_ACTIVITY_NEW_TASK to be set",
            (intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK) == Intent.FLAG_ACTIVITY_NEW_TASK
        )
    }

    @Test
    fun should_return_failure_when_context_throws_activity_not_found_in_invoke() {
        //GIVEN
        val phoneNumber = "+573001234567"
        val throwingContext = object : ContextWrapper(context) {
            override fun startActivity(intent: Intent) {
                throw ActivityNotFoundException("test")
            }
        }

        //WHEN
        val result = launchSaveContactIntent(throwingContext, phoneNumber)

        //THEN
        assertTrue("expected Result.failure", result.isFailure)
        assertTrue(
            "expected ActivityNotFoundException",
            result.exceptionOrNull() is ActivityNotFoundException
        )
    }
}
