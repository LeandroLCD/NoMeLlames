package cl.blipblipcode.prefixsapp.ui.prefix.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.blipblipcode.prefixsapp.R
import cl.blipblipcode.prefixsapp.domain.model.PrefixRule

@Composable
fun PrefixList(
    prefixRules: List<PrefixRule>,
    onRemovePrefix: (PrefixRule) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(
            items = prefixRules,
            key = { it.id }
        ) { prefixRule ->
            PrefixItem(
                prefixRule = prefixRule,
                onRemove = { onRemovePrefix(prefixRule) }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
        }
    }
}

@Composable
fun PrefixItem(
    prefixRule: PrefixRule,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = if (prefixRule.isBlocked) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    val statusText = if (prefixRule.isBlocked) {
        stringResource(R.string.prefix_status_blocked)
    } else {
        stringResource(R.string.prefix_status_allowed)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = Color.Transparent,
            border = BorderStroke(1.dp, statusColor),
            shape = RoundedCornerShape(2.dp)
        ) {
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = statusColor,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = prefixRule.prefix,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onRemove) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = stringResource(R.string.prefix_delete),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

