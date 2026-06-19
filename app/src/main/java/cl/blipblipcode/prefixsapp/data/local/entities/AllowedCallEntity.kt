package cl.blipblipcode.prefixsapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import cl.blipblipcode.prefixsapp.domain.model.AllowedCall
import cl.blipblipcode.prefixsapp.domain.model.maper.DomainMapper

@Entity(tableName = "allowed_calls")
data class AllowedCallEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val timestamp: Long = System.currentTimeMillis()
): DomainMapper<AllowedCall> {
    override fun mapToDomain(): AllowedCall = AllowedCall(
        id = id,
        phoneNumber = phoneNumber,
        timestamp = timestamp
    )
}

