package dev.andresfelipecaicedo.nomellames.ui.prefix

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.andresfelipecaicedo.nomellames.R
import dev.andresfelipecaicedo.nomellames.domain.exception.PrefixAlreadyExistsException
import dev.andresfelipecaicedo.nomellames.domain.model.PrefixRule
import dev.andresfelipecaicedo.nomellames.ui.theme.AllowedCyan
import dev.andresfelipecaicedo.nomellames.ui.theme.BlockedRed
import dev.andresfelipecaicedo.nomellames.ui.theme.CyanAccent
import dev.andresfelipecaicedo.nomellames.ui.theme.DarkBg
import dev.andresfelipecaicedo.nomellames.ui.theme.DarkGray
import dev.andresfelipecaicedo.nomellames.ui.theme.DividerColor
import dev.andresfelipecaicedo.nomellames.ui.theme.NoMeLlamesTheme
import dev.andresfelipecaicedo.nomellames.ui.theme.TextGray

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun PrefixScreen(
    modifier: Modifier = Modifier,
    viewModel: PrefixViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val errorException by viewModel.errorException.collectAsStateWithLifecycle(null)
    val inputPrefix by viewModel.prefixInput.collectAsState()
    val isAllowedRule by viewModel.isAllowedRule.collectAsState()

    LaunchedEffect(errorException) {
        if(errorException != null) {
            val message = when (val error = errorException) {
                is PrefixAlreadyExistsException -> {
                    val ruleType = if (error.existingRuleType == "BLOCK") {
                        context.getString(R.string.prefix_rule_type_block)
                    } else {
                        context.getString(R.string.prefix_rule_type_allow)
                    }
                    context.getString(R.string.prefix_error_already_exists, error.existingPrefix, ruleType)
                }
                else -> context.getString(R.string.prefix_error_generic)
            }
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = modifier,
        containerColor = DarkBg,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = BlockedRed,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(4.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is PrefixUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyanAccent)
                    }
                }
                is PrefixUiState.Content -> {
                    PrefixContent(
                        state = state,
                        onPrefixInputChanged = viewModel::onPrefixInputChanged,
                        onRuleTypeToggled = viewModel::onRuleTypeToggled,
                        onAddPrefix = viewModel::addPrefix,
                        onRemovePrefix = viewModel::removePrefix,
                        inputPrefix = inputPrefix,
                        isAllowedRule = isAllowedRule
                    )
                }
            }
        }
    }
}

@Composable
private fun PrefixContent(
    inputPrefix: String,
    modifier: Modifier = Modifier,
    isAllowedRule: Boolean = false,
    state: PrefixUiState.Content,
    onPrefixInputChanged: (String) -> Unit,
    onRuleTypeToggled: () -> Unit,
    onAddPrefix: (String, Boolean) -> Unit,
    onRemovePrefix: (PrefixRule) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        PrefixHeader(rulesCount = state.prefixRules.size)

        Spacer(modifier = Modifier.height(24.dp))

        PrefixInputSection(
            value = inputPrefix,
            isAllowedRule = isAllowedRule,
            onValueChange = onPrefixInputChanged,
            onRuleTypeToggled = onRuleTypeToggled,
            onAddClick = onAddPrefix,
            enabled = inputPrefix.isNotBlank(),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(color = DividerColor, thickness = 1.dp)

        if (state.isEmpty) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.prefix_empty),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextGray
                )
            }
        } else {
            PrefixList(
                prefixRules = state.prefixRules,
                onRemovePrefix = onRemovePrefix,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PrefixHeader(
    rulesCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_block_filled),
                contentDescription = null,
                tint = CyanAccent,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.prefix_title),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = Color.White
            )
        }

        Surface(
            color = Color.Transparent,
            border = BorderStroke(1.dp, CyanAccent),
            shape = RoundedCornerShape(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(CyanAccent)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.prefix_active_rules, rulesCount),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = CyanAccent
                )
            }
        }
    }
}

@Composable
private fun PrefixInputSection(
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
private fun RuleTypeToggle(
    isAllowed: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
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

@Composable
private fun PrefixList(
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
            HorizontalDivider(color = DividerColor, thickness = 1.dp)
        }
    }
}

@Composable
private fun PrefixItem(
    prefixRule: PrefixRule,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = if (prefixRule.isBlocked) BlockedRed else AllowedCyan
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
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = stringResource(R.string.prefix_delete),
                tint = DarkGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrefixScreenPreview() {
    val samplePrefixes = listOf(
        PrefixRule(1, "34 91", PrefixRule.RuleType.BLOCK),
        PrefixRule(2, "44 800", PrefixRule.RuleType.BLOCK),
        PrefixRule(3, "1 888", PrefixRule.RuleType.ALLOW),
        PrefixRule(4, "34 600 12", PrefixRule.RuleType.BLOCK),
        PrefixRule(5, "34 93", PrefixRule.RuleType.ALLOW)
    )
    NoMeLlamesTheme(darkTheme = true) {
        Box(modifier = Modifier.background(DarkBg)) {
            PrefixContent(
                inputPrefix = "",
                state = PrefixUiState.Content(
                    prefixRules = samplePrefixes
                ),
                onPrefixInputChanged = {},
                onRuleTypeToggled = {},
                onAddPrefix = {_,_ ->},
                onRemovePrefix = {}
            )
        }
    }
}
