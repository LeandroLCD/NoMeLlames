package cl.blipblipcode.prefixsapp.ui.prefix

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cl.blipblipcode.prefixsapp.R
import cl.blipblipcode.prefixsapp.domain.model.PrefixRule
import cl.blipblipcode.prefixsapp.ui.prefix.components.PrefixInputSection
import cl.blipblipcode.prefixsapp.ui.prefix.components.PrefixList
import cl.blipblipcode.prefixsapp.ui.theme.CyanAccent
import cl.blipblipcode.prefixsapp.ui.theme.DividerColor
import cl.blipblipcode.prefixsapp.ui.theme.TextGray

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrefixScreen(
    modifier: Modifier = Modifier,
    viewModel: PrefixViewModel = hiltViewModel(),
    showErrorException: suspend (Throwable)-> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val errorException by viewModel.errorException.collectAsStateWithLifecycle(null)
    val inputPrefix by viewModel.prefixInput.collectAsState()
    val isAllowedRule by viewModel.isAllowedRule.collectAsState()


    LaunchedEffect(errorException) {
        if (errorException != null) {
            showErrorException.invoke(errorException!!)
        }
    }

    val rulesCount = (uiState as? PrefixUiState.Content)?.prefixRules?.size ?: 0

    Box(
        modifier = modifier
            .fillMaxSize()
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
                    rulesCount = rulesCount,
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

@Composable
private fun PrefixContent(
    inputPrefix: String,
    modifier: Modifier = Modifier,
    isAllowedRule: Boolean = false,
    rulesCount: Int,
    state: PrefixUiState.Content,
    onPrefixInputChanged: (String) -> Unit,
    onRuleTypeToggled: () -> Unit,
    onAddPrefix: (String, Boolean) -> Unit,
    onRemovePrefix: (PrefixRule) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {

        PrefixInputSection(
            value = inputPrefix,
            isAllowedRule = isAllowedRule,
            onValueChange = onPrefixInputChanged,
            onRuleTypeToggled = onRuleTypeToggled,
            onAddClick = onAddPrefix,
            enabled = inputPrefix.isNotBlank(),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            color = Color.Transparent,
            modifier = Modifier.align(Alignment.End).padding(horizontal = 16.dp),
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
        Spacer(modifier = Modifier.height(8.dp))

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
