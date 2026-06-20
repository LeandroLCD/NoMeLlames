package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.core.fakes.FakePrefixRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetSkipNotificationUseCaseTest {

    private lateinit var repository: FakePrefixRepository
    private lateinit var useCase: IGetSkipNotificationUseCase

    @Before
    fun setUp() {
        repository = FakePrefixRepository()
        useCase = GetSkipNotificationUseCase(repository)
    }

    @Test
    fun should_expose_repository_skip_notification_value_in_invoke() = runTest {
        //GIVEN
        repository.setSkipNotificationValue(true)

        //WHEN
        val flow = useCase()

        //THEN
        assertEquals(true, flow.value)
    }
}
