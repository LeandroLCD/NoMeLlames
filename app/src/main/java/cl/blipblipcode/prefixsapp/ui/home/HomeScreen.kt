package cl.blipblipcode.prefixsapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cl.blipblipcode.prefixsapp.domain.model.BlockedCall
import cl.blipblipcode.prefixsapp.ui.home.components.FirewallToggle
import cl.blipblipcode.prefixsapp.ui.home.components.RecentThreatsHeader
import cl.blipblipcode.prefixsapp.ui.home.components.RulesUpdateSection
import cl.blipblipcode.prefixsapp.ui.home.components.StatsSection
import cl.blipblipcode.prefixsapp.ui.home.components.StatusSection
import cl.blipblipcode.prefixsapp.ui.home.components.ThreatItem
import cl.blipblipcode.prefixsapp.ui.home.model.Permission
import cl.blipblipcode.prefixsapp.ui.theme.PrefixsAppTheme

@Composable
fun HomeScreen(
    isCallScreeningEnabled: Boolean,
    modifier: Modifier = Modifier,
    permissionsGranted: Boolean,
    supportsRoleRequest: Boolean,
    viewModel: HomeViewModel = hiltViewModel(),
    onGoHistory: () -> Unit,
    onDisableRole: () -> Unit,
    onRequestPermissions: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val permission by viewModel.permission.collectAsState()

    LaunchedEffect(isCallScreeningEnabled, permissionsGranted, supportsRoleRequest) {
        viewModel.updateSystemState(isCallScreeningEnabled, permissionsGranted, supportsRoleRequest)
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is HomeUiState.Content -> {
                HomeContent(
                    state = state,
                    permission,
                    onGoHistory = onGoHistory,
                    onToggleMaster = {
                        if (permission.isActive) onDisableRole() else onRequestPermissions()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    state: HomeUiState.Content,
    permission: Permission,
    onGoHistory: () -> Unit,
    onToggleMaster: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { StatusSection(permission, state.prefixCount) }
        item { FirewallToggle(isEnabled = permission.isActive, onToggle = onToggleMaster) }
        item { StatsSection(blocked = state.blockedCount, allowed = state.allowedCount) }
        state.lastUpdate?.let {
            item {
                RulesUpdateSection(progress = state.lastUpdateProgress, it)
            }
        }
        if (state.recentThreats.isNotEmpty()) {
            stickyHeader {
                RecentThreatsHeader {
                    onGoHistory.invoke()
                }
            }
        }
        items(state.recentThreats, key = { it.id }) { threat ->
            ThreatItem(threat)
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePreview() {
    PrefixsAppTheme(darkTheme = true) {
        HomeContent(
            permission = Permission(
                isEnabled = true,
                permissionsGranted = true,
                supportsRoleRequest = true
            ),
            state = HomeUiState.Content(
                prefixCount = 5,
                blockedCount = 1204,
                allowedCount = 340,
                lastUpdateProgress = 85,
                lastUpdate = null,
                recentThreats = listOf(
                    BlockedCall(1, "+34 91 234 56 78", "SPAM_MADRID_01", 1715868165000),
                    BlockedCall(2, "+44 20 7123 4567", "INTL_BLOCK_UK", 1715859912000),
                    BlockedCall(3, "+34 600 11 22 33", "LISTA_BLANCA_FAMILIA", 1715850725000)
                )
            ),
            onGoHistory = { },
            onToggleMaster = {}
        )
    }
}
