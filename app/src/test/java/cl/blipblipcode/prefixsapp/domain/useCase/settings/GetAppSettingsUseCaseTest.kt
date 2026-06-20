package cl.blipblipcode.prefixsapp.domain.useCase.settings

import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.core.fakes.FakeAppSettingsRepository
import cl.blipblipcode.prefixsapp.domain.model.AppSettings
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetAppSettingsUseCaseTest {

    private lateinit var repository: FakeAppSettingsRepository
    private lateinit var useCase: IGetAppSettingsUseCase

    @Before
    fun setUp() {
        repository = FakeAppSettingsRepository()
        useCase = GetAppSettingsUseCase(repository)
    }

    @Test
    fun should_emit_repository_settings_in_invoke() = runTest {
        //GIVEN
        val settings = AppSettings(
            lastPrefixUpdateTimestamp = 1_000L,
            totalPrefixCount = 42,
            syncStatus = AppSettings.SyncStatus.COMPLETED
        )
        repository.setSettings(settings)

        //WHEN
        val flow = useCase()

        //THEN
        flow.test {
            assertEquals(settings, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
