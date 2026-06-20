package cl.blipblipcode.prefixsapp.domain.useCase.blockedcall

import cl.blipblipcode.prefixsapp.core.fakes.FakeBlockedCallRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class InsertBlockedCallUseCaseTest {

    private lateinit var repository: FakeBlockedCallRepository
    private lateinit var useCase: IInsertBlockedCallUseCase

    @Before
    fun setUp() {
        repository = FakeBlockedCallRepository()
        useCase = InsertBlockedCallUseCase(repository)
    }

    @Test
    fun should_call_repository_with_phone_and_prefix_in_invoke() = runTest {
        //GIVEN
        val phoneNumber = "+573001234567"
        val matchedPrefix = "+57"

        //WHEN
        useCase(phoneNumber, matchedPrefix)

        //THEN
        assertEquals(phoneNumber, repository.lastInsertedPhone)
        assertEquals(matchedPrefix, repository.lastInsertedMatchedPrefix)
        assertEquals(1, repository.insertCallCount)
    }

    @Test
    fun should_call_repository_once_per_invocation_in_invoke() = runTest {
        //GIVEN

        //WHEN
        useCase("+573001234567", "+57")
        useCase("+573001234568", "+57")

        //THEN
        assertEquals(2, repository.insertCallCount)
    }
}
