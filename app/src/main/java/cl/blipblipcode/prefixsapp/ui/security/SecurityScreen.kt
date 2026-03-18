package cl.blipblipcode.prefixsapp.ui.security

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import cl.blipblipcode.prefixsapp.BuildConfig
import cl.blipblipcode.prefixsapp.R
import cl.blipblipcode.prefixsapp.ui.settings.components.dialog.PatternInputGrid
import cl.blipblipcode.prefixsapp.ui.theme.BlockedRed
import cl.blipblipcode.prefixsapp.ui.theme.CyanAccent
import cl.blipblipcode.prefixsapp.ui.theme.DarkBg
import cl.blipblipcode.prefixsapp.ui.theme.PrefixsAppTheme
import cl.blipblipcode.prefixsapp.ui.widget.icons.Finger
import cl.blipblipcode.prefixsapp.utils.biometric.BiometricHelper

enum class SecurityMode {
    FINGERPRINT,
    PATTERN
}

@Composable
fun SecurityScreen(
    modifier: Modifier = Modifier,
    showBiometric: Boolean = true,
    showPattern: Boolean = true,
    storedPattern: List<Int> = emptyList(),
    onAuthSuccess: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    
    var currentMode by remember { 
        mutableStateOf(if (showBiometric) SecurityMode.FINGERPRINT else SecurityMode.PATTERN) 
    }
    var authError by remember { mutableStateOf<String?>(null) }
    
    val authTitle = stringResource(R.string.settings_auth_title)
    val authSubtitle = stringResource(R.string.settings_auth_subtitle)
    val authFailedMsg = stringResource(R.string.security_auth_failed)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .drawBehind {
                val gridSize = 40.dp.toPx()
                val strokeWidth = 1.dp.toPx()
                val color = Color.White.copy(alpha = 0.05f)

                for (x in 0..size.width.toInt() step gridSize.toInt()) {
                    drawLine(
                        color = color,
                        start = Offset(x.toFloat(), 0f),
                        end = Offset(x.toFloat(), size.height),
                        strokeWidth = strokeWidth
                    )
                }
                for (y in 0..size.height.toInt() step gridSize.toInt()) {
                    drawLine(
                        color = color,
                        start = Offset(0f, y.toFloat()),
                        end = Offset(size.width, y.toFloat()),
                        strokeWidth = strokeWidth
                    )
                }
            }
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SecurityHeader()

            Spacer(modifier = Modifier.weight(1f))

            when (currentMode) {
                SecurityMode.FINGERPRINT -> {
                    FingerprintScannerSection(
                        onRetry = {
                            activity?.let {
                                authError = null
                                BiometricHelper.authenticate(
                                    activity = activity,
                                    title = authTitle,
                                    subtitle = authSubtitle,
                                    onSuccess = onAuthSuccess,
                                    onError = { error ->
                                        authError = error.ifEmpty { authFailedMsg }
                                    }
                                )
                            }
                        },
                        error = authError
                    )
                }
                SecurityMode.PATTERN -> {
                    PatternLockSection(
                        storedPattern = storedPattern,
                        onPatternCorrect = onAuthSuccess,
                        onPatternError = { authError = it }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Only show mode switcher if both options are available
            if (showBiometric && showPattern) {
                DividerWithText(text = stringResource(R.string.security_or_else))

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = {
                        authError = null
                        currentMode = if (currentMode == SecurityMode.FINGERPRINT) {
                            SecurityMode.PATTERN
                        } else {
                            SecurityMode.FINGERPRINT
                        }
                    }
                ) {
                    Text(
                        text = if (currentMode == SecurityMode.FINGERPRINT) {
                            stringResource(R.string.security_use_pattern)
                        } else {
                            stringResource(R.string.security_use_fingerprint)
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = CyanAccent
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceAround,
                verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .height(56.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.security_cancel),
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                OutlinedButton(
                    onClick = {
                        activity?.let {
                            BiometricHelper.authenticate(
                                activity = it,
                                title = authTitle,
                                subtitle = authSubtitle,
                                onSuccess = onAuthSuccess,
                                onError = { error ->
                                    authError = error.ifEmpty { authFailedMsg }
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .height(56.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(R.string.security_auth),
                        style = MaterialTheme.typography.labelLarge.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

            }

            Spacer(modifier = Modifier.height(24.dp))

            SecurityFooter(version = BuildConfig.VERSION_NAME)
        }
    }
}

@Composable
private fun SecurityHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_shield_lock),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.security_system_safe),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f),
                letterSpacing = 1.sp
            )
            Text(
                text = stringResource(R.string.security_access_restricted),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = Color.White
            )
        }
    }
}

@Composable
private fun FingerprintScannerSection(
    onRetry: () -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    val primaryColor = if (error != null) BlockedRed else MaterialTheme.colorScheme.primary
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .border(
                    width = 1.dp,
                    color = primaryColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                )
                .clickable { onRetry() }
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path().apply {
                    moveTo(0f, size.height / 2)
                    lineTo(size.width, size.height / 2)
                }
                drawPath(
                    path = path,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            primaryColor.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            Icon(
                Icons.Finger,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = error ?: stringResource(R.string.security_scanning),
            style = MaterialTheme.typography.labelLarge.copy(
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Bold
            ),
            color = primaryColor
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.security_touch_sensor),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun DividerWithText(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.1f))
        )
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.3f),
            letterSpacing = 2.sp
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color.White.copy(alpha = 0.1f))
        )
    }
}

@Composable
private fun PatternLockSection(
    storedPattern: List<Int>,
    onPatternCorrect: () -> Unit,
    onPatternError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var enteredPattern by remember { mutableStateOf<List<Int>>(emptyList()) }
    var hasError by remember { mutableStateOf(false) }
    val wrongPatternMsg = stringResource(R.string.security_dialog_wrong_pattern)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.security_enter_pattern),
            style = MaterialTheme.typography.labelLarge.copy(
                letterSpacing = 4.sp,
                fontWeight = FontWeight.Bold
            ),
            color = if (hasError) BlockedRed else MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(40.dp))

        PatternInputGrid(
            selectedNodes = enteredPattern,
            onPatternComplete = { pattern ->
                enteredPattern = pattern
                if (pattern == storedPattern) {
                    hasError = false
                    onPatternCorrect()
                } else {
                    hasError = true
                    onPatternError(wrongPatternMsg)
                    enteredPattern = emptyList()
                }
            },
            errorColor = if (hasError) BlockedRed else null
        )
    }
}

@Composable
private fun SecurityFooter(version: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            color = if (index == 1) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                )
                if (index < 2) Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.security_version_label, version),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.3f)
            )
            Text(
                text = stringResource(R.string.security_encryption_label),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0A0A0A)
@Composable
fun SecurityScreenPreview() {
    PrefixsAppTheme {
        SecurityScreen()
    }
}
