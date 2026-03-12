package dev.andresfelipecaicedo.nomellames.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.andresfelipecaicedo.nomellames.domain.useCase.allowedcall.IGetAllowedCallsCountUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall.IGetBlockedCallsCountUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall.IGetRecentBlockedCallsUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.prefix.IGetPrefixesUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.settings.IGetAppSettingsUseCase
import dev.andresfelipecaicedo.nomellames.ui.home.model.Permission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getPrefixesUseCase: IGetPrefixesUseCase,
    getBlockedCallsCountUseCase: IGetBlockedCallsCountUseCase,
    getRecentBlockedCallsUseCase: IGetRecentBlockedCallsUseCase,
    getAllowedCallsCountUseCase: IGetAllowedCallsCountUseCase,
    getAppSettingsUseCase: IGetAppSettingsUseCase
) : ViewModel() {

    private val _permission = MutableStateFlow(Permission())
    val permission: StateFlow<Permission> = _permission.asStateFlow()

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

    companion object {
        private const val RECENT_THREATS_LIMIT = 5
    }
}
