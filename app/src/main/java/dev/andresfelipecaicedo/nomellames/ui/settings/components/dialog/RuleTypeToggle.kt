package dev.andresfelipecaicedo.nomellames.ui.settings.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.andresfelipecaicedo.nomellames.ui.theme.AllowedCyan
import dev.andresfelipecaicedo.nomellames.ui.theme.BlockedRed
import dev.andresfelipecaicedo.nomellames.ui.theme.CyanAccent

@Composable
fun RuleTypeToggle(
    isAllowed: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit,
) {
    Box(
        modifier = modifier
            .size(width = 54.dp, height = 28.dp)
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