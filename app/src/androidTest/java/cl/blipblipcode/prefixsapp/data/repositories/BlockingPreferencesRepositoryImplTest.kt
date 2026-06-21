package cl.blipblipcode.prefixsapp.data.repositories

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.domain.repositories.BlockingPreferencesRepository
import cl.blipblipcode.prefixsapp.rules.MainDispatcherRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class BlockingPreferencesRepositoryImplTest {

    @get:Rule(order = 0)
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: BlockingPreferencesRepository

    @Inject
    lateinit var repositoryImpl: BlockingPreferencesRepositoryImpl

    @Before
    fun setUp() {
        hiltRule.inject()
        runBlocking {
            repositoryImpl.setBlockPrivateNumbers(false)
            repositoryImpl.setBlockNonContacts(false)
        }
    }

    @Test
    fun should_emit_false_when_no_value_persisted_for_private_in_invoke() = runTest {
        //WHEN
        val value = repository.getBlockPrivateNumbers().first()

        //THEN
        assertEquals(false, value)
    }

    @Test
    fun should_emit_true_when_block_private_numbers_set_to_true_in_invoke() = runTest {
        //GIVEN
        repositoryImpl.setBlockPrivateNumbers(true).getOrThrow()

        //WHEN
        val value = repository.getBlockPrivateNumbers().first()

        //THEN
        assertEquals(true, value)
    }

    @Test
    fun should_emit_updated_value_when_block_private_numbers_changes_after_subscription_in_invoke() = runTest {
        //GIVEN
        repositoryImpl.setBlockPrivateNumbers(false).getOrThrow()

        //WHEN
        repository.getBlockPrivateNumbers().test {
            assertEquals(false, awaitItem())
            repositoryImpl.setBlockPrivateNumbers(true).getOrThrow()
            //THEN
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_false_when_no_value_persisted_for_non_contacts_in_invoke() = runTest {
        //WHEN
        val value = repository.getBlockNonContacts().first()

        //THEN
        assertEquals(false, value)
    }

    @Test
    fun should_emit_true_when_block_non_contacts_set_to_true_in_invoke() = runTest {
        //GIVEN
        repositoryImpl.setBlockNonContacts(true).getOrThrow()

        //WHEN
        val value = repository.getBlockNonContacts().first()

        //THEN
        assertEquals(true, value)
    }

    @Test
    fun should_emit_updated_value_when_block_non_contacts_changes_after_subscription_in_invoke() = runTest {
        //GIVEN
        repositoryImpl.setBlockNonContacts(false).getOrThrow()

        //WHEN
        repository.getBlockNonContacts().test {
            assertEquals(false, awaitItem())
            repositoryImpl.setBlockNonContacts(true).getOrThrow()
            //THEN
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_return_success_when_set_block_private_numbers_in_invoke() = runTest {
        //WHEN
        val result = repositoryImpl.setBlockPrivateNumbers(true)

        //THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun should_return_success_when_set_block_non_contacts_in_invoke() = runTest {
        //WHEN
        val result = repositoryImpl.setBlockNonContacts(true)

        //THEN
        assertTrue(result.isSuccess)
    }
}