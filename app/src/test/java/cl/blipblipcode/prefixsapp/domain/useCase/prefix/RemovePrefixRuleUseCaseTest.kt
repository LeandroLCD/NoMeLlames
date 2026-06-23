package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.core.fakes.FakePrefixRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RemovePrefixRuleUseCaseTest {

    private lateinit var repository: FakePrefixRepository
    private lateinit var useCase: IRemovePrefixRuleUseCase

    @Before
    fun setUp() {
        repository = FakePrefixRepository()
        useCase = RemovePrefixRuleUseCase(repository)
    }

    @Test
    fun should_propagate_id_to_repository_in_invoke() = runTest {
        //GIVEN
        val id = 99L

        //WHEN
        useCase(id)

        //THEN
        assertEquals(id, repository.lastRemovedPrefixRuleId)
    }

    @Test
    fun should_return_success_when_repository_succeeds_in_invoke() = runTest {
        //GIVEN
        repository.setRemovePrefixRuleResult(Result.success(Unit))

        //WHEN
        val result = useCase(1L)

        //THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun should_return_failure_when_repository_returns_failure_in_invoke() = runTest {
        //GIVEN
        val exception = RuntimeException("DB error")
        repository.setRemovePrefixRuleResult(Result.failure(exception))

        //WHEN
        val result = useCase(1L)

        //THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}
