package dev.andresfelipecaicedo.nomellames.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dev.andresfelipecaicedo.nomellames.R
import dev.andresfelipecaicedo.nomellames.domain.model.BlockedCall
import dev.andresfelipecaicedo.nomellames.ui.theme.NoMeLlamesTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    isEnabled: Boolean,
    permissionsGranted: Boolean,
    supportsRoleRequest: Boolean,
    onRequestPermissions: () -> Unit,
    onRequestRole: () -> Unit,
    onDisableRole: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(isEnabled, permissionsGranted, supportsRoleRequest) {
        viewModel.updateSystemState(isEnabled, permissionsGranted, supportsRoleRequest)
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
                    onToggleMaster = {
                        if (state.isActive) onDisableRole() else onRequestRole()
                    }
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState.Content,
    onToggleMaster: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { HomeTopBar() }
        item { StatusSection() }
        item { FirewallToggle(isEnabled = state.isActive, onToggle = onToggleMaster) }
        item { StatsSection(blocked = state.blockedCount, allowed = state.allowedCount) }
        item { RulesUpdateSection(progress = state.lastUpdateProgress) }
        item { RecentThreatsHeader() }
        items(state.recentThreats) { threat ->
            ThreatItem(threat)
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_block_filled),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.home_title),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.home_status_online),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StatusSection() {
    val primaryColor = MaterialTheme.colorScheme.primary
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .drawBehind {
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.05f),
                        radius = size.minDimension / 2,
                        center = center
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(primaryColor.copy(alpha = 0.15f), Color.Transparent),
                            center = center,
                            radius = size.minDimension / 1.5f
                        ),
                        radius = size.minDimension / 1.5f,
                        center = center
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background)
                    .border(2.dp, primaryColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_block_filled),
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.home_system_active),
            color = primaryColor,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.home_interception_on),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun FirewallToggle(isEnabled: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 54.dp, height = 28.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isEnabled) MaterialTheme.colorScheme.primary else Color.DarkGray)
                .clickable { onToggle() }
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(24.dp)
                    .align(if (isEnabled) Alignment.CenterEnd else Alignment.CenterStart)
                    .background(Color.White)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = stringResource(R.string.home_firewall_master),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun StatsSection(blocked: Int, allowed: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            label = stringResource(R.string.home_blocked_label),
            value = String.format(Locale.getDefault(), "%,d", blocked),
            iconId = R.drawable.ic_block_filled,
            accentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = stringResource(R.string.home_allowed_label),
            value = allowed.toString(),
            iconId = R.drawable.ic_history_filled,
            accentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    iconId: Int,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value,
                color = accentColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun RulesUpdateSection(progress: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_last_update_label),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.home_last_update_time),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "$progress%",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RecentThreatsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.home_recent_threats_title),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = stringResource(R.string.home_view_all) + " >",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { /* TODO */ }
        )
    }
}

@Composable
private fun ThreatItem(blockedCall: BlockedCall) {
    val isBlocked = !blockedCall.matchedPrefix.contains("FAMILIA")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(MaterialTheme.colorScheme.surface)
            .drawBehind {
                if (isBlocked) {
                    drawLine(
                        color = Color.Red,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 4.dp.toPx()
                    )
                }
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isBlocked) Color.Red.copy(alpha = 0.1f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = if (isBlocked) R.drawable.ic_block_filled else R.drawable.ic_history_filled),
                contentDescription = null,
                tint = if (isBlocked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = blockedCall.phoneNumber,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Regla: ${blockedCall.matchedPrefix}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Text(
            text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(blockedCall.timestamp)),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePreview() {
    NoMeLlamesTheme(darkTheme = true) {
        HomeContent(
            state = HomeUiState.Content(
                prefixCount = 5,
                isEnabled = true,
                permissionsGranted = true,
                supportsRoleRequest = true,
                blockedCount = 1204,
                allowedCount = 340,
                lastUpdateProgress = 85,
                recentThreats = listOf(
                    BlockedCall(1, "+34 91 234 56 78", "SPAM_MADRID_01", 1715868165000),
                    BlockedCall(2, "+44 20 7123 4567", "INTL_BLOCK_UK", 1715859912000),
                    BlockedCall(3, "+34 600 11 22 33", "LISTA_BLANCA_FAMILIA", 1715850725000)
                )
            ),
            onToggleMaster = {}
        )
    }
}
