package dev.andresfelipecaicedo.nomellames.ui.history.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.andresfelipecaicedo.nomellames.R
import dev.andresfelipecaicedo.nomellames.ui.theme.CyanAccent
import dev.andresfelipecaicedo.nomellames.ui.theme.DarkBg
import dev.andresfelipecaicedo.nomellames.ui.widget.icons.CallLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTopBar(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.history_title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    color = CyanAccent
                )
            )
        },
        navigationIcon = {
            Icon(
                Icons.CallLog,
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(24.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBg
        )
    )
}