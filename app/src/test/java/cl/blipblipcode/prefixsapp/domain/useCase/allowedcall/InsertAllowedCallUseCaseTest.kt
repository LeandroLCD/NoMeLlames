package cl.blipblipcode.prefixsapp.domain.useCase.allowedcall

import cl.blipblipcode.prefixsapp.core.fakes.FakeAllowedCallRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class InsertAllowedCallUseCaseTest {

    private lateinit var repository: FakeAllowedCallRepository
    private lateinit var useCase: IInsertAllowedCallUseCase

    @Before
    fun setUp() {
        repository = FakeAllowedCallRepository()
        useCase = InsertAllowedCallUseCase(repository)
    }

    @Test
    fun should_return_success_when_repository_inserts_in_invoke() = runTest {
        //GIVEN
        repository.setInsertResult(Result.success(Unit))
        val phoneNumber = "+573001234567"

        //WHEN
        val result = useCase(phoneNumber)

        //THEN
        assertTrue(result.isSuccess)
        assertEquals(phoneNumber, repository.lastInsertedPhone)
    }

    @Test
    fun should_return_failure_when_repository_returns_database_error_in_invoke() = runTest {
        //GIVEN
        val exception = IOException("Database error")
        repository.setInsertResult(Result.failure(exception))

        //WHEN
        val result = useCase("+573001234567")

        //THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IOException)
    }

    @Test
    fun should_propagate_phone_number_to_repository_in_invoke() = runTest {
        //GIVEN
        val phoneNumber = "+573159876543"
        repository.setInsertResult(Result.success(Unit))

        //WHEN
        useCase(phoneNumber)

        //THEN
        assertEquals(phoneNumber, repository.lastInsertedPhone)
    }

    @Test
    fun should_propagate_runtime_exception_when_repository_returns_failure_in_invoke() = runTest {
        //GIVEN
        val exception = RuntimeException("Unexpected error")
        repository.setInsertResult(Result.failure(exception))

        //WHEN
        val result = useCase("+573001234567")

        //THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }

    @Test
    fun should_call_repository_with_empty_phone_number_in_invoke() = runTest {
        //GIVEN
        repository.setInsertResult(Result.success(Unit))

        //WHEN
        val result = useCase("")

        //THEN
        assertTrue(result.isSuccess)
        assertEquals("", repository.lastInsertedPhone)
    }
}
