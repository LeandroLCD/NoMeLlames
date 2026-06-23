package cl.blipblipcode.prefixsapp.domain.useCase.blocking

import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockingPreferencesRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetBlockNonContactsUseCaseTest {

    private lateinit var repository: FakeBlockingPreferencesRepository
    private lateinit var useCase: IGetBlockNonContactsUseCase

    @Before
    fun setUp() {
        repository = FakeBlockingPreferencesRepository()
        useCase = GetBlockNonContactsUseCase(repository)
    }

    @Test
    fun should_emit_repository_value_in_invoke() = runTest {
        //GIVEN
        repository.setNonContactsValue(true)

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
        repository.setNonContactsValue(false)

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
        repository.setNonContactsValue(false)

        //WHEN
        useCase().test {
            assertEquals(false, awaitItem())
            repository.setNonContactsValue(true)
            //THEN
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}