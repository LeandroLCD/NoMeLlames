package cl.blipblipcode.prefixsapp.domain.useCase.version

import cl.blipblipcode.prefixsapp.core.fakes.FakeVersionRepository
import cl.blipblipcode.prefixsapp.domain.model.VersionStatus
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CheckVersionStatusUseCaseTest {

    private lateinit var repository: FakeVersionRepository
    private lateinit var useCase: ICheckVersionStatusUseCase

    @Before
    fun setUp() {
        repository = FakeVersionRepository()
        useCase = CheckVersionStatusUseCase(repository)
    }

    @Test
    fun should_return_success_with_up_to_date_when_repository_returns_up_to_date_in_invoke() = runTest {
        //GIVEN
        repository.setCheckResult(Result.success(VersionStatus.UpToDate))

        //WHEN
        val result = useCase()

        //THEN
        assertTrue(result.isSuccess)
        assertEquals(VersionStatus.UpToDate, result.getOrNull())
    }

    @Test
    fun should_return_success_with_update_available_when_repository_returns_it_in_invoke() = runTest {
        //GIVEN
        val status = VersionStatus.UpdateAvailable(latestVersion = "2.0.0", urlDownload = "https://example.com")
        repository.setCheckResult(Result.success(status))

        //WHEN
        val result = useCase()

        //THEN
        assertEquals(status, result.getOrNull())
    }

    @Test
    fun should_return_failure_when_repository_returns_failure_in_invoke() = runTest {
        //GIVEN
        val exception = RuntimeException("Network error")
        repository.setCheckResult(Result.failure(exception))

        //WHEN
        val result = useCase()

        //THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}
