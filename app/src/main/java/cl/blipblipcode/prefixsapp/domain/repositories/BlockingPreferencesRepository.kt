package cl.blipblipcode.prefixsapp.domain.repositories

import kotlinx.coroutines.flow.Flow

interface BlockingPreferencesRepository {
    fun getBlockPrivateNumbers(): Flow<Boolean>
    fun getBlockNonContacts(): Flow<Boolean>

    suspend fun setBlockPrivateNumbers(value: Boolean): Result<Unit>
    suspend fun setBlockNonContacts(value: Boolean): Result<Unit>

}