package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.core.fakes.FakePrefixRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SetSkipNotificationUseCaseTest {

    private lateinit var repository: FakePrefixRepository
    private lateinit var useCase: ISetSkipNotificationUseCase

    @Before
    fun setUp() {
        repository = FakePrefixRepository()
        useCase = SetSkipNotificationUseCase(repository)
    }

    @Test
    fun should_propagate_value_to_repository_in_invoke() = runTest {
        //GIVEN
        val value = true

        //WHEN
        useCase(value)

        //THEN
        assertEquals(value, repository.lastSetSkipNotification)
    }
}
