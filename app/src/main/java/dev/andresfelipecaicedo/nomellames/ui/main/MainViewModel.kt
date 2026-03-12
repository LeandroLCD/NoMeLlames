package dev.andresfelipecaicedo.nomellames.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall.IGetUnseenCountUseCase
import dev.andresfelipecaicedo.nomellames.domain.useCase.blockedcall.IMarkAllAsSeenUseCase
import dev.andresfelipecaicedo.nomellames.specialbottombar.data.SpecialBottom
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
    private val markAllAsSeenUseCase: IMarkAllAsSeenUseCase
) : ViewModel() {

    val unseenCount: StateFlow<Int> = getUnseenCountUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
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

