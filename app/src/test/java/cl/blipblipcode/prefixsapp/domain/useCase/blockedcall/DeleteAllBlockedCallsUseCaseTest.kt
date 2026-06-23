package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockedCallRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class DeleteAllBlockedCallsUseCaseTest {

    private lateinit var repository: FakeBlockedCallRepository
    private lateinit var useCase: IDeleteAllBlockedCallsUseCase

    @Before
    fun setUp() {
        repository = FakeBlockedCallRepository()
        useCase = DeleteAllBlockedCallsUseCase(repository)
    }

    @Test
    fun should_call_repository_in_invoke() = runTest {
        //GIVEN

        //WHEN
        useCase()

        //THEN
        assert(repository.deleteAllCallCount >= 1)
    }
}
