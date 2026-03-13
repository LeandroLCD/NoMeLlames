package dev.andresfelipecaicedo.nomellames.ui.history

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.andresfelipecaicedo.nomellames.R
import dev.andresfelipecaicedo.nomellames.domain.model.HistoryItem
import dev.andresfelipecaicedo.nomellames.domain.useCase.history.IGetCallHistoryUseCase
import dev.andresfelipecaicedo.nomellames.ui.theme.AllowedCyan
import dev.andresfelipecaicedo.nomellames.ui.theme.BlockedRed
import dev.andresfelipecaicedo.nomellames.ui.theme.BlockedRedTransparent
import dev.andresfelipecaicedo.nomellames.ui.theme.CyanAccent
import dev.andresfelipecaicedo.nomellames.ui.theme.DarkBg
import dev.andresfelipecaicedo.nomellames.ui.theme.DarkGray
import dev.andresfelipecaicedo.nomellames.ui.theme.DividerColor
import dev.andresfelipecaicedo.nomellames.ui.theme.NoMeLlamesTheme
import dev.andresfelipecaicedo.nomellames.ui.theme.PurpleAccent
import dev.andresfelipecaicedo.nomellames.ui.theme.TextGray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Handle export message
    LaunchedEffect(uiState) {
        val state = uiState as? HistoryUiState.Content
        state?.exportMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearExportMessage()
        }
    }

    HistoryContentContainer(
        uiState = uiState,
        onFilterSelected = { viewModel.setFilter(it) },
        onExportClick = { viewModel.exportHistory() },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryContentContainer(
    uiState: HistoryUiState,
    modifier: Modifier = Modifier,
    onFilterSelected: (IGetCallHistoryUseCase.HistoryFilter) -> Unit,
    onExportClick: () -> Unit
) {
    val canExport = (uiState as? HistoryUiState.Content)?.canExport == true
    val isExporting = (uiState as? HistoryUiState.Content)?.isExporting == true

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        when (uiState) {
            is HistoryUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CyanAccent)
                }
            }

            is HistoryUiState.Content -> {
                HistoryContent(
                    state = uiState,
                    onFilterSelected = onFilterSelected,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Surface(
            modifier = Modifier.size(56.dp).align(Alignment.BottomEnd).padding(16.dp),
            shape = RoundedCornerShape(4.dp),
            color = DarkBg,
            border = BorderStroke(1.dp, if (canExport) CyanAccent else DarkGray)
        ) {
            IconButton(
                onClick = onExportClick,
                enabled = canExport
            ) {
                if (isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = CyanAccent,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_download),
                        contentDescription = null,
                        tint = if (canExport) CyanAccent else TextGray
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HistoryContent(
    state: HistoryUiState.Content,
    onFilterSelected: (IGetCallHistoryUseCase.HistoryFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Column(modifier = modifier) {
        FilterBar(
            selectedFilter = state.selectedFilter,
            onFilterSelected = onFilterSelected
        )

        HorizontalDivider(color = DividerColor, thickness = 1.dp)

        if (state.isEmpty) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.history_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextGray
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                state.groupedByDate.forEach { (date, items) ->
                    stickyHeader {
                        DateHeader(date = date)
                    }
                    
                    items(
                        items = items,
                        key = { "${it.type}_${it.id}" }
                    ) { item ->
                        HistoryItem(
                            item = item,
                            dateFormat = dateFormat
                        )
                        HorizontalDivider(color = DividerColor, thickness = 1.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DateHeader(date: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkBg
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = PurpleAccent,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun FilterBar(
    selectedFilter: IGetCallHistoryUseCase.HistoryFilter,
    onFilterSelected: (IGetCallHistoryUseCase.HistoryFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilterButton(
            text = stringResource(R.string.history_filter_all),
            isSelected = selectedFilter == IGetCallHistoryUseCase.HistoryFilter.ALL,
            onClick = { onFilterSelected(IGetCallHistoryUseCase.HistoryFilter.ALL) },
            modifier = Modifier.weight(1f)
        )
        FilterButton(
            text = stringResource(R.string.history_filter_blocked),
            isSelected = selectedFilter == IGetCallHistoryUseCase.HistoryFilter.BLOCKED,
            onClick = { onFilterSelected(IGetCallHistoryUseCase.HistoryFilter.BLOCKED) },
            modifier = Modifier.weight(1.5f)
        )
        FilterButton(
            text = stringResource(R.string.history_filter_allowed),
            isSelected = selectedFilter == IGetCallHistoryUseCase.HistoryFilter.ALLOWED,
            onClick = { onFilterSelected(IGetCallHistoryUseCase.HistoryFilter.ALLOWED) },
            modifier = Modifier.weight(1.5f)
        )
    }
}

@Composable
private fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) PurpleAccent else DarkGray
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else TextGray
                )
            )
        }
    }
}

@Composable
private fun HistoryItem(
    item: HistoryItem,
    dateFormat: SimpleDateFormat
) {
    var expanded by remember { mutableStateOf(false) }
    val statusColor = if (item.isBlocked) BlockedRed else AllowedCyan
    val statusText = if (item.isBlocked) {
        stringResource(R.string.history_status_blocked)
    } else {
        stringResource(R.string.history_status_allowed)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dateFormat.format(Date(item.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                modifier = Modifier.width(75.dp)
            )
            Text(
                text = item.phoneNumber,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = statusColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(statusColor)
                )
            }
        }

        AnimatedVisibility(visible = expanded && item.isBlocked) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = stringResource(R.string.history_operator_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGray
                )
                Text(
                    text = stringResource(R.string.history_unknown_operator),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.history_matching_rule_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                item.matchedPrefix?.let { prefix ->
                    Box(
                        modifier = Modifier
                            .border(1.dp, BlockedRed, RoundedCornerShape(2.dp))
                            .background(BlockedRedTransparent)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "[$prefix] - DETERMINISTIC_BLOCK",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = BlockedRed
                        )
                    }
                } ?: run {
                    Text(
                        text = stringResource(R.string.history_no_matched_rule),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HistoryScreenPreview() {
    val sampleItems = listOf(
        HistoryItem(1, "+34 91 555 0192", System.currentTimeMillis(), HistoryItem.CallType.BLOCKED, "+34 91"),
        HistoryItem(2, "+44 7700 900077", System.currentTimeMillis() - 1000 * 60 * 15, HistoryItem.CallType.ALLOWED),
        HistoryItem(3, "+34 600 123 456", System.currentTimeMillis() - 1000 * 60 * 45, HistoryItem.CallType.BLOCKED, "+34 60"),
        HistoryItem(4, "+1 800 555 0199", System.currentTimeMillis() - 1000 * 60 * 60 * 24, HistoryItem.CallType.BLOCKED, "+1 800"),
        HistoryItem(5, "+34 611 222 333", System.currentTimeMillis() - 1000 * 60 * 60 * 48, HistoryItem.CallType.ALLOWED)
    )
    NoMeLlamesTheme(darkTheme = true) {
        HistoryContentContainer(
            uiState = HistoryUiState.Content(historyItems = sampleItems),
            onFilterSelected = {},
            onExportClick = {}
        )
    }
}
