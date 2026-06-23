package cl.blipblipcode.prefixsapp.domain.useCase.blocking

import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockingPreferencesRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SetBlockNonContactsUseCaseTest {

    private lateinit var repository: FakeBlockingPreferencesRepository
    private lateinit var useCase: ISetBlockNonContactsUseCase

    @Before
    fun setUp() {
        repository = FakeBlockingPreferencesRepository()
        useCase = SetBlockNonContactsUseCase(repository)
    }

    @Test
    fun should_propagate_true_to_repository_in_invoke() = runTest {
        //GIVEN
        val value = true

        //WHEN
        useCase(value)

        //THEN
        assertEquals(value, repository.lastSetBlockNonContacts)
        assertEquals(1, repository.setBlockNonContactsCallCount)
    }

    @Test
    fun should_propagate_false_to_repository_in_invoke() = runTest {
        //GIVEN
        val value = false

        //WHEN
        useCase(value)

        //THEN
        assertEquals(value, repository.lastSetBlockNonContacts)
        assertEquals(1, repository.setBlockNonContactsCallCount)
    }

    @Test
    fun should_return_success_when_repository_succeeds_in_invoke() = runTest {
        //GIVEN
        repository.setSetBlockNonContactsResult(Result.success(Unit))

        //WHEN
        val result = useCase(true)

        //THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun should_return_failure_when_repository_returns_failure_in_invoke() = runTest {
        //GIVEN
        val exception = RuntimeException("Block non-contacts error")
        repository.setSetBlockNonContactsResult(Result.failure(exception))

        //WHEN
        val result = useCase(true)

        //THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun should_call_repository_once_per_invocation_in_invoke() = runTest {
        //WHEN
        useCase(true)
        useCase(false)

        //THEN
        assertEquals(2, repository.setBlockNonContactsCallCount)
        assertEquals(false, repository.lastSetBlockNonContacts)
    }
}
