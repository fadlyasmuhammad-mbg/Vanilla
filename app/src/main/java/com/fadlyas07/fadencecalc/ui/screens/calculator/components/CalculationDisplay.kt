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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fadlyas07.fadencecalc.data.calculator.Tokens
import com.fadlyas07.fadencecalc.data.datastore.rememberColoredOperators
import com.fadlyas07.fadencecalc.data.datastore.rememberDecimal
import com.fadlyas07.fadencecalc.data.datastore.rememberIsLandscape
import com.fadlyas07.fadencecalc.data.datastore.rememberUseSystemFont
import com.fadlyas07.fadencecalc.ui.navigation.Screens
import com.fadlyas07.fadencecalc.ui.screens.calculator.CalculatorViewModel
import com.fadlyas07.fadencecalc.ui.theme.nunitoFontFamily
import com.fadlyas07.fadencecalc.utils.formatNumber
import com.fadlyas07.fadencecalc.utils.isErrorMessage

@Composable
fun CalculationDisplay(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel,
    onNavigate: (Screens) -> Unit
) {
    val useSystemFont by rememberUseSystemFont()
    val shouldFormat by rememberDecimal()
    val coloredOperators by rememberColoredOperators()
    val isLandscape = rememberIsLandscape()

    val scrollState = rememberScrollState()
    val previewScrollState = rememberScrollState()

    val previewCanShowErrors by
        viewModel.previewShowErrors.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.textFieldState.text) {
        scrollState.animateScrollTo(
            scrollState.maxValue
        )

        previewScrollState.animateScrollTo(
            previewScrollState.maxValue
        )
    }

    val resultStyle = if (isLandscape) {
        MaterialTheme.typography.headlineLarge
    } else {
        MaterialTheme.typography.displayMedium
    }

    val expressionStyle = if (isLandscape) {
        MaterialTheme.typography.headlineMedium
    } else {
        MaterialTheme.typography.headlineLarge
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            if (isLandscape) 24.dp else 28.dp
        ),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal =
                        if (isLandscape) 16.dp else 20.dp,
                    vertical =
                        if (isLandscape) 14.dp else 16.dp
                ),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = viewModel.evaluatedCalculation
                    .formatNumber(shouldFormat)
                    .takeIf {
                        !it.isErrorMessage() ||
                            previewCanShowErrors
                    }
                    ?: "",
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(previewScrollState),
                style = resultStyle.copy(
                    textAlign = TextAlign.End,
                    fontWeight = FontWeight.Bold,
                    color = if (
                        !viewModel.evaluatedCalculation
                            .isErrorMessage()
                    ) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            )

            Spacer(
                Modifier.height(
                    if (isLandscape) 6.dp else 8.dp
                )
            )

            DisableSoftKeyboard {
                BasicTextField(
                    state = viewModel.textFieldState,
                    lineLimits =
                        TextFieldLineLimits.SingleLine,
                    textStyle = expressionStyle.copy(
                        textAlign = TextAlign.End,
                        color =
                            MaterialTheme.colorScheme.onSurface,
                        fontFamily = if (!useSystemFont) {
                            nunitoFontFamily
                        } else {
                            null
                        },
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    cursorBrush = SolidColor(
                        MaterialTheme.colorScheme.primary
                    ),
                    scrollState = scrollState,
                    outputTransformation =
                        CalculatorOutputTransform(
                            format = shouldFormat,
                            coloredOperators =
                                coloredOperators,
                            operatorColor =
                                MaterialTheme.colorScheme.primary
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

    override fun TextFieldBuffer.transformOutput() {
        if (format) {
            val expression = originalText.toString()

            if (expression.isEmpty()) {
                return
            }

            var shift = 0

            NUMBERS_REGEX
                .findAll(expression)
                .forEach { match ->
                    val start =
                        match.range.first + shift

                    val end =
                        match.range.last + 1 + shift

                    val number = match.value

                    val formatted =
                        number.formatNumber(true)

                    replace(
                        start,
                        end,
                        formatted
                    )

                    shift +=
                        formatted.length - number.length
                }
        }

        if (coloredOperators) {
            val operators = setOf(
                Tokens.ADD,
                Tokens.SUBTRACT,
                Tokens.MULTIPLY,
                Tokens.DIVIDE,
                Tokens.POWER
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
    }

    companion object {
        val NUMBERS_REGEX =
            "[\\d.]+".toRegex()
    }
}
