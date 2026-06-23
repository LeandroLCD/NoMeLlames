package cl.blipblipcode.prefixsapp.data.repositories

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.domain.model.VersionStatus
import cl.blipblipcode.prefixsapp.domain.repositories.VersionRepository
import cl.blipblipcode.prefixsapp.rules.MainDispatcherRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class VersionRepositoryImplTest {

    @get:Rule(order = 0)
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: VersionRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun should_return_success_result_when_called_in_invoke() = runTest {
        //WHEN
        val result = repository.checkVersionStatus()

        //THEN
        assertTrue(result.isSuccess)
    }

    @Test
    fun should_return_up_to_date_when_debug_build_in_invoke() = runTest {
        //WHEN
        val result = repository.checkVersionStatus()

        //THEN
        assertEquals(VersionStatus.UpToDate, result.getOrNull())
    }

    @Test
    fun should_emit_up_to_date_on_first_subscription_in_version_status_flow() = runTest {
        //WHEN
        repository.versionStatusFlow.test {
            //THEN
            assertEquals(VersionStatus.UpToDate, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_replay_up_to_date_to_subsequent_subscribers_in_version_status_flow() = runTest {
        //WHEN
        repository.versionStatusFlow.test {
            assertEquals(VersionStatus.UpToDate, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        repository.versionStatusFlow.test {
            //THEN
            assertEquals(VersionStatus.UpToDate, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_up_to_date_to_multiple_concurrent_subscribers_in_version_status_flow() = runTest {
        //WHEN
        repository.versionStatusFlow.test {
            assertEquals(VersionStatus.UpToDate, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
        repository.versionStatusFlow.test {
            //THEN
            assertEquals(VersionStatus.UpToDate, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_return_consistent_result_when_called_twice_in_invoke() = runTest {
        //WHEN
        val first = repository.checkVersionStatus()
        val second = repository.checkVersionStatus()

        //THEN
        assertEquals(VersionStatus.UpToDate, first.getOrNull())
        assertEquals(VersionStatus.UpToDate, second.getOrNull())
    }
}
