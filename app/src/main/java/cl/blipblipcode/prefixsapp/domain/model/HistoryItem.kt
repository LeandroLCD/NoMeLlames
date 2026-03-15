package cl.blipblipcode.prefixsapp.domain.model

data class HistoryItem(
    val id: Long,
    val phoneNumber: String,
    val timestamp: Long,
    val type: CallType,
    val matchedPrefix: String? = null
) {
    enum class CallType {
        BLOCKED,
        ALLOWED
    }
    
    val isBlocked: Boolean get() = type == CallType.BLOCKED
}

