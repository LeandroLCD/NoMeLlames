package cl.blipblipcode.prefixsapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.blipblipcode.prefixsapp.domain.model.BlockType
import cl.blipblipcode.prefixsapp.domain.model.BlockedCall
import cl.blipblipcode.prefixsapp.domain.model.maper.DomainMapper


@Entity(tableName = "blocked_calls")
data class BlockedCallEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val matchedPrefix: String,
    @ColumnInfo(defaultValue = "Prefix")
    val blockType: String = BLOCK_TYPE_ALLOW,
    val timestamp: Long = System.currentTimeMillis(),
    val seen: Boolean = false
) : DomainMapper<BlockedCall> {

    override fun mapToDomain(): BlockedCall = BlockedCall(
        id = id,
        phoneNumber = phoneNumber,
        matchedPrefix = matchedPrefix,
        blockType = parseBlockType(blockType, matchedPrefix),
        timestamp = timestamp,
        seen = seen
    )

    companion object {
        const val BLOCK_TYPE_PREFIX = "Prefix"
        const val BLOCK_TYPE_ALLOW_PREFIX = "AllowPrefix"
        const val BLOCK_TYPE_ALLOW = "Allow"
        const val BLOCK_TYPE_PRIVATE_NUMBER = "PrivateNumber"
        const val BLOCK_TYPE_NON_CONTACT = "NonContact"

        fun fromDomain(blockedCall: BlockedCall): BlockedCallEntity = BlockedCallEntity(
            id = blockedCall.id,
            phoneNumber = blockedCall.phoneNumber,
            matchedPrefix = (blockedCall.blockType as? BlockType.Prefix)?.prefix.orEmpty(),
            blockType = blockedCall.blockType.toStorageKey(),
            timestamp = blockedCall.timestamp,
            seen = blockedCall.seen
        )

        private fun parseBlockType(raw: String, matchedPrefix: String): BlockType = when (raw) {
            BLOCK_TYPE_PRIVATE_NUMBER -> BlockType.PrivateNumber
            BLOCK_TYPE_NON_CONTACT -> BlockType.NonContact
            BLOCK_TYPE_PREFIX -> BlockType.Prefix(matchedPrefix)
            BLOCK_TYPE_ALLOW_PREFIX -> BlockType.AllowPrefix(matchedPrefix)
            else -> BlockType.Allow
        }

        private fun BlockType.toStorageKey(): String = when (this) {
            is BlockType.Prefix -> BLOCK_TYPE_PREFIX
            BlockType.PrivateNumber -> BLOCK_TYPE_PRIVATE_NUMBER
            BlockType.NonContact -> BLOCK_TYPE_NON_CONTACT
            is BlockType.AllowPrefix -> BLOCK_TYPE_ALLOW_PREFIX
            BlockType.Allow -> BLOCK_TYPE_ALLOW
        }
    }
}