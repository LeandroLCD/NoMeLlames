package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockedCallRepository
import cl.blipblipcode.prefixsapp.domain.model.BlockedCall
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetRecentBlockedCallsUseCaseTest {

    private lateinit var repository: FakeBlockedCallRepository
    private lateinit var useCase: IGetRecentBlockedCallsUseCase

    @Before
    fun setUp() {
        repository = FakeBlockedCallRepository()
        useCase = GetRecentBlockedCallsUseCase(repository)
    }

    @Test
    fun should_pass_limit_to_repository_in_invoke() = runTest {
        //GIVEN
        val limit = 10

        //WHEN
        useCase.invoke(limit).test {
            awaitItem()

            //THEN
            assertEquals(limit, repository.lastRequestedRecentLimit)
        }

    }

    @Test
    fun should_emit_repository_recent_blocked_calls_in_invoke() = runTest {
        //GIVEN
        val calls = listOf(
            BlockedCall(id = 1, phoneNumber = "+573001234567", matchedPrefix = "+57", timestamp = 100L)
        )
        repository.setRecentBlockedCalls(calls)

        //WHEN
        val flow = useCase(5)

        //THEN
        flow.test {
            assertEquals(calls, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
