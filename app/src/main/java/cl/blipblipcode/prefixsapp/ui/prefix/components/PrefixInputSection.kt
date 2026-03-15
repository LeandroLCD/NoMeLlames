package cl.blipblipcode.prefixsapp.ui.prefix.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cl.blipblipcode.prefixsapp.R
import cl.blipblipcode.prefixsapp.ui.theme.AllowedCyan
import cl.blipblipcode.prefixsapp.ui.theme.BlockedRed
import cl.blipblipcode.prefixsapp.ui.theme.CyanAccent
import cl.blipblipcode.prefixsapp.ui.theme.DarkGray
import cl.blipblipcode.prefixsapp.ui.theme.TextGray

@Composable
fun PrefixInputSection(
    value: String,
    isAllowedRule: Boolean,
    onValueChange: (String) -> Unit,
    onRuleTypeToggled: () -> Unit,
    onAddClick: (String, Boolean) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, DarkGray, RoundedCornerShape(2.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "+",
            style = MaterialTheme.typography.titleLarge,
            color = TextGray
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = stringResource(R.string.prefix_new_placeholder),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextGray
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                cursorBrush = SolidColor(CyanAccent),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        AnimatedVisibility(
            visible = value.isNotEmpty(),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            RuleTypeToggle(
                isAllowed = isAllowedRule,
                onToggle = onRuleTypeToggled,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            onClick = {
                onAddClick(value, isAllowedRule)
            },
            enabled = enabled,
            color = Color.Transparent,
            border = BorderStroke(1.dp, if (enabled) CyanAccent else DarkGray),
            shape = RoundedCornerShape(2.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.prefix_add),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (enabled) CyanAccent else TextGray
                )
            }
        }
    }
}

@Composable
fun RuleTypeToggle(
    isAllowed: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(54.dp)
            .height(28.dp)
            .border(1.dp, CyanAccent, RoundedCornerShape(2.dp))
            .clickable { onToggle() }
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(24.dp)
                .align(if (isAllowed) Alignment.CenterEnd else Alignment.CenterStart)
                .background(if (isAllowed) AllowedCyan else BlockedRed)
        )
    }
}

