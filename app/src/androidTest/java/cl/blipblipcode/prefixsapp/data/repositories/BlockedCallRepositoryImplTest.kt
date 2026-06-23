package cl.blipblipcode.prefixsapp.data.repositories

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.data.local.dao.BlockedCallDao
import cl.blipblipcode.prefixsapp.data.local.entities.BlockedCallEntity
import cl.blipblipcode.prefixsapp.domain.model.BlockType
import cl.blipblipcode.prefixsapp.domain.model.BlockedCall
import cl.blipblipcode.prefixsapp.domain.repositories.BlockedCallRepository
import cl.blipblipcode.prefixsapp.rules.MainDispatcherRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
class BlockedCallRepositoryImplTest {

    @get:Rule(order = 0)
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: BlockedCallRepository

    @Inject
    lateinit var blockedCallDao: BlockedCallDao

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun should_return_success_and_persist_entity_when_dao_inserts_in_invoke() = runTest {
        //GIVEN
        val phoneNumber = "+573001234567"
        val blockType = BlockType.Prefix("57")

        //WHEN
        val result = repository.insertBlockedCall(phoneNumber, blockType)

        //THEN
        assertTrue(result.isSuccess)
        repository.getAllBlockedCalls().test {
            val calls = awaitItem()
            assertEquals(1, calls.size)
            assertEquals(phoneNumber, calls.first().phoneNumber)
            assertEquals(blockType, calls.first().blockType)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_auto_generate_id_when_dao_inserts_in_invoke() = runTest {
        //GIVEN
        val phoneNumber = "+573001234567"
        val blockType = BlockType.Prefix("57")

        //WHEN
        repository.insertBlockedCall(phoneNumber, blockType)

        //THEN
        repository.getAllBlockedCalls().test {
            val calls = awaitItem()
            assertNotEquals(0L, calls.first().id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_assign_current_timestamp_when_dao_inserts_in_invoke() = runTest {
        //GIVEN
        val before = System.currentTimeMillis()

        //WHEN
        repository.insertBlockedCall("+573001234567", BlockType.Prefix("57"))
        val after = System.currentTimeMillis()

        //THEN
        repository.getAllBlockedCalls().test {
            val timestamp = awaitItem().first().timestamp
            assertTrue(
                "timestamp=$timestamp not in [$before, $after]",
                timestamp in before..after
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_store_matched_prefix_when_block_type_is_prefix_in_invoke() = runTest {
        //GIVEN
        val phoneNumber = "+573001234567"
        val matchedPrefix = "57"

        //WHEN
        repository.insertBlockedCall(phoneNumber, BlockType.Prefix(matchedPrefix))

        //THEN
        repository.getAllBlockedCalls().test {
            val call = awaitItem().first()
            assertEquals(matchedPrefix, call.matchedPrefix)
            assertEquals(BlockType.Prefix(matchedPrefix), call.blockType)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_store_private_number_block_type_in_invoke() = runTest {
        //GIVEN
        val phoneNumber = "+573001234567"

        //WHEN
        repository.insertBlockedCall(phoneNumber, BlockType.PrivateNumber)

        //THEN
        repository.getAllBlockedCalls().test {
            val call = awaitItem().first()
            assertEquals(BlockType.PrivateNumber, call.blockType)
            assertEquals("", call.matchedPrefix)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_store_non_contact_block_type_in_invoke() = runTest {
        //GIVEN
        val phoneNumber = "+573001234567"

        //WHEN
        repository.insertBlockedCall(phoneNumber, BlockType.NonContact)

        //THEN
        repository.getAllBlockedCalls().test {
            val call = awaitItem().first()
            assertEquals(BlockType.NonContact, call.blockType)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_persist_multiple_calls_when_dao_inserts_each_in_invoke() = runTest {
        //GIVEN
        val phones = listOf("+573001234567", "+573001234568", "+573001234569")
        val blockType = BlockType.Prefix("57")

        //WHEN
        phones.forEach { repository.insertBlockedCall(it, blockType) }

        //THEN
        repository.getAllBlockedCalls().test {
            val calls = awaitItem()
            assertEquals(3, calls.size)
            assertEquals(phones.toSet(), calls.map { it.phoneNumber }.toSet())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_remove_all_rows_when_dao_deletes_all_in_invoke() = runTest {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234567", matchedPrefix = "57")
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234568", matchedPrefix = "57")
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234569", matchedPrefix = "57")
        )

        //WHEN
        val result = repository.deleteAllBlockedCalls()

        //THEN
        assertTrue(result.isSuccess)
        assertTrue(repository.getAllBlockedCalls().first().isEmpty())
    }

    @Test
    fun should_return_success_when_database_is_empty_in_invoke() = runTest {
        //WHEN
        val result = repository.deleteAllBlockedCalls()

        //THEN
        assertTrue(result.isSuccess)
        assertTrue(repository.getAllBlockedCalls().first().isEmpty())
    }

    @Test
    fun should_reset_count_to_zero_when_dao_deletes_all_in_invoke() = runTest {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234567", matchedPrefix = "57")
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234568", matchedPrefix = "57")
        )

        //WHEN
        repository.deleteAllBlockedCalls()

        //THEN
        repository.getBlockedCallsCount().test {
            val calls = awaitItem()
            assertEquals(0, calls)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_remove_only_matching_row_when_dao_deletes_by_id_in_invoke() = runTest {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234567", matchedPrefix = "57")
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234568", matchedPrefix = "57")
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234569", matchedPrefix = "57")
        )
        val before = repository.getAllBlockedCalls().first()
        val targetId = before.first { it.phoneNumber == "+573001234568" }.id

        //WHEN
        val result = repository.deleteBlockedCall(targetId)

        //THEN
        assertTrue(result.isSuccess)
        repository.getAllBlockedCalls().test {
            val after = awaitItem()
            assertEquals(2, after.size)
            assertTrue(after.none { it.id == targetId })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_decrement_count_by_one_when_dao_deletes_by_id_in_invoke() = runTest {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234567", matchedPrefix = "57")
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234568", matchedPrefix = "57")
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234569", matchedPrefix = "57")
        )
        val targetId = repository.getAllBlockedCalls().first()
            .first { it.phoneNumber == "+573001234568" }.id

        //WHEN
        repository.deleteBlockedCall(targetId)

        //THEN
        repository.getBlockedCallsCount().test {
            assertEquals(2, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_return_success_when_id_does_not_exist_in_invoke() = runTest {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234567", matchedPrefix = "57")
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234568", matchedPrefix = "57")
        )

        //WHEN
        val result = repository.deleteBlockedCall(999L)

        //THEN
        assertTrue(result.isSuccess)
        repository.getBlockedCallsCount().test {
            assertEquals(2, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_empty_list_when_database_is_empty_in_invoke() = runTest {
        //WHEN
        repository.getAllBlockedCalls().test {
            //THEN
            assertEquals(emptyList<BlockedCall>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_list_ordered_by_timestamp_desc_when_database_has_rows_in_invoke() = runTest {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "a", matchedPrefix = "a", timestamp = 100L)
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "b", matchedPrefix = "b", timestamp = 300L)
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "c", matchedPrefix = "c", timestamp = 200L)
        )

        //WHEN
        repository.getAllBlockedCalls().test {
            //THEN
            val calls = awaitItem()
            assertEquals(listOf("b", "c", "a"), calls.map { it.phoneNumber })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_updated_list_when_new_row_inserted_after_subscription_in_invoke() = runTest {
        //GIVEN
        repository.insertBlockedCall("+573001234567", BlockType.Prefix("57"))

        //WHEN
        repository.getAllBlockedCalls().test {
            assertEquals(1, awaitItem().size)
            repository.insertBlockedCall("+573001234568", BlockType.Prefix("57"))
            //THEN
            assertEquals(2, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_map_entity_fields_to_domain_in_get_all_invoke() = runTest {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(
                id = 42L,
                phoneNumber = "+573001234567",
                matchedPrefix = "57",
                blockType = "Prefix",
                timestamp = 1000L,
                seen = false
            )
        )

        //WHEN
        repository.getAllBlockedCalls().test {
            //THEN
            val call = awaitItem().first()
            assertEquals(42L, call.id)
            assertEquals("+573001234567", call.phoneNumber)
            assertEquals("57", call.matchedPrefix)
            assertEquals(1000L, call.timestamp)
            assertEquals(false, call.seen)
            assertEquals(BlockType.Prefix("57"), call.blockType)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_zero_when_database_is_empty_in_count_invoke() = runTest {
        //WHEN
        repository.getBlockedCallsCount().test {
            //THEN
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_count_matching_rows_when_database_has_rows_in_invoke() = runTest {
        //GIVEN
        repeat(5) {
            blockedCallDao.insertBlockedCall(
                BlockedCallEntity(phoneNumber = "+57300123456$it", matchedPrefix = "57")
            )
        }

        //WHEN
        repository.getBlockedCallsCount().test {
            //THEN
            assertEquals(5, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_updated_count_when_row_inserted_after_subscription_in_invoke() = runTest {
        //GIVEN
        repository.insertBlockedCall("+573001234567", BlockType.Prefix("57"))

        //WHEN
        repository.getBlockedCallsCount().test {
            assertEquals(1, awaitItem())
            repository.insertBlockedCall("+573001234568", BlockType.Prefix("57"))
            //THEN
            assertEquals(2, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_updated_count_when_row_deleted_after_subscription_in_invoke() = runTest {
        //GIVEN
        repository.insertBlockedCall("+573001234567", BlockType.Prefix("57"))
        repository.insertBlockedCall("+573001234568", BlockType.Prefix("57"))
        repository.insertBlockedCall("+573001234569", BlockType.Prefix("57"))
        val targetId = repository.getAllBlockedCalls().first()
            .first { it.phoneNumber == "+573001234568" }.id

        //WHEN
        repository.getBlockedCallsCount().test {
            assertEquals(3, awaitItem())
            repository.deleteBlockedCall(targetId)
            //THEN
            assertEquals(2, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_zero_when_all_rows_deleted_after_subscription_in_invoke() = runTest {
        //GIVEN
        repository.insertBlockedCall("+573001234567", BlockType.Prefix("57"))
        repository.insertBlockedCall("+573001234568", BlockType.Prefix("57"))

        //WHEN
        repository.getBlockedCallsCount().test {
            assertEquals(2, awaitItem())
            repository.deleteAllBlockedCalls()
            //THEN
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_zero_when_no_unseen_rows_in_invoke() = runTest {
        //WHEN
        repository.getUnseenCount().test {
            //THEN
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_count_of_unseen_rows_when_database_has_rows_in_invoke() = runTest {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234567", matchedPrefix = "57", seen = false)
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234568", matchedPrefix = "57", seen = false)
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234569", matchedPrefix = "57", seen = true)
        )

        //WHEN
        repository.getUnseenCount().test {
            //THEN
            assertEquals(2, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_zero_unseen_when_mark_all_as_seen_invoked() = runTest {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234567", matchedPrefix = "57", seen = false)
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234568", matchedPrefix = "57", seen = false)
        )

        //WHEN
        val result = repository.markAllAsSeen()

        //THEN
        assertTrue(result.isSuccess)
        repository.getUnseenCount().test {
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_updated_unseen_count_when_row_marked_seen_after_subscription_in_invoke() = runTest {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234567", matchedPrefix = "57", seen = false)
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "+573001234568", matchedPrefix = "57", seen = false)
        )

        //WHEN
        repository.getUnseenCount().test {
            assertEquals(2, awaitItem())
            repository.markAllAsSeen()
            //THEN
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_empty_list_when_database_is_empty_in_recent_invoke() = runTest {
        //WHEN
        repository.getRecentBlockedCalls(5).test {
            //THEN
            assertEquals(emptyList<BlockedCall>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_respect_limit_parameter_when_database_has_more_rows_in_invoke() = runTest {
        //GIVEN
        repeat(10) { i ->
            blockedCallDao.insertBlockedCall(
                BlockedCallEntity(
                    phoneNumber = "+5730012345$i",
                    matchedPrefix = "57",
                    timestamp = i.toLong()
                )
            )
        }

        //WHEN
        repository.getRecentBlockedCalls(3).test {
            //THEN
            assertEquals(3, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_order_by_timestamp_desc_in_recent_invoke() = runTest {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "a", matchedPrefix = "a", timestamp = 100L)
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "b", matchedPrefix = "b", timestamp = 300L)
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "c", matchedPrefix = "c", timestamp = 200L)
        )

        //WHEN
        repository.getRecentBlockedCalls(5).test {
            //THEN
            assertEquals(listOf("b", "c", "a"), awaitItem().map { it.phoneNumber })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_most_recent_n_calls_in_recent_invoke() = runTest {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "a", matchedPrefix = "a", timestamp = 1L)
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "b", matchedPrefix = "b", timestamp = 2L)
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "c", matchedPrefix = "c", timestamp = 3L)
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "d", matchedPrefix = "d", timestamp = 4L)
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(phoneNumber = "e", matchedPrefix = "e", timestamp = 5L)
        )

        //WHEN
        repository.getRecentBlockedCalls(2).test {
            //THEN
            val calls = awaitItem()
            assertEquals(listOf(5L, 4L), calls.map { it.timestamp })
            assertEquals(listOf("e", "d"), calls.map { it.phoneNumber })
            cancelAndIgnoreRemainingEvents()
        }
    }
}
