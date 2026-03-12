package dev.andresfelipecaicedo.nomellames.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.andresfelipecaicedo.nomellames.domain.model.BlockedCall
import dev.andresfelipecaicedo.nomellames.domain.model.maper.DomainMapper

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
