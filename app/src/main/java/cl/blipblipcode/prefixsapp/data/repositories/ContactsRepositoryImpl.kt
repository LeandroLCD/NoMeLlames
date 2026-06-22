package cl.blipblipcode.prefixsapp.data.repositories

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import cl.blipblipcode.prefixsapp.domain.repositories.BlockingPreferencesRepository
import cl.blipblipcode.prefixsapp.domain.repositories.ContactsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactsRepositoryImpl @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val blockingPreferencesRepository: BlockingPreferencesRepository,
    @ApplicationContext private val context: Context,
    private val scope: CoroutineScope
) : ContactsRepository {

    override fun hasContactsPermission(): Boolean  {
       val has = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED
        if (!has){
            scope.launch {
                blockingPreferencesRepository.setBlockNonContacts(false)
            }
        }
        return has
    }
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
            )?.use { cursor ->
                cursor.count > 0 
            } ?: false
        } catch (e: SecurityException) {
            Timber.w(e, "READ_CONTACTS permission missing while querying contacts")
            false
        }
    }

    override suspend fun getContactName(phoneNumber: String): String? = withContext(dispatcher) {
        if (phoneNumber.isBlank()) return@withContext null
        if (!hasContactsPermission()) return@withContext null
        try {
            val uri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber)
            )
            context.contentResolver.query(
                uri,
                arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
                null, null, null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME))
                } else null
            }
        } catch (e: SecurityException) {
            Timber.w(e, "READ_CONTACTS permission missing while querying contacts")
            null
        }
    }
}