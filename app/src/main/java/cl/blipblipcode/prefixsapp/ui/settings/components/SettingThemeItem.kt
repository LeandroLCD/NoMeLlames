package cl.blipblipcode.prefixsapp.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import cl.blipblipcode.prefixsapp.R
import cl.blipblipcode.prefixsapp.domain.model.ThemeApp
import timber.log.Timber

@Composable
fun SettingThemeItem(theme: ThemeApp,
                     modifier:Modifier = Modifier,
                     onThemeChanged: (ThemeApp) -> Unit) {
    var spander by remember {
        mutableStateOf(false)
    }
    var size by remember {
        mutableStateOf(DpSize.Zero)
    }
    val density = LocalDensity.current

    Column(modifier
        .padding(16.dp)
        .onGloballyPositioned{
            with(density){

                size = it.size.toSize().toDpSize()
            }
            Timber.d("onGloballyPositioned $size")
        }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { spander = !spander },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(0.8f)) {
                Text(
                    text = stringResource(R.string.appearance),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.theme_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                Icons.Outlined.Bedtime,
                modifier = Modifier
                    .weight(0.2f)
                    .padding(horizontal = 8.dp),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )


        }
        DropdownMenu(
            expanded = spander,
            modifier = Modifier.width(size.width),
            onDismissRequest = {
            spander = false
        }) {
            ThemeApp.entries.forEach { tm ->
                val text = when(tm){
                    ThemeApp.System -> stringResource(R.string.system)
                    ThemeApp.Dark -> stringResource(R.string.dark)
                    ThemeApp.Light -> stringResource(R.string.light)
                    ThemeApp.Pink -> stringResource(R.string.pink)
                    ThemeApp.Green -> stringResource(R.string.green)
                    ThemeApp.Solaris -> stringResource(R.string.solaris)
                }

                DropdownMenuItem(text = {
                    Text(text = text, color =  if(tm == theme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                }, onClick = {
                    onThemeChanged(tm)
                    spander = false
                })
            }
        }

    }
}