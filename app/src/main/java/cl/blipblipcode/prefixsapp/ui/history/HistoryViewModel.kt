package cl.blipblipcode.prefixsapp.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.blipblipcode.prefixsapp.domain.useCase.history.IClearAllHistoryUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.history.IExportHistoryToCsvUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.history.IGetCallHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getCallHistoryUseCase: IGetCallHistoryUseCase,
    private val exportHistoryToCsvUseCase: IExportHistoryToCsvUseCase,
    private val clearAllHistoryUseCase: IClearAllHistoryUseCase
) : ViewModel() {


    private val _selectedFilter = MutableStateFlow(IGetCallHistoryUseCase.HistoryFilter.ALL)

    private val _export = MutableStateFlow(Export())
    val export: StateFlow<Export> = _export.asStateFlow()


    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = _selectedFilter
        .flatMapLatest { filter ->
            getCallHistoryUseCase.invoke(filter)
        }.mapLatest { result ->
            HistoryUiState.Content(
                historyItems = result,
                selectedFilter = _selectedFilter.value
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryUiState.Loading
        )


    init {
        observeHistory()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeHistory() {

    }

    fun setFilter(filter: IGetCallHistoryUseCase.HistoryFilter) {
        _selectedFilter.value = filter
    }

    fun exportHistory() {
        val currentState = uiState.value
        if (currentState !is HistoryUiState.Content || !currentState.canExport) return

        viewModelScope.launch {
            _export.update {
                it.copy(isExporting = true)
            }

            exportHistoryToCsvUseCase(currentState.historyItems)
                .onSuccess { filePath ->
                    _export.update {
                        Export(
                            isExporting = false,
                            exportedFilePath = filePath,
                            exportErrorMessage = null
                        )
                    }
                }
                .onFailure { error ->
                    _export.update {
                        Export(
                            isExporting = false,
                            exportedFilePath = null,
                            exportErrorMessage = error.message
                        )
                    }
                }
        }
    }

    fun clearExportMessage() {
        _export.update {
            it.copy(
                exportedFilePath = null,
                exportErrorMessage = null
            )
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            clearAllHistoryUseCase()
        }
    }
}
