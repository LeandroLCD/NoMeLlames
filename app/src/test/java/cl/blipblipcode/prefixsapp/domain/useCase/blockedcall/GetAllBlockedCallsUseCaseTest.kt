package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockedCallRepository
import cl.blipblipcode.prefixsapp.domain.model.BlockType
import cl.blipblipcode.prefixsapp.domain.model.BlockedCall
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllBlockedCallsUseCaseTest {

    private lateinit var repository: FakeBlockedCallRepository
    private lateinit var useCase: IGetAllBlockedCallsUseCase

    @Before
    fun setUp() {
        repository = FakeBlockedCallRepository()
        useCase = GetAllBlockedCallsUseCase(repository)
    }

    @Test
    fun should_emit_repository_blocked_calls_in_invoke() = runTest {
        //GIVEN
        val calls = listOf(
            BlockedCall(id = 1, phoneNumber = "+573001234567", matchedPrefix = "+57", timestamp = 100L, blockType = BlockType.Prefix("+57")),
            BlockedCall(id = 2, phoneNumber = "+573001234568", matchedPrefix = "+57", timestamp = 200L, blockType = BlockType.Prefix("+57"))
        )
        repository.setAllBlockedCalls(calls)

        //WHEN
        val flow = useCase()

        //THEN
        flow.test {
            assertEquals(calls, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_empty_list_when_repository_has_no_blocked_calls_in_invoke() = runTest {
        //GIVEN

        //WHEN
        val flow = useCase()

        //THEN
        flow.test {
            assertEquals(emptyList<BlockedCall>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
