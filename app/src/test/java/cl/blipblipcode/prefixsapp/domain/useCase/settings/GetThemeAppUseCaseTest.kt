package cl.blipblipcode.prefixsapp.domain.useCase.settings

import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.core.fakes.FakeAppSettingsRepository
import cl.blipblipcode.prefixsapp.domain.model.ThemeApp
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetThemeAppUseCaseTest {

    private lateinit var repository: FakeAppSettingsRepository
    private lateinit var useCase: IGetThemeAppUseCase

    @Before
    fun setUp() {
        repository = FakeAppSettingsRepository()
        useCase = GetThemeAppUseCase(repository)
    }

    @Test
    fun should_emit_repository_theme_in_invoke() = runTest {
        //GIVEN
        repository.setThemeAppValue(ThemeApp.Dark)

        //WHEN
        val flow = useCase()

        //THEN
        flow.test {
            assertEquals(ThemeApp.Dark, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
