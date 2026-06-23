package cl.blipblipcode.prefixsapp.domain.repositories

interface ContactsRepository {
    suspend fun isContact(phoneNumber: String): Boolean
    suspend fun getContactName(phoneNumber: String): String?
    fun hasContactsPermission(): Boolean
}