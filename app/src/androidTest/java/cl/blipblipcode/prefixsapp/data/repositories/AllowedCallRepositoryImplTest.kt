package cl.blipblipcode.prefixsapp.data.repositories

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.data.local.dao.AllowedCallDao
import cl.blipblipcode.prefixsapp.data.local.entities.AllowedCallEntity
import cl.blipblipcode.prefixsapp.domain.repositories.AllowedCallRepository
import cl.blipblipcode.prefixsapp.rules.MainDispatcherRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AllowedCallRepositoryImplTest {

    @get:Rule(order = 0)
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: AllowedCallRepository

    @Inject
    lateinit var allowedCallDao: AllowedCallDao

    @Before
    fun setUp() {
        hiltRule.inject()
        runBlocking { allowedCallDao.deleteAllAllowedCalls() }
    }

    @Test
    fun should_return_success_and_persist_entity_when_dao_inserts_in_invoke() = runTest {
        //GIVEN
        val phoneNumber = "+573001234567"

        //WHEN
        val result = repository.insertAllowedCall(phoneNumber)

        //THEN
        assertTrue(result.isSuccess)
        val calls = repository.getAllAllowedCalls().first()
        assertEquals(1, calls.size)
        assertEquals(phoneNumber, calls.first().phoneNumber)
    }

    @Test
    fun should_auto_generate_id_when_dao_inserts_in_invoke() = runTest {
        //GIVEN
        val phoneNumber = "+573001234567"

        //WHEN
        repository.insertAllowedCall(phoneNumber)

        //THEN
        val calls = repository.getAllAllowedCalls().first()
        assertNotEquals(0L, calls.first().id)
    }

    @Test
    fun should_assign_current_timestamp_when_dao_inserts_in_invoke() = runTest {
        //GIVEN
        val before = System.currentTimeMillis()

        //WHEN
        repository.insertAllowedCall("+573001234567")
        val after = System.currentTimeMillis()

        //THEN
        val calls = repository.getAllAllowedCalls().first()
        val timestamp = calls.first().timestamp
        assertTrue(
            "timestamp=$timestamp not in [$before, $after]",
            timestamp in before..after
        )
    }

    @Test
    fun should_persist_multiple_calls_when_dao_inserts_each_in_invoke() = runTest {
        //GIVEN
        val phones = listOf("+573001234567", "+573001234568", "+573001234569")

        //WHEN
        phones.forEach { repository.insertAllowedCall(it) }

        //THEN
        val calls = repository.getAllAllowedCalls().first()
        assertEquals(3, calls.size)
        assertEquals(phones.toSet(), calls.map { it.phoneNumber }.toSet())
    }

    @Test
    fun should_remove_all_rows_when_dao_deletes_all_in_invoke() = runTest {
        //GIVEN
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234567"))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234568"))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234569"))

        //WHEN
        val result = repository.deleteAllAllowedCalls()

        //THEN
        assertTrue(result.isSuccess)
        assertTrue(repository.getAllAllowedCalls().first().isEmpty())
    }

    @Test
    fun should_return_success_when_database_is_empty_in_invoke() = runTest {
        //WHEN
        val result = repository.deleteAllAllowedCalls()

        //THEN
        assertTrue(result.isSuccess)
        assertTrue(repository.getAllAllowedCalls().first().isEmpty())
    }

    @Test
    fun should_reset_count_to_zero_when_dao_deletes_all_in_invoke() = runTest {
        //GIVEN
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234567"))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234568"))

        //WHEN
        repository.deleteAllAllowedCalls()

        //THEN
        assertEquals(0, repository.getAllowedCallsCount().first())
    }

    @Test
    fun should_remove_only_matching_row_when_dao_deletes_by_id_in_invoke() = runTest {
        //GIVEN
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234567"))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234568"))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234569"))
        val before = repository.getAllAllowedCalls().first()
        val targetId = before.first { it.phoneNumber == "+573001234568" }.id

        //WHEN
        val result = repository.deleteAllowedCall(targetId)

        //THEN
        assertTrue(result.isSuccess)
        val after = repository.getAllAllowedCalls().first()
        assertEquals(2, after.size)
        assertTrue(after.none { it.id == targetId })
    }

    @Test
    fun should_keep_other_rows_untouched_when_dao_deletes_by_id_in_invoke() = runTest {
        //GIVEN
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234567"))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234568"))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234569"))
        val before = repository.getAllAllowedCalls().first()
        val targetId = before.first { it.phoneNumber == "+573001234568" }.id
        val expectedPhones = before.filter { it.id != targetId }.map { it.phoneNumber }.toSet()

        //WHEN
        repository.deleteAllowedCall(targetId)

        //THEN
        val after = repository.getAllAllowedCalls().first()
        assertEquals(expectedPhones, after.map { it.phoneNumber }.toSet())
    }

    @Test
    fun should_decrement_count_by_one_when_dao_deletes_by_id_in_invoke() = runTest {
        //GIVEN
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234567"))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234568"))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234569"))
        val targetId = repository.getAllAllowedCalls().first()
            .first { it.phoneNumber == "+573001234568" }.id

        //WHEN
        repository.deleteAllowedCall(targetId)

        //THEN
        assertEquals(2, repository.getAllowedCallsCount().first())
    }

    @Test
    fun should_return_success_when_id_does_not_exist_in_invoke() = runTest {
        //GIVEN
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234567"))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+573001234568"))

        //WHEN
        val result = repository.deleteAllowedCall(999L)

        //THEN
        assertTrue(result.isSuccess)
        assertEquals(2, repository.getAllAllowedCalls().first().size)
    }

    @Test
    fun should_emit_empty_list_when_database_is_empty_in_invoke() = runTest {
        //WHEN
        repository.getAllAllowedCalls().test {
            //THEN
            assertEquals(emptyList<Any>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_list_ordered_by_timestamp_desc_when_database_has_rows_in_invoke() = runTest {
        //GIVEN
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "a", timestamp = 100L))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "b", timestamp = 300L))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "c", timestamp = 200L))

        //WHEN
        repository.getAllAllowedCalls().test {
            //THEN
            val calls = awaitItem()
            assertEquals(listOf("b", "c", "a"), calls.map { it.phoneNumber })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_updated_list_when_new_row_inserted_after_subscription_in_invoke() = runTest {
        //GIVEN
        repository.insertAllowedCall("+573001234567")

        //WHEN
        repository.getAllAllowedCalls().test {
            assertEquals(1, awaitItem().size)
            repository.insertAllowedCall("+573001234568")
            //THEN
            assertEquals(2, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_map_entity_fields_to_domain_in_get_all_invoke() = runTest {
        //GIVEN
        allowedCallDao.insertAllowedCall(
            AllowedCallEntity(id = 42L, phoneNumber = "+573001234567", timestamp = 1000L)
        )

        //WHEN
        repository.getAllAllowedCalls().test {
            //THEN
            val call = awaitItem().first()
            assertEquals(42L, call.id)
            assertEquals("+573001234567", call.phoneNumber)
            assertEquals(1000L, call.timestamp)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_zero_when_database_is_empty_in_count_invoke() = runTest {
        //WHEN
        repository.getAllowedCallsCount().test {
            //THEN
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_count_matching_rows_when_database_has_rows_in_invoke() = runTest {
        //GIVEN
        repeat(5) {
            allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "+57300123456$it"))
        }

        //WHEN
        repository.getAllowedCallsCount().test {
            //THEN
            assertEquals(5, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_updated_count_when_row_inserted_after_subscription_in_invoke() = runTest {
        //GIVEN
        repository.insertAllowedCall("+573001234567")

        //WHEN
        repository.getAllowedCallsCount().test {
            assertEquals(1, awaitItem())
            repository.insertAllowedCall("+573001234568")
            //THEN
            assertEquals(2, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_updated_count_when_row_deleted_after_subscription_in_invoke() = runTest {
        //GIVEN
        repository.insertAllowedCall("+573001234567")
        repository.insertAllowedCall("+573001234568")
        repository.insertAllowedCall("+573001234569")
        val targetId = repository.getAllAllowedCalls().first()
            .first { it.phoneNumber == "+573001234568" }.id

        //WHEN
        repository.getAllowedCallsCount().test {
            assertEquals(3, awaitItem())
            repository.deleteAllowedCall(targetId)
            //THEN
            assertEquals(2, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_zero_when_all_rows_deleted_after_subscription_in_invoke() = runTest {
        //GIVEN
        repository.insertAllowedCall("+573001234567")
        repository.insertAllowedCall("+573001234568")

        //WHEN
        repository.getAllowedCallsCount().test {
            assertEquals(2, awaitItem())
            repository.deleteAllAllowedCalls()
            //THEN
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_empty_list_when_database_is_empty_in_recent_invoke() = runTest {
        //WHEN
        repository.getRecentAllowedCalls(5).test {
            //THEN
            assertEquals(emptyList<Any>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_respect_limit_parameter_when_database_has_more_rows_in_invoke() = runTest {
        //GIVEN
        repeat(10) { i ->
            allowedCallDao.insertAllowedCall(
                AllowedCallEntity(phoneNumber = "+5730012345$i", timestamp = i.toLong())
            )
        }

        //WHEN
        repository.getRecentAllowedCalls(3).test {
            //THEN
            assertEquals(3, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_order_by_timestamp_desc_in_recent_invoke() = runTest {
        //GIVEN
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "a", timestamp = 100L))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "b", timestamp = 300L))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "c", timestamp = 200L))

        //WHEN
        repository.getRecentAllowedCalls(5).test {
            //THEN
            assertEquals(listOf("b", "c", "a"), awaitItem().map { it.phoneNumber })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_most_recent_n_calls_in_recent_invoke() = runTest {
        //GIVEN
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "a", timestamp = 1L))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "b", timestamp = 2L))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "c", timestamp = 3L))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "d", timestamp = 4L))
        allowedCallDao.insertAllowedCall(AllowedCallEntity(phoneNumber = "e", timestamp = 5L))

        //WHEN
        repository.getRecentAllowedCalls(2).test {
            //THEN
            val calls = awaitItem()
            assertEquals(listOf(5L, 4L), calls.map { it.timestamp })
            assertEquals(listOf("e", "d"), calls.map { it.phoneNumber })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_map_entity_fields_to_domain_in_recent_invoke() = runTest {
        //GIVEN
        allowedCallDao.insertAllowedCall(
            AllowedCallEntity(id = 7L, phoneNumber = "+573001234567", timestamp = 1234L)
        )

        //WHEN
        repository.getRecentAllowedCalls(5).test {
            //THEN
            val call = awaitItem().first()
            assertEquals(7L, call.id)
            assertEquals("+573001234567", call.phoneNumber)
            assertEquals(1234L, call.timestamp)
            cancelAndIgnoreRemainingEvents()
        }
    }
}