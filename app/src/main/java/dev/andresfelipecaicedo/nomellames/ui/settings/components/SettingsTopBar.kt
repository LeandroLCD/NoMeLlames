package dev.andresfelipecaicedo.nomellames.ui.settings.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.andresfelipecaicedo.nomellames.R
import dev.andresfelipecaicedo.nomellames.ui.theme.CyanAccent
import dev.andresfelipecaicedo.nomellames.ui.theme.DarkBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTopBar(
    onBackClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = CyanAccent
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_arrow),
                    contentDescription = stringResource(R.string.back),
                    tint = CyanAccent
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBg
        )
    )
}