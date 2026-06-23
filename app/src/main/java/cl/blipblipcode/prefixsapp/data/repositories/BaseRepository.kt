package cl.blipblipcode.prefixsapp.data.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

open class BaseRepository(private val dispatcher: CoroutineDispatcher) {
    suspend fun <T> makeSuspendCall(call: suspend () -> T): Result<T>{
      return  runCatching {
            withContext(dispatcher){
                call()
            }
        }
    }
}