package cl.blipblipcode.prefixsapp.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhoneLocked
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PhoneLocked
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import cl.blipblipcode.prefixsapp.R
import cl.blipblipcode.prefixsapp.domain.exception.PrefixAlreadyExistsException
import cl.blipblipcode.prefixsapp.domain.model.VersionStatus
import cl.blipblipcode.prefixsapp.specialbottombar.components.SpecialBottomBar
import cl.blipblipcode.prefixsapp.specialbottombar.data.SpecialBottom
import cl.blipblipcode.prefixsapp.ui.history.HistoryScreen
import cl.blipblipcode.prefixsapp.ui.history.components.HistoryTopBar
import cl.blipblipcode.prefixsapp.ui.home.HomeScreen
import cl.blipblipcode.prefixsapp.ui.home.components.HomeTopBar
import cl.blipblipcode.prefixsapp.ui.prefix.PrefixScreen
import cl.blipblipcode.prefixsapp.ui.prefix.components.PrefixTopBar
import cl.blipblipcode.prefixsapp.ui.settings.SettingsScreen
import cl.blipblipcode.prefixsapp.ui.settings.components.SettingsTopBar
import cl.blipblipcode.prefixsapp.ui.theme.BlockedRed
import cl.blipblipcode.prefixsapp.ui.widget.dialog.versionControl.UpdateAvailableDialog
import cl.blipblipcode.prefixsapp.ui.widget.dialog.versionControl.UpdateRequiredDialog
import kotlinx.coroutines.launch
import androidx.core.net.toUri

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
            icon = SpecialBottom.Icon.Vector(
                icon = Icons.Outlined.Home,
                activatedIcon = Icons.Filled.Home
            ),
            tag = homeTag,
            id = TabIds.HOME
        ),
        SpecialBottom.Item(
            icon = SpecialBottom.Icon.Vector(
                icon = Icons.Outlined.PhoneLocked,
                activatedIcon = Icons.Filled.PhoneLocked
            ),
            tag = prefixesTag,
            id = TabIds.PREFIXES
        ),
        SpecialBottom.Item(
            icon = SpecialBottom.Icon.Vector(
                icon = Icons.Outlined.History,
                activatedIcon = Icons.Filled.History
            ),
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
            icon = SpecialBottom.Icon.Vector(
                icon = Icons.Outlined.Settings,
                activatedIcon = Icons.Filled.Settings
            ),
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
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val versionStatus by viewModel.versionStatus.collectAsState()
    var dialogDismiss by rememberSaveable {
        mutableStateOf(false)
    }

    Box(Modifier.fillMaxSize()){
        Scaffold(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                AnimatedContent(tabIdCurrent, Modifier) { current ->
                    when (current) {
                        TabIds.HOME -> HomeTopBar(permissionsGranted, scrollBehavior)
                        TabIds.PREFIXES -> PrefixTopBar(
                            scrollBehavior = scrollBehavior
                        )

                        TabIds.HISTORY -> HistoryTopBar()
                        TabIds.SETTINGS -> SettingsTopBar {
                            viewModel.setTabIdCurrent(TabIds.HOME)
                        }
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
                AnimatedContent(tabIdCurrent, Modifier) { current ->
                    when (current) {
                        TabIds.HOME -> HomeScreen(
                            isEnabled = isEnabled,
                            permissionsGranted = permissionsGranted,
                            supportsRoleRequest = supportsRoleRequest,
                            onRequestPermissions = onRequestPermissions,
                            onGoHistory = { viewModel.setTabIdCurrent(TabIds.HISTORY) },
                            onDisableRole = onDisableRole
                        )

                        TabIds.PREFIXES -> PrefixScreen {
                            val message = when (it) {
                                is PrefixAlreadyExistsException -> {
                                    val ruleType = if (it.existingRuleType == "BLOCK") {
                                        context.getString(R.string.prefix_rule_type_block)
                                    } else {
                                        context.getString(R.string.prefix_rule_type_allow)
                                    }
                                    context.getString(
                                        R.string.prefix_error_already_exists,
                                        it.existingPrefix, ruleType
                                    )
                                }

                                else -> context.getString(R.string.prefix_error_generic)
                            }
                            snackbarHostState.showSnackbar(message)
                        }

                        TabIds.HISTORY -> HistoryScreen()
                        TabIds.SETTINGS -> SettingsScreen {
                            scope.launch {
                                snackbarHostState.showSnackbar(it)
                            }
                        }
                    }
                }
            }
        }
        AnimatedContent(versionStatus, Modifier) { current ->
            when (current) {
                is VersionStatus.UpdateAvailable if !dialogDismiss -> {
                    UpdateAvailableDialog(
                        latestVersion = current.latestVersion,
                        onDismiss = { dialogDismiss = true },
                        onConfirm = {
                            val intent = Intent(Intent.ACTION_VIEW, current.urlDownload.toUri())
                            context.startActivity(intent)
                        })
                }
                is VersionStatus.UpdateRequired -> {
                    UpdateRequiredDialog(latestVersion = current.latestVersion){
                        val intent = Intent(Intent.ACTION_VIEW, current.urlDownload.toUri())
                        context.startActivity(intent)
                    }

                }
                else -> {}
            }

        }
    }
}
