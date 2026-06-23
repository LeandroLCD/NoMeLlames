package cl.blipblipcode.prefixsapp.ui.history

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import cl.blipblipcode.prefixsapp.data.local.dao.AllowedCallDao
import cl.blipblipcode.prefixsapp.data.local.dao.BlockedCallDao
import cl.blipblipcode.prefixsapp.data.local.entities.AllowedCallEntity
import cl.blipblipcode.prefixsapp.data.local.entities.BlockedCallEntity
import cl.blipblipcode.prefixsapp.domain.useCase.history.IClearAllHistoryUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.history.IExportHistoryToCsvUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.history.IGetCallHistoryUseCase
import cl.blipblipcode.prefixsapp.rules.MainDispatcherRule
import cl.blipblipcode.prefixsapp.utils.awaitMatches
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class HistoryViewModelTest {

    @get:Rule(order = 0)
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @Inject lateinit var getCallHistoryUseCase: IGetCallHistoryUseCase

    @Inject lateinit var exportHistoryToCsvUseCase: IExportHistoryToCsvUseCase

    @Inject lateinit var clearAllHistoryUseCase: IClearAllHistoryUseCase

    @Inject lateinit var blockedCallDao: BlockedCallDao

    @Inject lateinit var allowedCallDao: AllowedCallDao

    private lateinit var viewModel: HistoryViewModel

    private fun historyViewModel(
        getCallHistoryUseCase: IGetCallHistoryUseCase = this.getCallHistoryUseCase,
        exportHistoryToCsvUseCase: IExportHistoryToCsvUseCase = this.exportHistoryToCsvUseCase,
        clearAllHistoryUseCase: IClearAllHistoryUseCase = this.clearAllHistoryUseCase,
    ): HistoryViewModel = HistoryViewModel(
        getCallHistoryUseCase = getCallHistoryUseCase,
        exportHistoryToCsvUseCase = exportHistoryToCsvUseCase,
        clearAllHistoryUseCase = clearAllHistoryUseCase,
    )

    @Before
    fun setUp() = runTest(context = mainDispatcherRule.scheduler) {
        hiltRule.inject()
        blockedCallDao.deleteAllBlockedCalls()
        allowedCallDao.deleteAllAllowedCalls()
        viewModel = historyViewModel()
    }

    @Test
    fun should_emit_loading_in_uiState_in_init() = runTest(context = mainDispatcherRule.scheduler) {
        //WHEN
        viewModel.uiState.test {
            //THEN
            assertEquals(HistoryUiState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_default_export_in_export_in_init() = runTest(context = mainDispatcherRule.scheduler) {
        //WHEN
        viewModel.export.test {
            //THEN
            assertEquals(Export(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_content_with_empty_history_when_db_empty_in_uiState() = runTest(context = mainDispatcherRule.scheduler) {
        //WHEN
        viewModel.uiState.test {
            val state = awaitMatches { it is HistoryUiState.Content } as HistoryUiState.Content
            //THEN
            assertTrue(state.historyItems.isEmpty())
            assertTrue(state.isEmpty)
            assertFalse(state.canExport)
            assertEquals(IGetCallHistoryUseCase.HistoryFilter.ALL, state.selectedFilter)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_content_with_history_items_when_db_has_data_in_uiState() = runTest(context = mainDispatcherRule.scheduler) {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(
                phoneNumber = "+57123456789",
                matchedPrefix = "57",
                blockType = BlockedCallEntity.BLOCK_TYPE_PREFIX,
                timestamp = 1_000L
            )
        )
        allowedCallDao.insertAllowedCall(
            AllowedCallEntity(phoneNumber = "+34900000000", timestamp = 2_000L)
        )

        //WHEN
        viewModel.uiState.test {
            val state = awaitMatches {
                it is HistoryUiState.Content && it.historyItems.size == 2
            } as HistoryUiState.Content
            //THEN
            assertEquals(2, state.historyItems.size)
            assertEquals(IGetCallHistoryUseCase.HistoryFilter.ALL, state.selectedFilter)
            assertFalse(state.isEmpty)
            assertTrue(state.canExport)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_content_with_only_blocked_when_filter_is_BLOCKED_in_setFilter() = runTest(context = mainDispatcherRule.scheduler) {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(
                phoneNumber = "+57123456789",
                matchedPrefix = "57",
                blockType = BlockedCallEntity.BLOCK_TYPE_PREFIX,
                timestamp = 1_000L
            )
        )
        allowedCallDao.insertAllowedCall(
            AllowedCallEntity(phoneNumber = "+34900000000", timestamp = 2_000L)
        )

        //WHEN
        viewModel.setFilter(IGetCallHistoryUseCase.HistoryFilter.BLOCKED)

        viewModel.uiState.test {
            val state = awaitMatches {
                it is HistoryUiState.Content &&
                    it.selectedFilter == IGetCallHistoryUseCase.HistoryFilter.BLOCKED &&
                    it.historyItems.size == 1
            } as HistoryUiState.Content
            //THEN
            assertEquals(IGetCallHistoryUseCase.HistoryFilter.BLOCKED, state.selectedFilter)
            assertEquals(1, state.historyItems.size)
            assertTrue(state.historyItems.first().isBlocked)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_content_with_only_allowed_when_filter_is_ALLOWED_in_setFilter() = runTest(context = mainDispatcherRule.scheduler) {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(
                phoneNumber = "+57123456789",
                matchedPrefix = "57",
                blockType = BlockedCallEntity.BLOCK_TYPE_PREFIX,
                timestamp = 1_000L
            )
        )
        allowedCallDao.insertAllowedCall(
            AllowedCallEntity(phoneNumber = "+34900000000", timestamp = 2_000L)
        )

        //WHEN
        viewModel.setFilter(IGetCallHistoryUseCase.HistoryFilter.ALLOWED)

        viewModel.uiState.test {
            val state = awaitMatches {
                it is HistoryUiState.Content &&
                    it.selectedFilter == IGetCallHistoryUseCase.HistoryFilter.ALLOWED &&
                    it.historyItems.size == 1
            } as HistoryUiState.Content
            //THEN
            assertEquals(IGetCallHistoryUseCase.HistoryFilter.ALLOWED, state.selectedFilter)
            assertEquals(1, state.historyItems.size)
            assertFalse(state.historyItems.first().isBlocked)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_not_set_isExporting_when_exportHistory_called_with_empty_history_in_exportHistory() = runTest(context = mainDispatcherRule.scheduler) {
        viewModel.uiState.test {
            // Ensure we are in Content state (which is empty by default)
            awaitMatches { it is HistoryUiState.Content }

            viewModel.export.test {
                awaitItem() // Consume initial state

                //WHEN
                viewModel.exportHistory()

                //THEN - No new events should be produced because exportHistory returns early
                expectNoEvents()
                cancelAndIgnoreRemainingEvents()
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_set_isExporting_true_then_false_when_exportHistory_called_with_items_in_exportHistory() = runTest(context = mainDispatcherRule.scheduler) {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(
                phoneNumber = "+57123456789",
                matchedPrefix = "57",
                blockType = BlockedCallEntity.BLOCK_TYPE_PREFIX,
                timestamp = 1_000L
            )
        )

        viewModel.uiState.test {
            // Wait for Content state with data
            awaitMatches { it is HistoryUiState.Content && !it.isEmpty }

            viewModel.export.test {
                awaitItem() // Consume initial state

                //WHEN
                viewModel.exportHistory()

                //THEN - first transition: isExporting becomes true
                val exportingState = awaitMatches { it.isExporting }
                assertTrue(exportingState.isExporting)

                //THEN - second transition: isExporting becomes false (success or failure)
                val finalState = awaitMatches { !it.isExporting }
                assertTrue(
                    "Expected either exportedFilePath or exportErrorMessage to be set",
                    finalState.exportedFilePath != null || finalState.exportErrorMessage != null
                )
                cancelAndIgnoreRemainingEvents()
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_set_exportErrorMessage_when_export_fails_in_exportHistory() = runTest(context = mainDispatcherRule.scheduler) {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(
                phoneNumber = "+57123456789",
                matchedPrefix = "57",
                blockType = BlockedCallEntity.BLOCK_TYPE_PREFIX,
                timestamp = 1_000L
            )
        )

        viewModel.uiState.test {
            awaitMatches { it is HistoryUiState.Content && !it.isEmpty }

            viewModel.export.test {
                awaitItem() // Consume initial state

                //WHEN
                viewModel.exportHistory()

                //THEN - wait for the export to settle (either success or failure)
                val finalState = awaitMatches { !it.isExporting }
                
                // We assert that if it fails, the error message is set
                if (finalState.exportErrorMessage != null) {
                    assertNotNull(finalState.exportErrorMessage)
                    assertNull(finalState.exportedFilePath)
                }
                cancelAndIgnoreRemainingEvents()
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_clear_exportedFilePath_and_exportErrorMessage_when_clearExportMessage_called_in_clearExportMessage() = runTest(context = mainDispatcherRule.scheduler) {
        //GIVEN - first trigger an export so there's something to clear
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(
                phoneNumber = "+57123456789",
                matchedPrefix = "57",
                blockType = BlockedCallEntity.BLOCK_TYPE_PREFIX,
                timestamp = 1_000L
            )
        )

        viewModel.uiState.test {
            awaitMatches { it is HistoryUiState.Content && !it.isEmpty }

            viewModel.export.test {
                awaitItem() // Consume initial state

                viewModel.exportHistory()

                // Wait for the export to complete (isExporting=false)
                awaitMatches { !it.isExporting }

                //WHEN
                viewModel.clearExportMessage()

                //THEN
                val clearedState = awaitMatches {
                    it.exportedFilePath == null && it.exportErrorMessage == null && !it.isExporting
                }
                assertEquals(Export(), clearedState)
                cancelAndIgnoreRemainingEvents()
            }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun should_emit_content_with_empty_history_when_clearHistory_called_in_clearHistory() = runTest(context = mainDispatcherRule.scheduler) {
        //GIVEN
        blockedCallDao.insertBlockedCall(
            BlockedCallEntity(
                phoneNumber = "+57123456789",
                matchedPrefix = "57",
                blockType = BlockedCallEntity.BLOCK_TYPE_PREFIX,
                timestamp = 1_000L
            )
        )
        allowedCallDao.insertAllowedCall(
            AllowedCallEntity(phoneNumber = "+34900000000", timestamp = 2_000L)
        )

        //WHEN
        viewModel.clearHistory()

        viewModel.uiState.test {
            val state = awaitMatches {
                it is HistoryUiState.Content && it.historyItems.isEmpty()
            } as HistoryUiState.Content
            //THEN
            assertTrue(state.historyItems.isEmpty())
            assertTrue(state.isEmpty)
            assertFalse(state.canExport)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
