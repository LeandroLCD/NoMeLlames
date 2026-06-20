package cl.blipblipcode.prefixsapp.domain.useCase.settings

import cl.blipblipcode.prefixsapp.core.fakes.FakeAppSettingsRepository
import cl.blipblipcode.prefixsapp.domain.model.ThemeApp
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SetThemeAppUseCaseTest {

    private lateinit var repository: FakeAppSettingsRepository
    private lateinit var useCase: ISetThemeAppUseCase

    @Before
    fun setUp() {
        repository = FakeAppSettingsRepository()
        useCase = SetThemeAppUseCase(repository)
    }

    @Test
    fun should_propagate_theme_to_repository_in_invoke() = runTest {
        //GIVEN
        val theme = ThemeApp.Pink

        //WHEN
        useCase(theme)

        //THEN
        assertEquals(theme, repository.lastSetTheme)
    }

    @Test
    fun should_return_success_when_repository_succeeds_in_invoke() = runTest {
        //GIVEN
        repository.setSetThemeAppResult(Result.success(Unit))

        //WHEN
        val result = useCase(ThemeApp.Dark)

        //THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun should_return_failure_when_repository_returns_failure_in_invoke() = runTest {
        //GIVEN
        val exception = RuntimeException("Theme error")
        repository.setSetThemeAppResult(Result.failure(exception))

        //WHEN
        val result = useCase(ThemeApp.Dark)

        //THEN
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}
