package dev.andresfelipecaicedo.nomellames.domain.repositories

import dev.andresfelipecaicedo.nomellames.domain.model.AllowedCall
import kotlinx.coroutines.flow.Flow

interface AllowedCallRepository {
    fun getAllAllowedCalls(): Flow<List<AllowedCall>>
    fun getAllowedCallsCount(): Flow<Int>
    suspend fun insertAllowedCall(phoneNumber: String): Result<Unit>
    suspend fun deleteAllAllowedCalls(): Result<Unit>
    suspend fun deleteAllowedCall(id: Long): Result<Unit>
    fun getRecentAllowedCalls(limit: Int): Flow<List<AllowedCall>>
}

