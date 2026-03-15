package cl.blipblipcode.prefixsapp.ui.widget.dialog.versionControl

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cl.blipblipcode.prefixsapp.R
import cl.blipblipcode.prefixsapp.ui.theme.CyanAccent
import cl.blipblipcode.prefixsapp.ui.theme.DarkBg
import cl.blipblipcode.prefixsapp.ui.theme.PrefixsAppTheme

@Composable
fun UpdateAvailableDialog(
    latestVersion: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismiss) {
        UpdateAvailableContent(
            latestVersion = stringResource(R.string.v_stable, latestVersion),
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            modifier = modifier
        )
    }
}

@Composable
private fun UpdateAvailableContent(
    latestVersion: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(width = 1.dp, color = CyanAccent.copy(alpha = 0.3f)),
        color = DarkBg,
        shape = RoundedCornerShape(0.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(2.dp)
                .drawBehind {
                    val strokeWidth = 2.dp.toPx()
                    val length = 20.dp.toPx()

                    // Top Left
                    drawLine(CyanAccent, Offset(0f, 0f), Offset(length, 0f), strokeWidth)
                    drawLine(CyanAccent, Offset(0f, 0f), Offset(0f, length), strokeWidth)

                    // Top Right
                    drawLine(
                        CyanAccent,
                        Offset(size.width, 0f),
                        Offset(size.width - length, 0f),
                        strokeWidth
                    )
                    drawLine(
                        CyanAccent,
                        Offset(size.width, 0f),
                        Offset(size.width, length),
                        strokeWidth
                    )

                    // Bottom Left
                    drawLine(
                        CyanAccent,
                        Offset(0f, size.height),
                        Offset(length, size.height),
                        strokeWidth
                    )
                    drawLine(
                        CyanAccent,
                        Offset(0f, size.height),
                        Offset(0f, size.height - length),
                        strokeWidth
                    )

                    // Bottom Right
                    drawLine(
                        CyanAccent,
                        Offset(size.width, size.height),
                        Offset(size.width - length, size.height),
                        strokeWidth
                    )
                    drawLine(
                        CyanAccent,
                        Offset(size.width, size.height),
                        Offset(size.width, size.height - length),
                        strokeWidth
                    )
                }
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = stringResource(R.string.close),
                tint = Color.Gray,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable { onDismiss() }
                    .size(24.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color = CyanAccent, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Download,
                        contentDescription = stringResource(R.string.update_icon_desc),
                        tint = DarkBg,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.update_available_title),
                    color = CyanAccent,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.update_available_description),
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    modifier = Modifier.border(1.dp, CyanAccent),
                    color = Color.Transparent
                ) {
                    Text(
                        text = latestVersion,
                        color = CyanAccent,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.update_available_button_update),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    border = BorderStroke(1.dp, Color.DarkGray),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(0.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.update_available_button_postpone),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun UpdateAvailableDialogPreview() {
    PrefixsAppTheme {
        UpdateAvailableContent(
            latestVersion = "v4.1.0-STABLE",
            onDismiss = {},
            onConfirm = {}
        )
    }
}
