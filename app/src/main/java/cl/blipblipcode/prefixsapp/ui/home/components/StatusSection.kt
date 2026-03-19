package cl.blipblipcode.prefixsapp.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.blipblipcode.prefixsapp.R
import cl.blipblipcode.prefixsapp.ui.home.model.Permission
import cl.blipblipcode.prefixsapp.ui.widget.icons.Logo

@Composable
fun StatusSection(permission: Permission, prefixCount: Int) {
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
                    Icons.Logo,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        val systemColor = if (permission.isActive) primaryColor else MaterialTheme.colorScheme.error
        val systemRes = if (permission.isActive) R.string.home_system_active else R.string.home_system_inactive

        Text(
            text = stringResource(systemRes),
            color = systemColor,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        val text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(stringResource(R.string.home_interception))
            }
            append(": ")
            withStyle(
                style = SpanStyle(
                    color = systemColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            ) {
                if (permission.isActive) append("ON") else append("OFF")
            }
        }
        Text(
            text = text,
            modifier = Modifier.padding(top = 8.dp)
        )
        if(prefixCount == 0){
            Text(
                text = "Requiere Configurar Prefijos",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

