package cl.blipblipcode.prefixsapp.domain.useCase.history

import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.core.fakes.FakeAllowedCallRepository
import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockedCallRepository
import cl.blipblipcode.prefixsapp.domain.model.AllowedCall
import cl.blipblipcode.prefixsapp.domain.model.BlockedCall
import cl.blipblipcode.prefixsapp.domain.model.HistoryItem
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetCallHistoryUseCaseTest {

    private lateinit var blockedRepository: FakeBlockedCallRepository
    private lateinit var allowedRepository: FakeAllowedCallRepository
    private lateinit var useCase: IGetCallHistoryUseCase

    @Before
    fun setUp() {
        blockedRepository = FakeBlockedCallRepository()
        allowedRepository = FakeAllowedCallRepository()
        useCase = GetCallHistoryUseCase(blockedRepository, allowedRepository)
    }

    @Test
    fun should_emit_combined_history_sorted_by_timestamp_descending_when_filter_is_all_in_invoke() = runTest {
        //GIVEN
        val blocked = BlockedCall(id = 1, phoneNumber = "+57300", matchedPrefix = "+57", timestamp = 100L)
        val allowed = AllowedCall(id = 2, phoneNumber = "+57301", timestamp = 200L)
        blockedRepository.setAllBlockedCalls(listOf(blocked))
        allowedRepository.setAllowedCalls(listOf(allowed))

        //WHEN
        val flow = useCase(IGetCallHistoryUseCase.HistoryFilter.ALL)

        //THEN
        flow.test {
            val items = awaitItem()
            assertEquals(2, items.size)
            assertEquals(allowed.id, items[0].id)
            assertEquals(blocked.id, items[1].id)
            assertEquals(HistoryItem.CallType.ALLOWED, items[0].type)
            assertEquals(HistoryItem.CallType.BLOCKED, items[1].type)
            assertEquals("+57", items[1].matchedPrefix)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_only_blocked_items_when_filter_is_blocked_in_invoke() = runTest {
        //GIVEN
        val blocked = BlockedCall(id = 1, phoneNumber = "+57300", matchedPrefix = "+57", timestamp = 100L)
        val allowed = AllowedCall(id = 2, phoneNumber = "+57301", timestamp = 200L)
        blockedRepository.setAllBlockedCalls(listOf(blocked))
        allowedRepository.setAllowedCalls(listOf(allowed))

        //WHEN
        val flow = useCase(IGetCallHistoryUseCase.HistoryFilter.BLOCKED)

        //THEN
        flow.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(blocked.id, items[0].id)
            assertTrue(items[0].isBlocked)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_only_allowed_items_when_filter_is_allowed_in_invoke() = runTest {
        //GIVEN
        val blocked = BlockedCall(id = 1, phoneNumber = "+57300", matchedPrefix = "+57", timestamp = 100L)
        val allowed = AllowedCall(id = 2, phoneNumber = "+57301", timestamp = 200L)
        blockedRepository.setAllBlockedCalls(listOf(blocked))
        allowedRepository.setAllowedCalls(listOf(allowed))

        //WHEN
        val flow = useCase(IGetCallHistoryUseCase.HistoryFilter.ALLOWED)

        //THEN
        flow.test {
            val items = awaitItem()
            assertEquals(1, items.size)
            assertEquals(allowed.id, items[0].id)
            assertEquals(HistoryItem.CallType.ALLOWED, items[0].type)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_empty_list_when_both_repositories_are_empty_in_invoke() = runTest {
        //GIVEN

        //WHEN
        val flow = useCase()

        //THEN
        flow.test {
            assertEquals(emptyList<HistoryItem>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
