package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockedCallRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetUnseenCountUseCaseTest {

    private lateinit var repository: FakeBlockedCallRepository
    private lateinit var useCase: IGetUnseenCountUseCase

    @Before
    fun setUp() {
        repository = FakeBlockedCallRepository()
        useCase = GetUnseenCountUseCase(repository)
    }

    @Test
    fun should_emit_repository_unseen_count_in_invoke() = runTest {
        //GIVEN
        repository.setUnseenCount(3)

        //WHEN
        val flow = useCase()

        //THEN
        flow.test {
            assertEquals(3, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_zero_when_repository_unseen_count_is_zero_in_invoke() = runTest {
        //GIVEN
        repository.setUnseenCount(0)

        //WHEN
        val flow = useCase()

        //THEN
        flow.test {
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
