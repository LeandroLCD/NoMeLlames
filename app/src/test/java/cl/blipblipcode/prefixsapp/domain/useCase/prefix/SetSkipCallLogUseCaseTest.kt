package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.core.fakes.FakePrefixRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
        useCase(value)

        //THEN
        assertEquals(value, repository.lastSetSkipCallLog)
    }
}
