package cl.blipblipcode.prefixsapp.domain.useCase.history

import cl.blipblipcode.prefixsapp.core.fakes.FakeAllowedCallRepository
import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockedCallRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ClearAllHistoryUseCaseTest {

    private lateinit var blockedRepository: FakeBlockedCallRepository
    private lateinit var allowedRepository: FakeAllowedCallRepository
    private lateinit var useCase: IClearAllHistoryUseCase

    @Before
    fun setUp() {
        blockedRepository = FakeBlockedCallRepository()
        allowedRepository = FakeAllowedCallRepository()
        useCase = ClearAllHistoryUseCase(blockedRepository, allowedRepository)
    }

    @Test
    fun should_call_both_repositories_in_invoke() = runTest {
        //GIVEN

        //WHEN
        useCase()

        //THEN
        assertTrue(blockedRepository.deleteAllCallCount >= 1)
        assertTrue(allowedRepository.lastDeleteAllCalled)
    }

    @Test
    fun should_return_success_when_both_repositories_succeed_in_invoke() = runTest {
        //GIVEN

        //WHEN
        val result = useCase()

        //THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun should_return_failure_when_blocked_repository_throws_in_invoke() = runTest {
        //GIVEN
        blockedRepository.deleteAllShouldThrow = RuntimeException("DB error")

        //WHEN
        val result = useCase()

        //THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}
