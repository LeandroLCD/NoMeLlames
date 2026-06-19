package cl.blipblipcode.prefixsapp.ui.prefix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IAddPrefixRuleUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IGetAllPrefixRulesUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IRemovePrefixRuleUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrefixViewModel @Inject constructor(
    getAllPrefixRulesUseCase: IGetAllPrefixRulesUseCase,
    private val addPrefixRuleUseCase: IAddPrefixRuleUseCase,
    private val removePrefixRuleUseCase: IRemovePrefixRuleUseCase
) : ViewModel() {
    
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = getAllPrefixRulesUseCase.invoke().mapLatest { prefixRules ->
        PrefixUiState.Content(
            prefixRules = prefixRules,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, PrefixUiState.Loading)

    private val _errorException = MutableSharedFlow<Throwable>(replay = 0, extraBufferCapacity = 1)
    val errorException: SharedFlow<Throwable> = _errorException.asSharedFlow()

    private var _prefixInput = MutableStateFlow("")
    val prefixInput = _prefixInput.asStateFlow()

    private val _isAllowedRule = MutableStateFlow(false)
    val isAllowedRule = _isAllowedRule.asStateFlow()

    
    fun onPrefixInputChanged(input: String) {
        _prefixInput.tryEmit(input.filter { it.isDigit() || it == ' ' })
    }

    fun onRuleTypeToggled() {
        _isAllowedRule.update {
            !isAllowedRule.value
        }
    }

    fun addPrefix(input: String, isAllowedRule: Boolean) {
        if (input.isBlank()) return

        viewModelScope.launch {
            val ruleType = if (isAllowedRule) PrefixRule.RuleType.ALLOW else PrefixRule.RuleType.BLOCK

            addPrefixRuleUseCase(input, ruleType)
                .onSuccess {
                    // Clear input on success
                    _prefixInput.tryEmit("")
                    _isAllowedRule.tryEmit(false)
                }
                .onFailure { error ->
                    _errorException.emit(error)
                }
        }
    }

    fun removePrefix(prefixRule: PrefixRule) {
        viewModelScope.launch {
            removePrefixRuleUseCase(prefixRule.id)
                .onFailure { error ->
                    _errorException.emit(error)
                }
        }
    }
}
