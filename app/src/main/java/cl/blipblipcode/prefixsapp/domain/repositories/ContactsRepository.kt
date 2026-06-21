package cl.blipblipcode.prefixsapp.domain.repositories

interface ContactsRepository {
    suspend fun isContact(phoneNumber: String): Boolean
    fun hasContactsPermission(): Boolean
}