package cl.blipblipcode.prefixsapp.ui.settings.components.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
 * Dialog to enable pattern lock with pattern input
 */
@Composable
fun EnablePatternDialog(
    onPatternSet: (List<Int>) -> Unit,
    onDismiss: () -> Unit
) {
    var pattern by remember { mutableStateOf<List<Int>>(emptyList()) }
    var confirmPattern by remember { mutableStateOf<List<Int>>(emptyList()) }
    var isConfirming by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    
    val errorPatternMismatch = stringResource(R.string.security_dialog_pattern_mismatch)
    val errorPatternTooShort = stringResource(R.string.security_dialog_pattern_too_short)

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
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.security_dialog_enable_pattern_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isConfirming) {
                        stringResource(R.string.security_dialog_confirm_pattern)
                    } else {
                        stringResource(R.string.security_dialog_draw_pattern)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Pattern input grid
                PatternInputGrid(
                    selectedNodes = if (isConfirming) confirmPattern else pattern,
                    onPatternComplete = { newPattern ->
                        error = null
                        if (!isConfirming) {
                            if (newPattern.size < 4) {
                                error = errorPatternTooShort
                                pattern = emptyList()
                            } else {
                                pattern = newPattern
                                isConfirming = true
                            }
                        } else {
                            confirmPattern = newPattern
                            if (newPattern == pattern) {
                                onPatternSet(pattern)
                            } else {
                                error = errorPatternMismatch
                                confirmPattern = emptyList()
                                pattern = emptyList()
                                isConfirming = false
                            }
                        }
                    },
                    errorColor = if (error != null) MaterialTheme.colorScheme.error else null
                )
                Text(
                    text = error.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(R.string.security_cancel),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dialog to disable pattern lock - requires entering the current pattern
 */
@Composable
fun DisablePatternDialog(
    currentPattern: List<Int>,
    onPatternCorrect: () -> Unit,
    onDismiss: () -> Unit
) {
    var enteredPattern by remember { mutableStateOf<List<Int>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    
    val errorWrongPattern = stringResource(R.string.security_dialog_wrong_pattern)

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
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(R.string.security_dialog_disable_pattern_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.security_dialog_enter_current_pattern),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                PatternInputGrid(
                    selectedNodes = enteredPattern,
                    onPatternComplete = { pattern ->
                        enteredPattern = pattern
                        if (pattern == currentPattern) {
                            onPatternCorrect()
                        } else {
                            error = errorWrongPattern
                            enteredPattern = emptyList()
                        }
                    },
                    errorColor = if (error != null) MaterialTheme.colorScheme.error else null,
                    accentColor = MaterialTheme.colorScheme.error
                )

                Text(
                    text = error.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(R.string.security_cancel),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Pattern input component - 3x3 grid
 */
@Composable
fun PatternInputGrid(
    selectedNodes: List<Int>,
    onPatternComplete: (List<Int>) -> Unit,
    modifier: Modifier = Modifier,
    errorColor: Color? = null,
    accentColor: Color = Color.Unspecified
) {
    val resolvedAccentColor = if (accentColor == Color.Unspecified) MaterialTheme.colorScheme.primary else accentColor
    val currentPattern = remember { mutableStateListOf<Int>() }
    var isDragging by remember { mutableStateOf(false) }

    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
    val activeColor = errorColor ?: resolvedAccentColor

    Canvas(
        modifier = modifier
            .size(200.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        currentPattern.clear()
                        val node = getNodeAtPosition(offset, size.width.toFloat())
                        if (node != null && node !in currentPattern) {
                            currentPattern.add(node)
                        }
                    },
                    onDrag = { change, _ ->
                        val node = getNodeAtPosition(change.position, size.width.toFloat())
                        if (node != null && node !in currentPattern) {
                            currentPattern.add(node)
                        }
                    },
                    onDragEnd = {
                        isDragging = false
                        if (currentPattern.isNotEmpty()) {
                            onPatternComplete(currentPattern.toList())
                        }
                    },
                    onDragCancel = {
                        isDragging = false
                        currentPattern.clear()
                    }
                )
            }
    ) {
        val dotRadius = 8.dp.toPx()
        val spacing = size.width / 4

        // Draw nodes
        for (i in 0..2) {
            for (j in 0..2) {
                val nodeIndex = j * 3 + i
                val center = Offset(spacing + i * spacing, spacing + j * spacing)
                val isActive = nodeIndex in (if (isDragging) currentPattern else selectedNodes)

                drawCircle(
                    color = if (isActive) activeColor else inactiveColor,
                    radius = dotRadius,
                    center = center
                )

                if (isActive) {
                    drawCircle(
                        color = activeColor.copy(alpha = 0.2f),
                        radius = dotRadius * 2,
                        center = center
                    )
                }
            }
        }

        // Draw lines connecting selected nodes
        val pattern = if (isDragging) currentPattern else selectedNodes
        if (pattern.size > 1) {
            for (k in 0 until pattern.size - 1) {
                val fromNode = pattern[k]
                val toNode = pattern[k + 1]
                val fromCenter = getNodeCenter(fromNode, spacing)
                val toCenter = getNodeCenter(toNode, spacing)
                drawLine(
                    color = activeColor,
                    start = fromCenter,
                    end = toCenter,
                    strokeWidth = 4.dp.toPx()
                )
            }
        }
    }
}

private fun getNodeAtPosition(offset: Offset, canvasSize: Float): Int? {
    val spacing = canvasSize / 4
    val touchRadius = spacing * 0.6f

    for (i in 0..2) {
        for (j in 0..2) {
            val center = Offset(spacing + i * spacing, spacing + j * spacing)
            if ((offset - center).getDistance() <= touchRadius) {
                return j * 3 + i
            }
        }
    }
    return null
}

private fun getNodeCenter(node: Int, spacing: Float): Offset {
    val i = node % 3
    val j = node / 3
    return Offset(spacing + i * spacing, spacing + j * spacing)
}

@Preview
@Composable
private fun EnablePatternDialogPreview() {
    PrefixsAppTheme {
        EnablePatternDialog(onPatternSet = {}, onDismiss = {})
    }
}

@Preview
@Composable
private fun DisablePatternDialogPreview() {
    PrefixsAppTheme {
        DisablePatternDialog(emptyList(), onPatternCorrect = {}, onDismiss = {})
    }
}

/**
 * Dialog to verify pattern before sensitive operations (e.g., purge database)
 */
@Composable
fun VerifyPatternDialog(
    currentPattern: List<Int>,
    onPatternCorrect: () -> Unit,
    onDismiss: () -> Unit
) {
    var enteredPattern by remember { mutableStateOf<List<Int>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    
    val errorWrongPattern = stringResource(R.string.security_dialog_wrong_pattern)

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
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(R.string.security_dialog_verify_pattern_title),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.security_dialog_enter_pattern_to_continue),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                PatternInputGrid(
                    selectedNodes = enteredPattern,
                    onPatternComplete = { pattern ->
                        enteredPattern = pattern
                        if (pattern == currentPattern) {
                            onPatternCorrect()
                        } else {
                            error = errorWrongPattern
                            enteredPattern = emptyList()
                        }
                    },
                    errorColor = if (error != null) MaterialTheme.colorScheme.error else null
                )

                Text(
                    text = error.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(R.string.security_cancel),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun VerifyPatternDialogPreview() {
    PrefixsAppTheme {
        VerifyPatternDialog(listOf(0, 1, 2), onPatternCorrect = {}, onDismiss = {})
    }
}
