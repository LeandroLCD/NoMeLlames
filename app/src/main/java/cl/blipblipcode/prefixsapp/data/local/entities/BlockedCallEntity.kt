package cl.blipblipcode.prefixsapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.blipblipcode.prefixsapp.domain.model.BlockedCall
import cl.blipblipcode.prefixsapp.domain.model.maper.DomainMapper

@Entity(tableName = "blocked_calls")
data class BlockedCallEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val matchedPrefix: String,
    val timestamp: Long = System.currentTimeMillis(),
    val seen: Boolean = false
): DomainMapper<BlockedCall>{
    override fun mapToDomain(): BlockedCall = BlockedCall(
        id = id,
        phoneNumber = phoneNumber,
        matchedPrefix = matchedPrefix,
        timestamp = timestamp,
        seen = seen
    )
}
