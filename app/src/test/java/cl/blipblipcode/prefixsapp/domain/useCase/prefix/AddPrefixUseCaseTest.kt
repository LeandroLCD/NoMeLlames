package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.core.fakes.FakePrefixRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AddPrefixUseCaseTest {

    private lateinit var repository: FakePrefixRepository
    private lateinit var useCase: IAddPrefixUseCase

    @Before
    fun setUp() {
        repository = FakePrefixRepository()
        useCase = AddPrefixUseCase(repository)
    }

    @Test
    fun should_propagate_prefix_to_repository_in_invoke() = runTest {
        //GIVEN
        val prefix = "+57"

        //WHEN
        val result = useCase(prefix)

        //THEN
        assertTrue(result.isSuccess)
        assertEquals(prefix, repository.lastAddedPrefix)
    }

    @Test
    fun should_propagate_repository_failure_in_invoke() = runTest {
        //GIVEN
        val exception = RuntimeException("DB error")
        repository.setAddPrefixResult(Result.failure(exception))

        //WHEN
        val result = useCase("+57")

        //THEN
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
