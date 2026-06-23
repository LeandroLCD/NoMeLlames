package cl.blipblipcode.prefixsapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.blipblipcode.prefixsapp.domain.useCase.allowedcall.IGetAllowedCallsCountUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blockedcall.IGetBlockedCallsCountUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blockedcall.IGetRecentBlockedCallsUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blocking.IGetBlockNonContactsUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blocking.IGetBlockPrivateNumbersUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blocking.ISetBlockNonContactsUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blocking.ISetBlockPrivateNumbersUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.prefix.IGetPrefixesUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.settings.IGetAppSettingsUseCase
import cl.blipblipcode.prefixsapp.ui.home.model.Permission
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getPrefixesUseCase: IGetPrefixesUseCase,
    getBlockedCallsCountUseCase: IGetBlockedCallsCountUseCase,
    getRecentBlockedCallsUseCase: IGetRecentBlockedCallsUseCase,
    getAllowedCallsCountUseCase: IGetAllowedCallsCountUseCase,
    getAppSettingsUseCase: IGetAppSettingsUseCase,
    getBlockNonContactsUseCase: IGetBlockNonContactsUseCase,
    getBlockPrivateNumbersUseCase: IGetBlockPrivateNumbersUseCase,
    private val setBlockNonContactsUseCase: ISetBlockNonContactsUseCase,
    private val setBlockPrivateNumbersUseCase: ISetBlockPrivateNumbersUseCase
) : ViewModel() {

    private val _permission = MutableStateFlow(Permission())
    val permission: StateFlow<Permission> = _permission.asStateFlow()
    val isBlockNonContact = getBlockNonContactsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )
    val isBlockPrivateNumbers = getBlockPrivateNumbersUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )
    val uiState = combine(
        getPrefixesUseCase(),
        getBlockedCallsCountUseCase(),
        getRecentBlockedCallsUseCase(RECENT_THREATS_LIMIT),
        getAllowedCallsCountUseCase(),
        getAppSettingsUseCase(),
    ) { prefixes, blockedCount, recentCalls, allowedCount, appSettings ->
        HomeUiState.Content(
            prefixCount = prefixes.size,
            blockedCount = blockedCount,
            allowedCount = allowedCount,
            lastUpdateProgress = appSettings.calculateUpdateProgress(),
            recentThreats = recentCalls,
            lastUpdate = appSettings.getLastUpdate()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState.Loading)


    fun updateSystemState(
        isEnabled: Boolean,
        permissionsGranted: Boolean,
        supportsRoleRequest: Boolean
    ) {
        _permission.update {
            it.copy(
                isEnabled = isEnabled,
                permissionsGranted = permissionsGranted,
                supportsRoleRequest = supportsRoleRequest
            )
        }

    }
    fun onChangedBlockNonContacts(value: Boolean) {
        viewModelScope.launch {
            setBlockNonContactsUseCase(value)
        }
    }
    fun onChangedBlockPrivateNumbers(value: Boolean) {
        viewModelScope.launch {
            setBlockPrivateNumbersUseCase(value)
        }
    }


    companion object {
        private const val RECENT_THREATS_LIMIT = 5
    }
}
