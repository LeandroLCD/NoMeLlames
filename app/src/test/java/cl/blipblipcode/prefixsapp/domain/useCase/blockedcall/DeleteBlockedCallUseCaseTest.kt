package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockedCallRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DeleteBlockedCallUseCaseTest {

    private lateinit var repository: FakeBlockedCallRepository
    private lateinit var useCase: IDeleteBlockedCallUseCase

    @Before
    fun setUp() {
        repository = FakeBlockedCallRepository()
        useCase = DeleteBlockedCallUseCase(repository)
    }

    @Test
    fun should_call_repository_with_id_in_invoke() = runTest {
        //GIVEN
        val id = 42L

        //WHEN
        useCase(id)

        //THEN
        assertEquals(id, repository.lastDeletedId)
    }

    @Test
    fun should_call_repository_with_zero_when_id_is_zero_in_invoke() = runTest {
        //GIVEN

        //WHEN
        useCase(0L)

        //THEN
        assertEquals(0L, repository.lastDeletedId)
    }
}
