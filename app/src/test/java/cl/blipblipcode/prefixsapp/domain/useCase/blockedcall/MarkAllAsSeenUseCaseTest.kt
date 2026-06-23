package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockedCallRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MarkAllAsSeenUseCaseTest {

    private lateinit var repository: FakeBlockedCallRepository
    private lateinit var useCase: IMarkAllAsSeenUseCase

    @Before
    fun setUp() {
        repository = FakeBlockedCallRepository()
        useCase = MarkAllAsSeenUseCase(repository)
    }

    @Test
    fun should_call_repository_mark_all_as_seen_in_invoke() = runTest {
        //GIVEN

        //WHEN
        useCase()

        //THEN
        assertEquals(1, repository.markAllAsSeenCallCount)
    }

    @Test
    fun should_call_repository_each_invocation_in_invoke() = runTest {
        //GIVEN

        //WHEN
        useCase()
        useCase()

        //THEN
        assertEquals(2, repository.markAllAsSeenCallCount)
    }
}
