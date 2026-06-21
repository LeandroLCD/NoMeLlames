package cl.blipblipcode.prefixsapp.data.repositories

import cl.blipblipcode.prefixsapp.data.local.dao.BlockedCallDao
import cl.blipblipcode.prefixsapp.data.local.entities.BlockedCallEntity
import cl.blipblipcode.prefixsapp.domain.model.BlockType
import cl.blipblipcode.prefixsapp.domain.model.BlockedCall
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
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
        blockType: BlockType
    ): Result<Unit> {
        return makeSuspendCall {
            blockedCallDao.insertBlockedCall(
                BlockedCallEntity.fromDomain(
                    BlockedCall(
                        id = 0,
                        phoneNumber = phoneNumber,
                        matchedPrefix = blockType.getPrefix.orEmpty(),
                        blockType = blockType,
                        timestamp = System.currentTimeMillis(),
                    )
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