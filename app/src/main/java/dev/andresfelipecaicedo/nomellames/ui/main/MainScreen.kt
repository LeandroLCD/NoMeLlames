package dev.andresfelipecaicedo.nomellames.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.andresfelipecaicedo.nomellames.R
import dev.andresfelipecaicedo.nomellames.specialbottombar.components.SpecialBottomBar
import dev.andresfelipecaicedo.nomellames.specialbottombar.data.SpecialBottom
import dev.andresfelipecaicedo.nomellames.ui.history.HistoryScreen
import dev.andresfelipecaicedo.nomellames.ui.home.HomeScreen
import dev.andresfelipecaicedo.nomellames.ui.prefix.PrefixScreen
import dev.andresfelipecaicedo.nomellames.ui.settings.SettingsScreen

object TabIds {
    val HOME = SpecialBottom.Id("home")
    val PREFIXES = SpecialBottom.Id("prefixes")
    val HISTORY = SpecialBottom.Id("history")
    val SETTINGS = SpecialBottom.Id("settings")
}

@Composable
fun MainScreen(
    isEnabled: Boolean,
    permissionsGranted: Boolean,
    supportsRoleRequest: Boolean,
    onRequestPermissions: () -> Unit,
    onRequestRole: () -> Unit,
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


    Scaffold(
        modifier = Modifier.background(MaterialTheme.colorScheme.surface).navigationBarsPadding(),
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
                        onRequestRole = onRequestRole,
                        onDisableRole = onDisableRole
                    )
                    TabIds.PREFIXES -> PrefixScreen()
                    TabIds.HISTORY -> HistoryScreen()
                    TabIds.SETTINGS -> SettingsScreen()
                }
            }
        }
    }
}
