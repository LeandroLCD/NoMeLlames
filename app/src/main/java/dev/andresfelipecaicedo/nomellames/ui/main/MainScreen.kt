package dev.andresfelipecaicedo.nomellames.ui.main

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.andresfelipecaicedo.nomellames.R
import dev.andresfelipecaicedo.nomellames.domain.exception.PrefixAlreadyExistsException
import dev.andresfelipecaicedo.nomellames.specialbottombar.components.SpecialBottomBar
import dev.andresfelipecaicedo.nomellames.specialbottombar.data.SpecialBottom
import dev.andresfelipecaicedo.nomellames.ui.history.HistoryScreen
import dev.andresfelipecaicedo.nomellames.ui.home.HomeScreen
import dev.andresfelipecaicedo.nomellames.ui.home.components.HomeTopBar
import dev.andresfelipecaicedo.nomellames.ui.prefix.PrefixScreen
import dev.andresfelipecaicedo.nomellames.ui.prefix.components.PrefixTopBar
import dev.andresfelipecaicedo.nomellames.ui.settings.SettingsScreen
import dev.andresfelipecaicedo.nomellames.ui.theme.BlockedRed

object TabIds {
    val HOME = SpecialBottom.Id("home")
    val PREFIXES = SpecialBottom.Id("prefixes")
    val HISTORY = SpecialBottom.Id("history")
    val SETTINGS = SpecialBottom.Id("settings")
}

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isEnabled: Boolean,
    permissionsGranted: Boolean,
    supportsRoleRequest: Boolean,
    onRequestPermissions: () -> Unit,
    onDisableRole: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val tabIdCurrent by viewModel.tabIdCurrent.collectAsState()
    val unseenCount by viewModel.unseenCount.collectAsState()

    val homeTag = stringResource(R.string.tab_home)
    val prefixesTag = stringResource(R.string.tab_prefixes)
    val historyTag = stringResource(R.string.tab_history)
    val settingsTag = stringResource(R.string.tab_settings)

    val menuItems = listOf(
        SpecialBottom.Item(
            icon = R.drawable.ic_home_outlined,
            activatedIcon = R.drawable.ic_home_filled,
            tag = homeTag,
            id = TabIds.HOME
        ),
        SpecialBottom.Item(
            icon = R.drawable.ic_block_outlined,
            activatedIcon = R.drawable.ic_block_filled,
            tag = prefixesTag,
            id = TabIds.PREFIXES
        ),
        SpecialBottom.Item(
            icon = R.drawable.ic_history_outlined,
            activatedIcon = R.drawable.ic_history_filled,
            tag = historyTag,
            id = TabIds.HISTORY,
            badge = if (unseenCount > 0) {
                SpecialBottom.Badge(
                    text = if (unseenCount > 99) "99+" else unseenCount.toString(),
                    backgroundColor = MaterialTheme.colorScheme.error,
                    textColor = MaterialTheme.colorScheme.onError
                )
            } else null
        ),
        SpecialBottom.Item(
            icon = R.drawable.ic_settings_outlined,
            activatedIcon = R.drawable.ic_settings_filled,
            tag = settingsTag,
            id = TabIds.SETTINGS
        )
    )

    val bottomBarTheme = SpecialBottom.Theme(
        shadowColor = MaterialTheme.colorScheme.onBackground,
        backgroundColor = MaterialTheme.colorScheme.surface,
        selectedColor = MaterialTheme.colorScheme.primary,
        unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            .navigationBarsPadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AnimatedContent (tabIdCurrent) { current->
                when (current) {
                    TabIds.HOME -> HomeTopBar(permissionsGranted, scrollBehavior)
                    TabIds.PREFIXES ->  PrefixTopBar(
                        scrollBehavior = scrollBehavior
                    )
                    TabIds.HISTORY -> HistoryScreen()
                    TabIds.SETTINGS -> SettingsScreen()
                }
            }
        },
        bottomBar = {
            SpecialBottomBar(
                theme = bottomBarTheme,
                menuItems = menuItems,
                currentSelected = tabIdCurrent,
                onItemSelected = { id ->
                    viewModel.setTabIdCurrent(id)
                    if (id == TabIds.HISTORY) {
                        viewModel.markAllAsSeen()
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = BlockedRed,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(4.dp)
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent (tabIdCurrent) { current->
                when (current) {
                    TabIds.HOME -> HomeScreen(
                        isEnabled = isEnabled,
                        permissionsGranted = permissionsGranted,
                        supportsRoleRequest = supportsRoleRequest,
                        onRequestPermissions = onRequestPermissions,
                        onGoHistory = { viewModel.setTabIdCurrent(TabIds.HISTORY) },
                        onDisableRole = onDisableRole
                    )
                    TabIds.PREFIXES -> PrefixScreen{
                        val message = when (val error = it) {
                            is PrefixAlreadyExistsException -> {
                                val ruleType = if (error.existingRuleType == "BLOCK") {
                                    context.getString(R.string.prefix_rule_type_block)
                                } else {
                                    context.getString(R.string.prefix_rule_type_allow)
                                }
                                context.getString(R.string.prefix_error_already_exists, error.existingPrefix, ruleType)
                            }
                            else -> context.getString(R.string.prefix_error_generic)
                        }
                        snackbarHostState.showSnackbar(message)
                    }
                    TabIds.HISTORY -> HistoryScreen()
                    TabIds.SETTINGS -> SettingsScreen()
                }
            }
        }
    }
}
