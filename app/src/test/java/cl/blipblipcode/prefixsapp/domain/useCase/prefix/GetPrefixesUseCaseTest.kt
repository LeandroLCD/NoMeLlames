package cl.blipblipcode.prefixsapp.domain.useCase.prefix

import cl.blipblipcode.prefixsapp.core.fakes.FakePrefixRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetPrefixesUseCaseTest {

    private lateinit var repository: FakePrefixRepository
    private lateinit var useCase: IGetPrefixesUseCase

    @Before
    fun setUp() {
        repository = FakePrefixRepository()
        useCase = GetPrefixesUseCase(repository)
    }

    @Test
    fun should_expose_repository_prefixes_state_flow_in_invoke() = runTest {
        //GIVEN
        val prefixes = setOf("+57", "+1", "+44")
        repository.setPrefixes(prefixes)

        //WHEN
        val flow = useCase()

        //THEN
        assertEquals(prefixes, flow.value)
    }

    @Test
    fun should_return_empty_set_when_repository_has_no_prefixes_in_invoke() = runTest {
        //GIVEN

        //WHEN
        val flow = useCase()

        //THEN
        assertEquals(emptySet<String>(), flow.value)
    }
}
