package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockedCallRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetBlockedCallsCountUseCaseTest {

    private lateinit var repository: FakeBlockedCallRepository
    private lateinit var useCase: IGetBlockedCallsCountUseCase

    @Before
    fun setUp() {
        repository = FakeBlockedCallRepository()
        useCase = GetBlockedCallsCountUseCase(repository)
    }

    @Test
    fun should_emit_repository_count_in_invoke() = runTest {
        //GIVEN
        repository.setBlockedCallsCount(7)

        //WHEN
        val flow = useCase()

        //THEN
        flow.test {
            assertEquals(7, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_zero_when_repository_count_is_zero_in_invoke() = runTest {
        //GIVEN
        repository.setBlockedCallsCount(0)

        //WHEN
        val flow = useCase()

        //THEN
        flow.test {
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
