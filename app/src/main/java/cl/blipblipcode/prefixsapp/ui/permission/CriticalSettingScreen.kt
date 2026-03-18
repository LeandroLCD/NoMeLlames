package cl.blipblipcode.prefixsapp.ui.permission

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.blipblipcode.prefixsapp.R
import cl.blipblipcode.prefixsapp.ui.theme.PrefixsAppTheme
import cl.blipblipcode.prefixsapp.ui.widget.icons.CallLog
import cl.blipblipcode.prefixsapp.ui.widget.icons.Locked

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriticalSettingScreen(
    modifier: Modifier = Modifier,
    onBackStack: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    val backgroundColor = Color(0xFF102022)
    val primaryColor = Color(0xFF13DDEC)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.drawBehind {
                    drawLine(
                        color = primaryColor.copy(alpha = 0.1f),
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = backgroundColor
                ),
                title = {
                    Text(
                        text = stringResource(R.string.critical_setting_title),
                        style = MaterialTheme.typography.labelLarge,
                        color = primaryColor,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackStack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = primaryColor
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(2.dp))
                            .border(
                                BorderStroke(1.dp, primaryColor.copy(alpha = 0.5f)),
                                RoundedCornerShape(2.dp)
                            )
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Locked,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = primaryColor
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.critical_setting_access_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = stringResource(R.string.critical_setting_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Cyber Border Visual
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .drawCyberBorder(primaryColor)
                        .background(primaryColor.copy(alpha = 0.05f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Grid background pattern
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val step = 20.dp.toPx()
                            for (x in 0 until (size.width / step).toInt()) {
                                for (y in 0 until (size.height / step).toInt()) {
                                    drawCircle(
                                        primaryColor.copy(alpha = 0.1f),
                                        1.dp.toPx(),
                                        Offset(x * step, y * step)
                                    )
                                }
                            }
                        })

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.CallLog,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = Color.White.copy(alpha = 0.3f)
                            )
                            Text(
                                text = stringResource(R.string.critical_call_in),
                                fontSize = 8.sp,
                                color = Color.White.copy(alpha = 0.3f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier
                                .height(2.dp)
                                .width(30.dp)
                                .background(primaryColor.copy(alpha = 0.2f)))
                            Box(modifier = Modifier
                                .size(6.dp)
                                .background(primaryColor, RoundedCornerShape(3.dp)))
                            Box(modifier = Modifier
                                .height(2.dp)
                                .width(30.dp)
                                .background(primaryColor.copy(alpha = 0.2f)))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Locked,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = primaryColor
                            )
                            Text(
                                text = stringResource(R.string.critical_protected),
                                fontSize = 8.sp,
                                color = primaryColor,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                            .background(primaryColor.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                            .border(
                                1.dp,
                                primaryColor.copy(alpha = 0.2f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.critical_system_active),
                            fontSize = 8.sp,
                            color = primaryColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Options List
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SettingOptionItem(
                        title = stringResource(R.string.critical_setting_auto_block_title),
                        subtitle = stringResource(R.string.critical_setting_auto_block_desc),
                        primaryColor = primaryColor
                    )
                }
            }

            // Action Button and Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = backgroundColor
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.critical_setting_set_default),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = stringResource(R.string.critical_setting_android_protocol),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.3f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier
                        .height(1.dp)
                        .width(48.dp)
                        .background(primaryColor.copy(alpha = 0.2f)))
                    Text(
                        text = stringResource(R.string.critical_setting_footer_version),
                        style = MaterialTheme.typography.labelSmall,
                        color = primaryColor.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )
                    Box(modifier = Modifier
                        .height(1.dp)
                        .width(48.dp)
                        .background(primaryColor.copy(alpha = 0.2f)))
                }
            }
        }
    }
}

@Composable
private fun SettingOptionItem(
    title: String,
    subtitle: String,
    primaryColor: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(primaryColor.copy(alpha = 0.05f), RoundedCornerShape(4.dp))
            .border(1.dp, primaryColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = true,
            onCheckedChange = { },
            colors = CheckboxDefaults.colors(
                checkedColor = primaryColor,
                uncheckedColor = primaryColor.copy(alpha = 0.2f),
                checkmarkColor = Color(0xFF102022)
            ),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 11.sp
            )
        }
    }
}

private fun Modifier.drawCyberBorder(color: Color): Modifier = this.drawBehind {
    val strokeWidth = 1.dp.toPx()
    val cornerSize = 10.dp.toPx()
    val accentWidth = 2.dp.toPx()

    // Base border
    drawRect(
        color = color.copy(alpha = 0.3f),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
    )

    // Corners
    // Top-Left
    drawLine(color = color, start = Offset(0f, 0f), end = Offset(cornerSize, 0f), strokeWidth = accentWidth)
    drawLine(color = color, start = Offset(0f, 0f), end = Offset(0f, cornerSize), strokeWidth = accentWidth)

    // Bottom-Right
    drawLine(color = color, start = Offset(size.width, size.height), end = Offset(size.width - cornerSize, size.height), strokeWidth = accentWidth)
    drawLine(color = color, start = Offset(size.width, size.height), end = Offset(size.width, size.height - cornerSize), strokeWidth = accentWidth)
}

@Preview
@Composable
private fun CriticalSettingScreenPreview() {
    PrefixsAppTheme {
        CriticalSettingScreen()
    }
}
