package dev.andresfelipecaicedo.nomellames.data.repository

import dev.andresfelipecaicedo.nomellames.data.local.dao.BlockedCallDao
import dev.andresfelipecaicedo.nomellames.data.local.entities.BlockedCallEntity
import dev.andresfelipecaicedo.nomellames.domain.model.BlockedCall
import dev.andresfelipecaicedo.nomellames.domain.repositories.BlockedCallRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockedCallRepositoryImpl @Inject constructor(
    dispatcher: CoroutineDispatcher,
    private val blockedCallDao: BlockedCallDao
) : BaseRepository(dispatcher), BlockedCallRepository {

    override fun getAllBlockedCalls(): Flow<List<BlockedCall>> {
        return blockedCallDao.getAllBlockedCalls().map { entities ->
            entities.map { entity ->
                entity.mapToDomain()
            }
        }
    }

    override suspend fun insertBlockedCall(
        phoneNumber: String,
        matchedPrefix: String
    ): Result<Unit> {
        return makeSuspendCall {
            blockedCallDao.insertBlockedCall(
                BlockedCallEntity(
                    phoneNumber = phoneNumber,
                    matchedPrefix = matchedPrefix
                )
            )
        }
    }

    override suspend fun deleteAllBlockedCalls(): Result<Unit> {
        return makeSuspendCall {
            blockedCallDao.deleteAllBlockedCalls()
        }
    }

    override suspend fun deleteBlockedCall(id: Long): Result<Unit> {
        return makeSuspendCall {
            blockedCallDao.deleteBlockedCall(id)
        }
    }

    override fun getUnseenCount(): Flow<Int> {
        return blockedCallDao.getUnseenCount()
    }

    override suspend fun markAllAsSeen(): Result<Unit> {
        return makeSuspendCall {
            blockedCallDao.markAllAsSeen()
        }
    }

    override fun getBlockedCallsCount(): Flow<Int> {
        return blockedCallDao.getBlockedCallsCount()
    }

    override fun getRecentBlockedCalls(limit: Int): Flow<List<BlockedCall>> {
        return blockedCallDao.getRecentBlockedCalls(limit).map { entities ->
            entities.map { entity ->
                entity.mapToDomain()
            }
        }
    }
}
