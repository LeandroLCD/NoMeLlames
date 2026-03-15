package cl.blipblipcode.prefixsapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.blipblipcode.prefixsapp.domain.model.VersionStatus
import cl.blipblipcode.prefixsapp.domain.useCase.blockedcall.IGetUnseenCountUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.blockedcall.IMarkAllAsSeenUseCase
import cl.blipblipcode.prefixsapp.domain.useCase.version.IObserveVersionStatusUseCase
import cl.blipblipcode.prefixsapp.specialbottombar.data.SpecialBottom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getUnseenCountUseCase: IGetUnseenCountUseCase,
    private val markAllAsSeenUseCase: IMarkAllAsSeenUseCase,
    observeVersionStatusUseCase: IObserveVersionStatusUseCase
) : ViewModel() {

    val unseenCount: StateFlow<Int> = getUnseenCountUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val versionStatus: StateFlow<VersionStatus?> = observeVersionStatusUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = VersionStatus.UpToDate
        )

    private val _tabIdCurrent = MutableStateFlow(TabIds.HOME)
    val tabIdCurrent = _tabIdCurrent.asStateFlow()


    fun setTabIdCurrent(tabId: SpecialBottom.Id) {
        _tabIdCurrent.tryEmit(tabId)
    }

    fun markAllAsSeen() {
        viewModelScope.launch {
            markAllAsSeenUseCase()
        }
    }
}

