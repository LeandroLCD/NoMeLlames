package cl.blipblipcode.prefixsapp.data.repositories

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import cl.blipblipcode.prefixsapp.domain.repositories.ContactsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsRepositoryImpl @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context
) : ContactsRepository {

    override fun hasContactsPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED

    override suspend fun isContact(phoneNumber: String): Boolean = withContext(dispatcher) {
        if (phoneNumber.isBlank()) return@withContext false
        if (!hasContactsPermission()) return@withContext false
        try {
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            context.contentResolver.query(
                uri,
                arrayOf(ContactsContract.PhoneLookup._ID),
                null, null, null
            )?.use { cursor -> cursor.count > 0 } ?: false
        } catch (e: SecurityException) {
            Timber.w(e, "READ_CONTACTS permission missing while querying contacts")
            false
        }
    }
}