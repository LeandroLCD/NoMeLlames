package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.core.fakes.FakePrefixRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DeleteAllPrefixRulesUseCaseTest {

    private lateinit var repository: FakePrefixRepository
    private lateinit var useCase: IDeleteAllPrefixRulesUseCase

    @Before
    fun setUp() {
        repository = FakePrefixRepository()
        useCase = DeleteAllPrefixRulesUseCase(repository)
    }

    @Test
    fun should_return_success_when_repository_succeeds_in_invoke() = runTest {
        //GIVEN
        repository.setDeleteAllPrefixRulesResult(Result.success(Unit))

        //WHEN
        val result = useCase()

        //THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun should_return_failure_when_repository_returns_failure_in_invoke() = runTest {
        //GIVEN
        val exception = RuntimeException("DB error")
        repository.setDeleteAllPrefixRulesResult(Result.failure(exception))

        //WHEN
        val result = useCase()

        //THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}
