package cl.blipblipcode.prefixsapp.ui.settings.components.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cl.blipblipcode.prefixsapp.R
import cl.blipblipcode.prefixsapp.ui.theme.PrefixsAppTheme

/**
 * Dialog to enable biometric authentication
 */
@Composable
fun EnableBiometricDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    SecurityBaseDialog(
        icon = R.drawable.ic_fingerprint,
        title = stringResource(R.string.security_dialog_enable_biometric_title),
        description = stringResource(R.string.security_dialog_enable_biometric_description),
        confirmText = stringResource(R.string.security_dialog_enable),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

/**
 * Dialog to disable biometric authentication
 */
@Composable
fun DisableBiometricDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    SecurityBaseDialog(
        icon = R.drawable.ic_fingerprint,
        title = stringResource(R.string.security_dialog_disable_biometric_title),
        description = stringResource(R.string.security_dialog_disable_biometric_description),
        confirmText = stringResource(R.string.security_dialog_disable),
        confirmColor = MaterialTheme.colorScheme.error,
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

/**
 * Base dialog component for security dialogs
 */
@Composable
private fun SecurityBaseDialog(
    icon: Int,
    title: String,
    description: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmColor: Color = Color.Unspecified
) {
    val resolvedColor = if (confirmColor == Color.Unspecified) MaterialTheme.colorScheme.primary else confirmColor
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, resolvedColor.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = resolvedColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = resolvedColor.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = resolvedColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(R.string.security_cancel),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    OutlinedButton(
                        onClick = { onConfirm.invoke() },
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, resolvedColor),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = resolvedColor
                        )
                    ) {
                        Text(
                            text = confirmText,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun EnableBiometricDialogPreview() {
    PrefixsAppTheme {
        EnableBiometricDialog(onConfirm = {}, onDismiss = {})
    }
}

@Preview
@Composable
private fun DisableBiometricDialogPreview() {
    PrefixsAppTheme {
        DisableBiometricDialog(onConfirm = {}, onDismiss = {})
    }
}

