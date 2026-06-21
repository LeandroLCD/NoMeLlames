package cl.blipblipcode.prefixsapp.core.fakes

import cl.blipblipcode.prefixsapp.domain.repositories.ContactsRepository
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class FakeContactsRepository : ContactsRepository {

    private val contacts = AtomicReference<Set<String>>(emptySet())
    private val hasPermission = AtomicBoolean(true)

    override suspend fun isContact(phoneNumber: String): Boolean {
        if (!hasPermission.get()) return false
        val normalized = phoneNumber.filter { it.isDigit() || it == '+' }
        return contacts.get().any { stored ->
            val storedNormalized = stored.filter { it.isDigit() || it == '+' }
            normalized.endsWith(storedNormalized) || storedNormalized.endsWith(normalized)
        }
    }

    override fun hasContactsPermission(): Boolean = hasPermission.get()

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