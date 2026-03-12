package dev.andresfelipecaicedo.nomellames.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.andresfelipecaicedo.nomellames.domain.useCase.allowedcall.IGetAllowedCallsCountUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall.IGetBlockedCallsCountUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall.IGetRecentBlockedCallsUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.prefix.IGetPrefixesUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.settings.IGetAppSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPrefixesUseCase: IGetPrefixesUseCase,
    private val getBlockedCallsCountUseCase: IGetBlockedCallsCountUseCase,
    private val getRecentBlockedCallsUseCase: IGetRecentBlockedCallsUseCase,
    private val getAllowedCallsCountUseCase: IGetAllowedCallsCountUseCase,
    private val getAppSettingsUseCase: IGetAppSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var isEnabled: Boolean = false
    private var permissionsGranted: Boolean = false
    private var supportsRoleRequest: Boolean = true

    init {
        observeData()
    }

    private fun observeData() {
        combine(
            getPrefixesUseCase(),
            getBlockedCallsCountUseCase(),
            getRecentBlockedCallsUseCase(RECENT_THREATS_LIMIT),
            getAllowedCallsCountUseCase(),
            getAppSettingsUseCase()
        ) { prefixes, blockedCount, recentCalls, allowedCount, appSettings ->
            HomeUiState.Content(
                prefixCount = prefixes.size,
                isEnabled = isEnabled,
                permissionsGranted = permissionsGranted,
                supportsRoleRequest = supportsRoleRequest,
                blockedCount = blockedCount,
                allowedCount = allowedCount,
                lastUpdateProgress = appSettings.calculateUpdateProgress(),
                recentThreats = recentCalls
            )
        }
        .onEach { content ->
            _uiState.value = content
        }
        .launchIn(viewModelScope)
    }

    fun updateSystemState(
        isEnabled: Boolean,
        permissionsGranted: Boolean,
        supportsRoleRequest: Boolean
    ) {
        this.isEnabled = isEnabled
        this.permissionsGranted = permissionsGranted
        this.supportsRoleRequest = supportsRoleRequest
        
        val currentState = _uiState.value
        if (currentState is HomeUiState.Content) {
            _uiState.update {
                currentState.copy(
                    isEnabled = isEnabled,
                    permissionsGranted = permissionsGranted,
                    supportsRoleRequest = supportsRoleRequest
                )
            }
        }
    }

    companion object {
        private const val RECENT_THREATS_LIMIT = 5
    }
}
