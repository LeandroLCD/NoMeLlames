package cl.blipblipcode.prefixsapp.data.repository

import cl.blipblipcode.prefixsapp.data.local.dao.AllowedCallDao
import cl.blipblipcode.prefixsapp.data.local.entities.AllowedCallEntity
import cl.blipblipcode.prefixsapp.domain.model.AllowedCall
import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AllowedCallRepositoryImpl @Inject constructor(
    dispatcher: CoroutineDispatcher,
    private val allowedCallDao: AllowedCallDao
) : BaseRepository(dispatcher), AllowedCallRepository {

    override fun getAllAllowedCalls(): Flow<List<AllowedCall>> {
        return allowedCallDao.getAllAllowedCalls().map { entities ->
            entities.map { entity -> entity.mapToDomain() }
        }
    }

    override fun getAllowedCallsCount(): Flow<Int> {
        return allowedCallDao.getAllowedCallsCount()
    }

    override suspend fun insertAllowedCall(phoneNumber: String): Result<Unit> {
        return makeSuspendCall {
            allowedCallDao.insertAllowedCall(
                AllowedCallEntity(phoneNumber = phoneNumber)
            )
        }
    }

    override suspend fun deleteAllAllowedCalls(): Result<Unit> {
        return makeSuspendCall {
            allowedCallDao.deleteAllAllowedCalls()
        }
    }

    override suspend fun deleteAllowedCall(id: Long): Result<Unit> {
        return makeSuspendCall {
            allowedCallDao.deleteAllowedCall(id)
        }
    }

    override fun getRecentAllowedCalls(limit: Int): Flow<List<AllowedCall>> {
        return allowedCallDao.getRecentAllowedCalls(limit).map { entities ->
            entities.map { entity -> entity.mapToDomain() }
        }
    }
}

