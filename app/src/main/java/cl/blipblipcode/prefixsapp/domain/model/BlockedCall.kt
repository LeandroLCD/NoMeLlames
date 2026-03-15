package cl.blipblipcode.prefixsapp.domain.model

data class BlockedCall(
    val id: Long,
    val phoneNumber: String,
    val matchedPrefix: String,
    val timestamp: Long,
    val seen: Boolean = false
)
