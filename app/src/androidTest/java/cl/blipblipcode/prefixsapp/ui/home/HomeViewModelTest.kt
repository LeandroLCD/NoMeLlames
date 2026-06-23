package cl.blipblipcode.prefixsapp.ui.home

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.data.local.dao.AllowedCallDao
import cl.blipblipcode.prefixsapp.data.local.dao.BlockedCallDao
import cl.blipblipcode.prefixsapp.data.local.dao.PrefixRuleDao
import cl.blipblipcode.prefixsapp.data.local.entities.AllowedCallEntity
import cl.blipblipcode.prefixsapp.data.local.entities.BlockedCallEntity
import cl.blipblipcode.prefixsapp.data.local.entities.PrefixRuleEntity
import cl.blipblipcode.prefixsapp.data.repositories.BlockingPreferencesRepositoryImpl
import cl.blipblipcode.prefixsapp.domain.useCase.allowedcall.IGetAllowedCallsCountUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blockedcall.IGetBlockedCallsCountUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blockedcall.IGetRecentBlockedCallsUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blocking.IGetBlockNonContactsUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blocking.IGetBlockPrivateNumbersUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blocking.ISetBlockNonContactsUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blocking.ISetBlockPrivateNumbersUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IGetPrefixesUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.settings.IGetAppSettingsUseCase
import cl.blipblipcode.prefixsapp.rules.MainDispatcherRule
import cl.blipblipcode.prefixsapp.ui.home.model.Permission
import cl.blipblipcode.prefixsapp.utils.awaitMatches
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    @get:Rule(order = 0)
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var getPrefixesUseCase: IGetPrefixesUseCase

    @Inject lateinit var getBlockedCallsCountUseCase: IGetBlockedCallsCountUseCase

    @Inject lateinit var getRecentBlockedCallsUseCase: IGetRecentBlockedCallsUseCase

    @Inject lateinit var getAllowedCallsCountUseCase: IGetAllowedCallsCountUseCase

    @Inject lateinit var getAppSettingsUseCase: IGetAppSettingsUseCase

    @Inject lateinit var getBlockNonContactsUseCase: IGetBlockNonContactsUseCase

    @Inject lateinit var getBlockPrivateNumbersUseCase: IGetBlockPrivateNumbersUseCase

    @Inject lateinit var setBlockNonContactsUseCase: ISetBlockNonContactsUseCase

    @Inject lateinit var setBlockPrivateNumbersUseCase: ISetBlockPrivateNumbersUseCase

    @Inject lateinit var prefixRuleDao: PrefixRuleDao

    @Inject lateinit var blockedCallDao: BlockedCallDao

    @Inject lateinit var allowedCallDao: AllowedCallDao

    @Inject lateinit var blockingPreferencesRepositoryImpl: BlockingPreferencesRepositoryImpl

    private lateinit var viewModel: HomeViewModel

    private fun homeViewModel(
        getPrefixesUseCase: IGetPrefixesUseCase = this.getPrefixesUseCase,
        getBlockedCallsCountUseCase: IGetBlockedCallsCountUseCase = this.getBlockedCallsCountUseCase,
        getRecentBlockedCallsUseCase: IGetRecentBlockedCallsUseCase = this.getRecentBlockedCallsUseCase,
        getAllowedCallsCountUseCase: IGetAllowedCallsCountUseCase = this.getAllowedCallsCountUseCase,
        getAppSettingsUseCase: IGetAppSettingsUseCase = this.getAppSettingsUseCase,
        getBlockNonContactsUseCase: IGetBlockNonContactsUseCase = this.getBlockNonContactsUseCase,
        getBlockPrivateNumbersUseCase: IGetBlockPrivateNumbersUseCase = this.getBlockPrivateNumbersUseCase,
        setBlockNonContactsUseCase: ISetBlockNonContactsUseCase = this.setBlockNonContactsUseCase,
        setBlockPrivateNumbersUseCase: ISetBlockPrivateNumbersUseCase = this.setBlockPrivateNumbersUseCase,
    ): HomeViewModel = HomeViewModel(
        getPrefixesUseCase = getPrefixesUseCase,
        getBlockedCallsCountUseCase = getBlockedCallsCountUseCase,
        getRecentBlockedCallsUseCase = getRecentBlockedCallsUseCase,
        getAllowedCallsCountUseCase = getAllowedCallsCountUseCase,
        getAppSettingsUseCase = getAppSettingsUseCase,
        getBlockNonContactsUseCase = getBlockNonContactsUseCase,
        getBlockPrivateNumbersUseCase = getBlockPrivateNumbersUseCase,
        setBlockNonContactsUseCase = setBlockNonContactsUseCase,
        setBlockPrivateNumbersUseCase = setBlockPrivateNumbersUseCase,
    )

    @Before
    fun setUp() = runTest {
        hiltRule.inject()
        prefixRuleDao.deleteAllPrefixRules()
        blockedCallDao.deleteAllBlockedCalls()
        allowedCallDao.deleteAllAllowedCalls()
        blockingPreferencesRepositoryImpl.setBlockNonContacts(false).getOrThrow()
        blockingPreferencesRepositoryImpl.setBlockPrivateNumbers(false).getOrThrow()
        viewModel = homeViewModel()
        viewModel.updateSystemState(
            isEnabled = false,
            permissionsGranted = false,
            supportsRoleRequest = true
        )
    }

    @Test
    fun should_emit_default_permission_in_invoke() = runTest {
        //WHEN
        val permission = viewModel.permission.first()

        //THEN
        assertEquals(
            Permission(
                isEnabled = false,
                permissionsGranted = false,
                supportsRoleRequest = true
            ),
            permission
        )
    }

    @Test
    fun should_emit_updated_permission_when_update_system_state_called_in_updateSystemState() = runTest {
        //WHEN
        viewModel.updateSystemState(
            isEnabled = true,
            permissionsGranted = true,
            supportsRoleRequest = false
        )

        //THEN
        val permission = viewModel.permission.first()
        assertEquals(
            Permission(
                isEnabled = true,
                permissionsGranted = true,
                supportsRoleRequest = false
            ),
            permission
        )
    }

    @Test
    fun should_emit_false_in_isBlockNonContact_when_no_value_persisted_in_invoke() = runTest {
        //WHEN
        viewModel.isBlockNonContact.test {
            //THEN
            assertFalse(awaitMatches { !it })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_true_in_isBlockNonContact_when_value_persisted_in_invoke() = runTest {
        //GIVEN
        blockingPreferencesRepositoryImpl.setBlockNonContacts(true).getOrThrow()

        //WHEN
        viewModel.isBlockNonContact.test {
            //THEN
            assertTrue(awaitMatches { it })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_false_in_isBlockPrivateNumbers_when_no_value_persisted_in_invoke() = runTest {
        //WHEN
        viewModel.isBlockPrivateNumbers.test {
            //THEN
            assertFalse(awaitMatches { !it })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_true_in_isBlockPrivateNumbers_when_value_persisted_in_invoke() = runTest {
        //GIVEN
        blockingPreferencesRepositoryImpl.setBlockPrivateNumbers(true).getOrThrow()

        //WHEN
        viewModel.isBlockPrivateNumbers.test {
            //THEN
            assertTrue(awaitMatches { it })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_content_with_zero_counts_when_db_empty_in_invoke() = runTest {
        //WHEN
        viewModel.uiState.test {
            val state = awaitMatches { it is HomeUiState.Content } as HomeUiState.Content
            //THEN
            assertEquals(0, state.prefixCount)
            assertEquals(0, state.blockedCount)
            assertEquals(0, state.allowedCount)
            assertEquals(0, state.lastUpdateProgress)
            assertEquals(null, state.lastUpdate)
            assertTrue(state.recentThreats.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_content_with_prefix_count_when_prefix_added_in_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "57", ruleType = "BLOCK"))
        prefixRuleDao.insertPrefixRule(PrefixRuleEntity(prefix = "44", ruleType = "BLOCK"))

        //WHEN
        viewModel.uiState.test {
            val state = awaitMatches {
                it is HomeUiState.Content && it.prefixCount == 2
            } as HomeUiState.Content
            //THEN
            assertEquals(2, state.prefixCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_content_with_blocked_count_when_blocked_call_inserted_in_invoke() = runTest {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(
                phoneNumber = "+57123456789",
                matchedPrefix = "57",
                blockType = BlockedCallEntity.BLOCK_TYPE_PREFIX,
                timestamp = 1_000L
            )
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(
                phoneNumber = "+34900000000",
                matchedPrefix = "34",
                blockType = BlockedCallEntity.BLOCK_TYPE_PREFIX,
                timestamp = 2_000L
            )
        )

        //WHEN
        viewModel.uiState.test {
            val state = awaitMatches {
                it is HomeUiState.Content && it.blockedCount == 2
            } as HomeUiState.Content
            //THEN
            assertEquals(2, state.blockedCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_content_with_allowed_count_when_allowed_call_inserted_in_invoke() = runTest {
        //GIVEN
        allowedCallDao.insertAllowedCall(
            AllowedCallEntity(phoneNumber = "+57123456789", timestamp = 1_000L)
        )
        allowedCallDao.insertAllowedCall(
            AllowedCallEntity(phoneNumber = "+34900000000", timestamp = 2_000L)
        )

        //WHEN
        viewModel.uiState.test {
            val state = awaitMatches {
                it is HomeUiState.Content && it.allowedCount == 2
            } as HomeUiState.Content
            //THEN
            assertEquals(2, state.allowedCount)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_content_with_recent_threats_when_blocked_calls_inserted_in_invoke() = runTest {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(
                phoneNumber = "+57111111111",
                matchedPrefix = "57",
                blockType = BlockedCallEntity.BLOCK_TYPE_PREFIX,
                timestamp = 1_000L
            )
        )
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(
                phoneNumber = "+57222222222",
                matchedPrefix = "57",
                blockType = BlockedCallEntity.BLOCK_TYPE_PREFIX,
                timestamp = 2_000L
            )
        )

        //WHEN
        viewModel.uiState.test {
            val state = awaitMatches {
                it is HomeUiState.Content && it.recentThreats.size == 2
            } as HomeUiState.Content
            //THEN
            assertEquals(2, state.recentThreats.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_content_with_progress_100_when_prefix_added_with_recent_timestamp_in_invoke() = runTest {
        //GIVEN
        prefixRuleDao.insertPrefixRule(
            PrefixRuleEntity(
                prefix = "57",
                ruleType = "BLOCK",
                createdAt = System.currentTimeMillis()
            )
        )

        //WHEN
        viewModel.uiState.test {
            val state = awaitMatches {
                it is HomeUiState.Content &&
                    it.prefixCount == 1 &&
                    it.lastUpdateProgress == 100
            } as HomeUiState.Content
            //THEN
            assertNotNull(state.lastUpdate)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_persist_true_when_on_changed_block_non_contacts_called_in_onChangedBlockNonContacts() = runTest {
        //GIVEN
        viewModel.isBlockNonContact.test {
            assertFalse(awaitMatches { !it })

            //WHEN
            viewModel.onChangedBlockNonContacts(true)

            //THEN
            assertTrue(awaitMatches { it })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_persist_true_when_on_changed_block_private_numbers_called_in_onChangedBlockPrivateNumbers() = runTest {
        //GIVEN
        viewModel.isBlockPrivateNumbers.test {
            assertFalse(awaitMatches { !it })

            //WHEN
            viewModel.onChangedBlockPrivateNumbers(true)

            //THEN
            assertTrue(awaitMatches { it })
            cancelAndIgnoreRemainingEvents()
        }
    }
}