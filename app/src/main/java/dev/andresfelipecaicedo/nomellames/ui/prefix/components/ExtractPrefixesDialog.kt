package dev.andresfelipecaicedo.nomellames.ui.prefix.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.andresfelipecaicedo.nomellames.R
import dev.andresfelipecaicedo.nomellames.ui.theme.CyanAccent
import dev.andresfelipecaicedo.nomellames.ui.theme.DarkBg
import dev.andresfelipecaicedo.nomellames.ui.theme.DarkGray
import dev.andresfelipecaicedo.nomellames.ui.theme.DividerColor
import dev.andresfelipecaicedo.nomellames.ui.theme.TextGray

data class SelectablePrefix(
    val prefix: String,
    val isSelected: Boolean = false
)

@Composable
fun ExtractPrefixesDialog(
    prefixes: List<SelectablePrefix>,
    onPrefixSelectionChanged: (String, Boolean) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    val selectedCount = prefixes.count { it.isSelected }
    val allSelected = prefixes.isNotEmpty() && selectedCount == prefixes.size

    Dialog(onDismissRequest = onCancel) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = DarkBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Title
                Text(
                    text = stringResource(R.string.prefix_extract_dialog_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle with count
                Text(
                    text = stringResource(R.string.prefix_extract_dialog_subtitle, prefixes.size),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Select all / Deselect all header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.prefix_extract_selected, selectedCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanAccent
                    )
                    Row {
                        TextButton(onClick = onSelectAll) {
                            Text(
                                text = stringResource(R.string.prefix_extract_select_all),
                                color = if (allSelected) TextGray else CyanAccent
                            )
                        }
                        TextButton(onClick = onDeselectAll) {
                            Text(
                                text = stringResource(R.string.prefix_extract_deselect_all),
                                color = if (selectedCount == 0) TextGray else CyanAccent
                            )
                        }
                    }
                }

                HorizontalDivider(color = DividerColor, thickness = 1.dp)

                // List of prefixes
                if (prefixes.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.prefix_extract_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp)
                    ) {
                        items(prefixes, key = { it.prefix }) { item ->
                            PrefixSelectableItem(
                                prefix = item.prefix,
                                isSelected = item.isSelected,
                                onSelectionChanged = { onPrefixSelectionChanged(item.prefix, it) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onCancel) {
                        Text(
                            text = stringResource(R.string.prefix_extract_cancel),
                            color = TextGray
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = onConfirm,
                        enabled = selectedCount > 0
                    ) {
                        Text(
                            text = stringResource(R.string.prefix_extract_add, selectedCount),
                            color = if (selectedCount > 0) CyanAccent else DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrefixSelectableItem(
    prefix: String,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectionChanged(!isSelected) }
            .background(if (isSelected) CyanAccent.copy(alpha = 0.1f) else Color.Transparent)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectionChanged,
            colors = CheckboxDefaults.colors(
                checkedColor = CyanAccent,
                uncheckedColor = DarkGray,
                checkmarkColor = DarkBg
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "+$prefix",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = Color.White
        )
    }
}

