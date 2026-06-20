package cl.blipblipcode.prefixsapp.domain.useCase.settings

import cl.blipblipcode.prefixsapp.core.fakes.FakeAppSettingsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UpdatePrefixSyncUseCaseTest {

    private lateinit var repository: FakeAppSettingsRepository
    private lateinit var useCase: IUpdatePrefixSyncUseCase

    @Before
    fun setUp() {
        repository = FakeAppSettingsRepository()
        useCase = UpdatePrefixSyncUseCase(repository)
    }

    @Test
    fun should_propagate_prefix_count_to_repository_in_invoke() = runTest {
        //GIVEN
        val prefixCount = 25

        //WHEN
        useCase(prefixCount)

        //THEN
        assertEquals(prefixCount, repository.lastUpdatedPrefixCount)
    }

    @Test
    fun should_return_success_when_repository_succeeds_in_invoke() = runTest {
        //GIVEN
        repository.setUpdatePrefixSyncResult(Result.success(Unit))

        //WHEN
        val result = useCase(10)

        //THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun should_return_failure_when_repository_returns_failure_in_invoke() = runTest {
        //GIVEN
        val exception = RuntimeException("Sync failed")
        repository.setUpdatePrefixSyncResult(Result.failure(exception))

        //WHEN
        val result = useCase(10)

        //THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}
