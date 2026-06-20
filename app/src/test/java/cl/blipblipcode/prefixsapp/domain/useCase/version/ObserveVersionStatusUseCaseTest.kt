package cl.blipblipcode.prefixsapp.domain.useCase.version

import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.core.fakes.FakeVersionRepository
import cl.blipblipcode.prefixsapp.domain.model.VersionStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ObserveVersionStatusUseCaseTest {

    private lateinit var repository: FakeVersionRepository
    private lateinit var useCase: IObserveVersionStatusUseCase

    @Before
    fun setUp() {
        repository = FakeVersionRepository()
        useCase = ObserveVersionStatusUseCase(repository)
    }

    @Test
    fun should_emit_values_pushed_to_repository_flow_in_invoke() = runTest {
        //GIVEN
        val status = VersionStatus.UpdateAvailable(latestVersion = "2.0.0", urlDownload = "https://example.com")

        //WHEN
        val flow = useCase()
        flow.test {
            repository.emit(status)

            //THEN
            assertEquals(status, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
