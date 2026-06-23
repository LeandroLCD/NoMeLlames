package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.core.fakes.FakePrefixRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SetSkipCallLogUseCaseTest {

    private lateinit var repository: FakePrefixRepository
    private lateinit var useCase: ISetSkipCallLogUseCase

    @Before
    fun setUp() {
        repository = FakePrefixRepository()
        useCase = SetSkipCallLogUseCase(repository)
    }

    @Test
    fun should_propagate_value_to_repository_in_invoke() = runTest {
        //GIVEN
        val value = true

        //WHEN
        val result = useCase(value)

        //THEN
        assertTrue(result.isSuccess)
        assertEquals(value, repository.lastSetSkipCallLog)
        assertEquals(value, repository.skipCallLog.value)
    }

    @Test
    fun should_propagate_repository_failure_in_invoke() = runTest {
        //GIVEN
        val exception = RuntimeException("DataStore error")
        repository.setSetSkipCallLogResult(Result.failure(exception))

        //WHEN
        val result = useCase(true)

        //THEN
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
