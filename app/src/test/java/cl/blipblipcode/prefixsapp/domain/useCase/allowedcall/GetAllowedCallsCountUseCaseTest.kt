package cl.blipblipcode.prefixsapp.domain.useCase.allowedcall

import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.core.fakes.FakeAllowedCallRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAllowedCallsCountUseCaseTest {

    private lateinit var repository: FakeAllowedCallRepository
    private lateinit var useCase: IGetAllowedCallsCountUseCase

    @Before
    fun setUp() {
        repository = FakeAllowedCallRepository()
        useCase = GetAllowedCallsCountUseCase(repository)
    }

    @Test
    fun should_emit_repository_count_in_invoke() = runTest {
        //GIVEN
        repository.setCount(5)

        //WHEN
        val flow = useCase()

        //THEN
        flow.test {
            assertEquals(5, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_zero_when_repository_count_is_zero_in_invoke() = runTest {
        //GIVEN
        repository.setCount(0)

        //WHEN
        val flow = useCase()

        //THEN
        flow.test {
            assertEquals(0, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_updated_value_when_repository_count_changes_after_subscription_in_invoke() = runTest {
        //GIVEN
        repository.setCount(1)

        //WHEN
        useCase().test {
            assertEquals(1, awaitItem())
            repository.setCount(2)

            //THEN
            assertEquals(2, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
