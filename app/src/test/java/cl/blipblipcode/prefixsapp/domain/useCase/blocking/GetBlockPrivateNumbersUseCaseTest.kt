package cl.blipblipcode.prefixsapp.domain.useCase.blocking

import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockingPreferencesRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetBlockPrivateNumbersUseCaseTest {

    private lateinit var repository: FakeBlockingPreferencesRepository
    private lateinit var useCase: IGetBlockPrivateNumbersUseCase

    @Before
    fun setUp() {
        repository = FakeBlockingPreferencesRepository()
        useCase = GetBlockPrivateNumbersUseCase(repository)
    }

    @Test
    fun should_emit_repository_value_in_invoke() = runTest {
        //GIVEN
        repository.setPrivateNumbersValue(true)

        //WHEN
        useCase().test {
            //THEN
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_false_when_repository_value_is_false_in_invoke() = runTest {
        //GIVEN
        repository.setPrivateNumbersValue(false)

        //WHEN
        useCase().test {
            //THEN
            assertEquals(false, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_updated_value_when_repository_value_changes_after_subscription_in_invoke() = runTest {
        //GIVEN
        repository.setPrivateNumbersValue(false)

        //WHEN
        useCase().test {
            assertEquals(false, awaitItem())
            repository.setPrivateNumbersValue(true)
            //THEN
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}