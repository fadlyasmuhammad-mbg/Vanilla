package com.fadlyas07.fadencecalc.ui.screens.calculator.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fadlyas07.fadencecalc.data.datastore.rememberColoredOperators
import com.fadlyas07.fadencecalc.data.datastore.rememberDecimal
import com.fadlyas07.fadencecalc.data.datastore.rememberIsLandscape
import com.fadlyas07.fadencecalc.data.datastore.rememberUseSystemFont
import com.fadlyas07.fadencecalc.domain.calculator.CalculatorSyntax
import com.fadlyas07.fadencecalc.ui.screens.calculator.CalculatorUiState
import com.fadlyas07.fadencecalc.ui.theme.nunitoFontFamily
import com.fadlyas07.fadencecalc.utils.formatNumber

@Composable
fun CalculationDisplay(
    textFieldState: TextFieldState,
    state: CalculatorUiState,
    modifier: Modifier = Modifier
) {
    val useSystemFont by
        rememberUseSystemFont()

    val shouldFormat by rememberDecimal()

    val coloredOperators by
        rememberColoredOperators()

    val isLandscape = rememberIsLandscape()

    val expressionScrollState =
        rememberScrollState()

    val previewScrollState =
        rememberScrollState()

    LaunchedEffect(textFieldState.text) {
        expressionScrollState.animateScrollTo(
            expressionScrollState.maxValue
        )

        previewScrollState.animateScrollTo(
            previewScrollState.maxValue
        )
    }

    val resultStyle =
        if (isLandscape) {
            MaterialTheme.typography.headlineLarge
        } else {
            MaterialTheme.typography.displayMedium
        }

    val expressionStyle =
        if (isLandscape) {
            MaterialTheme.typography.headlineMedium
        } else {
            MaterialTheme.typography.headlineLarge
        }

    val visiblePreview =
        state.previewText
            .formatNumber(shouldFormat)
            .takeIf {
                !state.previewIsError ||
                    state.revealError
            }
            .orEmpty()

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            if (isLandscape) {
                24.dp
            } else {
                28.dp
            }
        ),
        color =
            MaterialTheme.colorScheme
                .surfaceContainerLow,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal =
                        if (isLandscape) {
                            16.dp
                        } else {
                            20.dp
                        },
                    vertical =
                        if (isLandscape) {
                            14.dp
                        } else {
                            16.dp
                        }
                ),
            verticalArrangement =
                Arrangement.Bottom
        ) {
            Text(
                text = visiblePreview,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(
                        previewScrollState
                    ),
                style = resultStyle.copy(
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold,
                    color =
                        if (state.previewIsError) {
                            MaterialTheme
                                .colorScheme
                                .error
                        } else {
                            MaterialTheme
                                .colorScheme
                                .primary
                        }
                )
            )

            Spacer(
                Modifier.height(
                    if (isLandscape) {
                        6.dp
                    } else {
                        8.dp
                    }
                )
            )

            DisableSoftKeyboard {
                BasicTextField(
                    state = textFieldState,
                    lineLimits =
                        TextFieldLineLimits
                            .SingleLine,
                    textStyle =
                        expressionStyle.copy(
                            textAlign =
                                TextAlign.End,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurface,
                            fontFamily =
                                if (
                                    useSystemFont
                                ) {
                                    null
                                } else {
                                    nunitoFontFamily
                                },
                            fontWeight =
                                FontWeight.SemiBold
                        ),
                    modifier =
                        Modifier.fillMaxWidth(),
                    cursorBrush = SolidColor(
                        MaterialTheme
                            .colorScheme
                            .primary
                    ),
                    scrollState =
                        expressionScrollState,
                    outputTransformation =
                        CalculatorOutputTransform(
                            format =
                                shouldFormat,
                            coloredOperators =
                                coloredOperators,
                            operatorColor =
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        )
                )
            }
        }
    }
}

class CalculatorOutputTransform(
    private val format: Boolean,
    private val coloredOperators: Boolean,
    private val operatorColor: Color
) : OutputTransformation {

    override fun TextFieldBuffer
        .transformOutput() {

        if (format) {
            formatNumbers()
        }

        if (coloredOperators) {
            highlightOperators()
        }
    }

    private fun TextFieldBuffer.formatNumbers() {
        val expression =
            originalText.toString()

        if (expression.isEmpty()) {
            return
        }

        var shift = 0

        NUMBER_PATTERN
            .findAll(expression)
            .forEach { match ->
                val start =
                    match.range.first + shift

                val end =
                    match.range.last +
                        1 +
                        shift

                val originalNumber =
                    match.value

                val formattedNumber =
                    originalNumber
                        .formatNumber(true)

                replace(
                    start,
                    end,
                    formattedNumber
                )

                shift +=
                    formattedNumber.length -
                        originalNumber.length
            }
    }

    private fun TextFieldBuffer
        .highlightOperators() {

        val operators = setOf(
            CalculatorSyntax.ADD,
            CalculatorSyntax.SUBTRACT,
            CalculatorSyntax.MULTIPLY,
            CalculatorSyntax.DIVIDE,
            CalculatorSyntax.POWER
        )

        asCharSequence()
            .forEachIndexed { index, char ->
                if (char in operators) {
                    addStyle(
                        SpanStyle(
                            color = operatorColor
                        ),
                        index,
                        index + 1
                    )
                }
            }
    }

    private companion object {
        val NUMBER_PATTERN =
            Regex("""[\d.]+""")
    }
}
