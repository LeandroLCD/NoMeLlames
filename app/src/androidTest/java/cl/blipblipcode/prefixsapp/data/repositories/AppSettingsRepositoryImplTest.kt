package cl.blipblipcode.prefixsapp.data.repositories

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.data.local.dao.PrefixRuleDao
import cl.blipblipcode.prefixsapp.data.local.entities.PrefixRuleEntity
import cl.blipblipcode.prefixsapp.domain.model.AppSettings
import cl.blipblipcode.prefixsapp.domain.model.ThemeApp
import cl.blipblipcode.prefixsapp.domain.repositories.AppSettingsRepository
import cl.blipblipcode.prefixsapp.rules.MainDispatcherRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AppSettingsRepositoryImplTest {

    @get:Rule(order = 0)
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: AppSettingsRepository

    @Inject
    lateinit var prefixRuleDao: PrefixRuleDao

    @Before
    fun setUp() {
        hiltRule.inject()
        runBlocking {
            prefixRuleDao.deleteAllPrefixRules()
            repository.setThemeApp(ThemeApp.System)
        }
    }

    @Test
    fun should_emit_zero_count_and_zero_timestamp_when_no_prefixes_in_db_in_invoke() = runTest {
        //WHEN
        val settings = repository.getSettings().first()

        //THEN
        assertEquals(0, settings.totalPrefixCount)
        assertEquals(0L, settings.lastPrefixUpdateTimestamp)
    }

    @Test
    fun should_emit_count_matching_prefixes_when_prefixes_exist_in_db_in_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "57", ruleType = "BLOCK"))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "1", ruleType = "ALLOW"))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "44", ruleType = "BLOCK"))

        //WHEN
        val settings = repository.getSettings().first()

        //THEN
        assertEquals(3, settings.totalPrefixCount)
    }

    @Test
    fun should_emit_max_created_at_as_timestamp_when_prefixes_exist_in_db_in_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "1", ruleType = "BLOCK", createdAt = 100L))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "2", ruleType = "BLOCK", createdAt = 300L))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "3", ruleType = "BLOCK", createdAt = 200L))

        //WHEN
        val settings = repository.getSettings().first()

        //THEN
        assertEquals(300L, settings.lastPrefixUpdateTimestamp)
    }

    @Test
    fun should_emit_updated_settings_after_prefix_added_to_db_in_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "1", ruleType = "BLOCK", createdAt = 100L))

        //WHEN
        repository.getSettings().test {
            assertEquals(AppSettings(100L, 1), awaitItem())
            prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "2", ruleType = "BLOCK", createdAt = 200L))
            //THEN
            assertEquals(AppSettings(200L, 2), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_updated_settings_after_prefix_removed_from_db_in_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "1", ruleType = "BLOCK", createdAt = 100L))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "2", ruleType = "BLOCK", createdAt = 200L))

        //WHEN
        repository.getSettings().test {
            assertEquals(AppSettings(200L, 2), awaitItem())
            prefixRuleDao.deletePrefixByValue("1")
            //THEN
            assertEquals(AppSettings(200L, 1), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_system_theme_on_initial_state_in_invoke() = runTest {
        //WHEN
        val theme = repository.getThemeApp().first()

        //THEN
        assertEquals(ThemeApp.System, theme)
    }

    @Test
    fun should_emit_persisted_theme_when_set_in_data_store_in_invoke() = runTest {
        //GIVEN
        repository.setThemeApp(ThemeApp.Pink).getOrThrow()

        //WHEN
        val theme = repository.getThemeApp().first()

        //THEN
        assertEquals(ThemeApp.Pink, theme)
    }

    @Test
    fun should_emit_each_theme_when_persisted_in_data_store_in_invoke() = runTest {
        //GIVEN
        val themes = listOf(
            ThemeApp.System, ThemeApp.Dark, ThemeApp.Light,
            ThemeApp.Pink, ThemeApp.Green, ThemeApp.Solaris
        )

        themes.forEach { theme ->
            //WHEN
            repository.setThemeApp(theme).getOrThrow()

            //THEN
            val emitted = repository.getThemeApp().first()
            assertEquals("failed for theme=$theme", theme, emitted)
        }
    }

    @Test
    fun should_return_success_when_data_store_writes_in_invoke() = runTest {
        //WHEN
        val result = repository.setThemeApp(ThemeApp.Dark)

        //THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun should_persist_dark_theme_when_invoked() = runTest {
        //GIVEN
        val theme = ThemeApp.Dark

        //WHEN
        repository.setThemeApp(theme).getOrThrow()

        //THEN
        val emitted = repository.getThemeApp().first()
        assertEquals(theme, emitted)
    }

    @Test
    fun should_persist_all_themes_in_data_store_when_invoked() = runTest {
        //GIVEN
        val themes = listOf(
            ThemeApp.System, ThemeApp.Dark, ThemeApp.Light,
            ThemeApp.Pink, ThemeApp.Green, ThemeApp.Solaris
        )

        themes.forEach { theme ->
            //WHEN
            repository.setThemeApp(theme).getOrThrow()

            //THEN
            val emitted = repository.getThemeApp().first()
            assertEquals("failed for theme=$theme", theme, emitted)
        }
    }

    @Test
    fun should_emit_updated_theme_after_set_theme_app_in_invoke() = runTest {
        //WHEN
        repository.getThemeApp().test {
            assertEquals(ThemeApp.System, awaitItem())
            repository.setThemeApp(ThemeApp.Green).getOrThrow()
            //THEN
            assertEquals(ThemeApp.Green, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }
}