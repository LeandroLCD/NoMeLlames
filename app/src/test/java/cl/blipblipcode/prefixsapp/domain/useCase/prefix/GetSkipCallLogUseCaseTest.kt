package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.core.fakes.FakePrefixRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetSkipCallLogUseCaseTest {

    private lateinit var repository: FakePrefixRepository
    private lateinit var useCase: IGetSkipCallLogUseCase

    @Before
    fun setUp() {
        repository = FakePrefixRepository()
        useCase = GetSkipCallLogUseCase(repository)
    }

    @Test
    fun should_expose_repository_skip_call_log_value_in_invoke() = runTest {
        //GIVEN
        repository.setSkipCallLogValue(true)

        //WHEN
        val flow = useCase()

        //THEN
        assertEquals(true, flow.value)
    }

    @Test
    fun should_return_false_when_repository_skip_call_log_is_false_in_invoke() = runTest {
        //GIVEN

        //WHEN
        val flow = useCase()

        //THEN
        assertEquals(false, flow.value)
    }
}
