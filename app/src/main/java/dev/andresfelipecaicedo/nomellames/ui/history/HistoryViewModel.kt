package dev.andresfelipecaicedo.nomellames.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.andresfelipecaicedo.nomellames.domain.useCase.history.IClearAllHistoryUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.history.IExportHistoryToCsvUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.history.IGetCallHistoryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getCallHistoryUseCase: IGetCallHistoryUseCase,
    private val exportHistoryToCsvUseCase: IExportHistoryToCsvUseCase,
    private val clearAllHistoryUseCase: IClearAllHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private val _selectedFilter = MutableStateFlow(IGetCallHistoryUseCase.HistoryFilter.ALL)

    init {
        observeHistory()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeHistory() {
        _selectedFilter
            .flatMapLatest { filter ->
                getCallHistoryUseCase(filter)
            }
            .onEach { historyItems ->
                _uiState.update { currentState ->
                    when (currentState) {
                        is HistoryUiState.Loading -> HistoryUiState.Content(
                            historyItems = historyItems,
                            selectedFilter = _selectedFilter.value
                        )
                        is HistoryUiState.Content -> currentState.copy(
                            historyItems = historyItems,
                            selectedFilter = _selectedFilter.value
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun setFilter(filter: IGetCallHistoryUseCase.HistoryFilter) {
        _selectedFilter.value = filter
    }

    fun exportHistory() {
        val currentState = _uiState.value
        if (currentState !is HistoryUiState.Content || !currentState.canExport) return

        viewModelScope.launch {
            _uiState.update {
                (it as? HistoryUiState.Content)?.copy(isExporting = true) ?: it
            }

            exportHistoryToCsvUseCase(currentState.historyItems)
                .onSuccess { filePath ->
                    _uiState.update {
                        (it as? HistoryUiState.Content)?.copy(
                            isExporting = false,
                            exportMessage = filePath
                        ) ?: it
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        (it as? HistoryUiState.Content)?.copy(
                            isExporting = false,
                            exportMessage = "Error: ${error.message}"
                        ) ?: it
                    }
                }
        }
    }

    fun clearExportMessage() {
        _uiState.update {
            (it as? HistoryUiState.Content)?.copy(exportMessage = null) ?: it
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            clearAllHistoryUseCase()
        }
    }
}
