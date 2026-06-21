package cl.blipblipcode.prefixsapp.domain.model

sealed interface BlockType {
    data class Prefix(val prefix: String) : BlockType
    
    data class AllowPrefix(val prefix: String) : BlockType
    data object PrivateNumber : BlockType
    data object NonContact : BlockType
    data object Allow : BlockType

    val getPrefix: String?
        get() = when(this) {
            is Prefix -> prefix
            is AllowPrefix -> prefix
            else -> null
        }
}