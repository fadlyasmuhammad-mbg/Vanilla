package com.fadlyas07.fadencecalc.ui.screens.history

import android.content.ClipData
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.fadlyas07.fadencecalc.R
import com.fadlyas07.fadencecalc.data.datastore.rememberColoredOperators
import com.fadlyas07.fadencecalc.data.datastore.rememberDecimal
import com.fadlyas07.fadencecalc.data.datastore.rememberHistoryNewestFirst
import com.fadlyas07.fadencecalc.data.datastore.rememberUseHistory
import com.fadlyas07.fadencecalc.domain.model.Calculation
import com.fadlyas07.fadencecalc.domain.repository.HistoryEvents
import com.fadlyas07.fadencecalc.ui.screens.history.components.DeletionConfirmationDialog
import com.fadlyas07.fadencecalc.utils.formatExpression
import com.fadlyas07.fadencecalc.utils.formatNumber
import com.fadlyas07.fadencecalc.utils.isErrorMessage
import com.fadlyas07.fadencecalc.utils.isOperator
import com.fadlyas07.fadencecalc.utils.sort

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    calculations: List<Calculation>,
    onEvents: (HistoryEvents) -> Unit,
    onPutBackToField: (String) -> Unit,
    onGotoMain: () -> Unit
) {
    var isHistoryEnabled by rememberUseHistory()
    val newestFirst by rememberHistoryNewestFirst()

    var showDeleteConfirmation by remember {
        mutableStateOf(false)
    }

    val sortedCalculations = remember(
        calculations,
        newestFirst
    ) {
        calculations.sort(newestFirst)
    }

    if (showDeleteConfirmation) {
        DeletionConfirmationDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
            },
            onDelete = {
                showDeleteConfirmation = false
                onEvents(HistoryEvents.DeleteAllCalculation)
            }
        )
    }

    Scaffold(
        topBar = {
            HistoryTopBar(
                historyCount = sortedCalculations.size,
                canClear = isHistoryEnabled &&
                    sortedCalculations.isNotEmpty(),
                onBack = onGotoMain,
                onClearAll = {
                    showDeleteConfirmation = true
                }
            )
        }
    ) { paddingValues ->
        when {
            !isHistoryEnabled -> {
                HistoryDisabledState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .navigationBarsPadding(),
                    onEnableHistory = {
                        isHistoryEnabled = true
                    }
                )
            }

            sortedCalculations.isEmpty() -> {
                EmptyHistoryState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .navigationBarsPadding(),
                    onGoBack = onGotoMain
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom =
                            paddingValues.calculateBottomPadding() +
                            24.dp
                    ),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = sortedCalculations,
                        key = { item -> item.id }
                    ) { calculation ->
                        HistoryCalculationCard(
                            calculation = calculation,
                            onPutBackToField =
                                onPutBackToField,
                            onDelete = {
                                onEvents(
                                    HistoryEvents
                                        .DeleteCalculation(
                                            calculation
                                        )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryTopBar(
    historyCount: Int,
    canClear: Boolean,
    onBack: () -> Unit,
    onClearAll: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "History",
                    style = MaterialTheme
                        .typography
                        .headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                TextButton(
                    onClick = onBack
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.arrow_up
                        ),
                        contentDescription = "Back"
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text("Back")
                }
            },
            actions = {
                if (canClear) {
                    IconButton(
                        onClick = onClearAll
                    ) {
                        Icon(
                            painter = painterResource(
                                R.drawable.delete
                            ),
                            contentDescription =
                                "Clear history",
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .error
                        )
                    }
                }
            },
            colors = TopAppBarDefaults
                .topAppBarColors(
                    containerColor = Color.Transparent
                )
        )

        if (historyCount > 0) {
            Text(
                text = "$historyCount item" +
                    if (historyCount == 1) "" else "s",
                modifier = Modifier
                    .padding(
                        horizontal = 20.dp
                    ),
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HistoryDisabledState(
    modifier: Modifier = Modifier,
    onEnableHistory: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surfaceContainerLow
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    painter = painterResource(
                        R.drawable.history_rounded
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )

                Text(
                    text = stringResource(
                        R.string.history_not_enabled
                    ),
                    style = MaterialTheme
                        .typography
                        .headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text =
                        "Turn history on to save your recent calculations.",
                    style = MaterialTheme
                        .typography
                        .bodyMedium,
                    color = MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
                )

                Button(
                    onClick = onEnableHistory
                ) {
                    Text(
                        stringResource(
                            R.string.enable_history
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryState(
    modifier: Modifier = Modifier,
    onGoBack: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(
                    R.drawable.history_rounded
                ),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )

            Text(
                text = "No history yet",
                style = MaterialTheme
                    .typography
                    .headlineMedium,
                fontWeight = FontWeight.Black
            )

            Text(
                text =
                    "Your recent calculations will appear here.",
                style = MaterialTheme
                    .typography
                    .bodyLarge,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Button(
                onClick = onGoBack
            ) {
                Text("Go to calculator")
            }
        }
    }
}

@Composable
private fun HistoryCalculationCard(
    calculation: Calculation,
    onPutBackToField: (String) -> Unit,
    onDelete: () -> Unit
) {
    val clipboardManager = LocalClipboard.current
    val shouldFormat by rememberDecimal()
    val coloredOperators by rememberColoredOperators()

    Card(
        onClick = {
            onPutBackToField(calculation.operation)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme
                    .colorScheme
                    .surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    calculation.operation
                        .formatExpression(
                            shouldFormat
                        )
                        .forEach { char ->
                            if (
                                coloredOperators &&
                                char.isOperator()
                            ) {
                                withStyle(
                                    SpanStyle(
                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .primary
                                    )
                                ) {
                                    append(char)
                                }
                            } else {
                                append(char)
                            }
                        }
                },
                style = MaterialTheme
                    .typography
                    .titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = calculation.result
                    .formatNumber(shouldFormat),
                style = MaterialTheme
                    .typography
                    .headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color =
                    if (
                        calculation.result
                            .isErrorMessage()
                    ) {
                        MaterialTheme
                            .colorScheme
                            .error
                    } else {
                        MaterialTheme
                            .colorScheme
                            .primary
                    }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        rememberScrollState()
                    ),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                HistoryActionChip(
                    label = stringResource(
                        R.string.put_field
                    ),
                    icon = R.drawable.undo,
                    onClick = {
                        onPutBackToField(
                            calculation.operation
                        )
                    }
                )

                HistoryActionChip(
                    label = stringResource(
                        R.string.copy
                    ),
                    icon = R.drawable.copy,
                    onClick = {
                        clipboardManager
                            .nativeClipboard
                            .setPrimaryClip(
                                ClipData.newPlainText(
                                    "",
                                    "${calculation.operation} = " +
                                        calculation.result
                                )
                            )
                    }
                )

                HistoryActionChip(
                    label = stringResource(
                        R.string.delete
                    ),
                    icon = R.drawable.delete,
                    onClick = onDelete,
                    isDestructive = true
                )
            }
        }
    }
}

@Composable
private fun HistoryActionChip(
    label: String,
    icon: Int,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val contentColor =
        if (isDestructive) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurface
        }

    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = label,
                color = contentColor
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = contentColor
            )
        }
    )
}
