package cl.blipblipcode.prefixsapp.data.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import cl.blipblipcode.prefixsapp.domain.repositories.BlockingPreferencesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockingPreferencesRepositoryImpl @Inject constructor(
    dispatcher: CoroutineDispatcher,
    private val dataStore: DataStore<Preferences>
) : BaseRepository(dispatcher), BlockingPreferencesRepository {

    companion object {
        private val KEY_BLOCK_PRIVATE_NUMBERS = booleanPreferencesKey("block_private_numbers")
        private val KEY_BLOCK_NON_CONTACTS = booleanPreferencesKey("block_non_contacts")
        private const val DEFAULT_BLOCK_PRIVATE_NUMBERS = false
        private const val DEFAULT_BLOCK_NON_CONTACTS = false
    }

    override fun getBlockPrivateNumbers(): Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[KEY_BLOCK_PRIVATE_NUMBERS] ?: DEFAULT_BLOCK_PRIVATE_NUMBERS
        }

    override fun getBlockNonContacts(): Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[KEY_BLOCK_NON_CONTACTS] ?: DEFAULT_BLOCK_NON_CONTACTS
        }

    override suspend fun setBlockPrivateNumbers(value: Boolean): Result<Unit> = makeSuspendCall {
        dataStore.edit { prefs ->
            prefs[KEY_BLOCK_PRIVATE_NUMBERS] = value
        }
    }

    override suspend fun setBlockNonContacts(value: Boolean): Result<Unit> = makeSuspendCall {
        dataStore.edit { prefs ->
            prefs[KEY_BLOCK_NON_CONTACTS] = value
        }
    }
}