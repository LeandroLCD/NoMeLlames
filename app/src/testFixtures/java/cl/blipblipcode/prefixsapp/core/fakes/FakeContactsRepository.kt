package cl.blipblipcode.prefixsapp.core.fakes

import cl.blipblipcode.prefixsapp.domain.repositories.ContactsRepository
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class FakeContactsRepository : ContactsRepository {

    private val contacts = AtomicReference<Set<String>>(emptySet())

    private val contactsNames = AtomicReference<Map<String, String>>(emptyMap())
    private val hasPermission = AtomicBoolean(true)

    override suspend fun isContact(phoneNumber: String): Boolean {
        if (!hasPermission.get()) return false
        val normalized = phoneNumber.filter { it.isDigit() || it == '+' }
        return contacts.get().any { stored ->
            val storedNormalized = stored.filter { it.isDigit() || it == '+' }
            normalized.endsWith(storedNormalized) || storedNormalized.endsWith(normalized)
        }
    }

    override suspend fun getContactName(phoneNumber: String): String? {
        if (!hasPermission.get()) return null
        val normalized = phoneNumber.filter { it.isDigit() || it == '+' }
        return contactsNames.get()[normalized]
    }

    override fun hasContactsPermission(): Boolean = hasPermission.get()

    fun setContactNames(names: Map<String, String>) {
        contactsNames.set(names)
    }

    fun setContacts(phoneNumbers: Collection<String>) {
        contacts.set(phoneNumbers.toSet())
    }

    fun clearContacts() {
        contacts.set(emptySet())
    }

    fun setHasContactsPermission(granted: Boolean) {
        hasPermission.set(granted)
    }
}