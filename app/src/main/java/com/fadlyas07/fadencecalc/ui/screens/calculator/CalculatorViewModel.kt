package com.fadlyas07.fadencecalc.ui.screens.calculator

import android.app.Application
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fadlyas07.fadencecalc.data.datastore.getDecimalPrecision
import com.fadlyas07.fadencecalc.domain.calculator.CalculationResult
import com.fadlyas07.fadencecalc.domain.calculator.ExpressionEngine
import com.fadlyas07.fadencecalc.domain.calculator.KevalExpressionEngine
import com.fadlyas07.fadencecalc.utils.backspace
import com.fadlyas07.fadencecalc.utils.insertText
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class CalculatorViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val expressionEngine: ExpressionEngine =
        KevalExpressionEngine()

    val textFieldState = TextFieldState()

    var uiState by mutableStateOf(
        CalculatorUiState()
    )
        private set

    /**
     * Temporary compatibility property for the existing
     * history integration.
     *
     * It can be removed after CalculatorRoute owns history.
     */
    val evaluatedCalculation: String
        get() = uiState.previewText

    private var activeDecimalPrecision =
        DEFAULT_DECIMAL_PRECISION

    init {
        observeExpression()
    }

    private fun observeExpression() {
        viewModelScope.launch {
            combine(
                snapshotFlow {
                    textFieldState.text.toString()
                }.distinctUntilChanged(),

                getDecimalPrecision(
                    getApplication<Application>()
                        .applicationContext
                ).distinctUntilChanged()
            ) { expression, precision ->
                ExpressionSnapshot(
                    expression = expression,
                    precision = precision
                )
            }.collectLatest { snapshot ->
                activeDecimalPrecision =
                    snapshot.precision

                val result =
                    expressionEngine.evaluate(
                        expression =
                            snapshot.expression,
                        decimalPrecision =
                            snapshot.precision
                    )

                updatePreview(result)
            }
        }
    }

    fun onIntent(
        intent: CalculatorIntent
    ) {
        hideError()

        when (intent) {
            CalculatorIntent.Evaluate -> {
                evaluateAndCommit()
            }

            CalculatorIntent.Clear -> {
                clearCalculator()
            }

            CalculatorIntent.DeletePrevious -> {
                deletePreviousCharacter()
            }

            is CalculatorIntent.InsertSymbol -> {
                textFieldState.insertText(
                    intent.symbol
                )
            }

            is CalculatorIntent.RestoreExpression -> {
                textFieldState
                    .setTextAndPlaceCursorAtEnd(
                        intent.expression
                    )
            }
        }
    }

    /**
     * Evaluation is performed synchronously so the existing
     * history code can read the committed result immediately.
     */
    private fun evaluateAndCommit() {
        val result =
            expressionEngine.evaluate(
                expression =
                    textFieldState.text.toString(),
                decimalPrecision =
                    activeDecimalPrecision
            )

        when (result) {
            is CalculationResult.Value -> {
                updatePreview(result)

                textFieldState
                    .setTextAndPlaceCursorAtEnd(
                        result.text
                    )
            }

            CalculationResult.Incomplete -> {
                showError(
                    INVALID_EXPRESSION_MESSAGE
                )
            }

            is CalculationResult.Failure -> {
                showError(result.message)
            }
        }
    }

    private fun clearCalculator() {
        uiState = CalculatorUiState()
        textFieldState.clearText()
    }

    private fun deletePreviousCharacter() {
        textFieldState.backspace()

        if (textFieldState.text.isEmpty()) {
            uiState = CalculatorUiState()
        }
    }

    private fun updatePreview(
        result: CalculationResult
    ) {
        uiState = when (result) {
            is CalculationResult.Value -> {
                CalculatorUiState(
                    previewText = result.text
                )
            }

            CalculationResult.Incomplete -> {
                CalculatorUiState()
            }

            is CalculationResult.Failure -> {
                CalculatorUiState(
                    previewText = result.message,
                    previewIsError = true,
                    revealError = false
                )
            }
        }
    }

    private fun showError(
        message: String
    ) {
        uiState = CalculatorUiState(
            previewText = message,
            previewIsError = true,
            revealError = true
        )
    }

    private fun hideError() {
        if (uiState.revealError) {
            uiState = uiState.copy(
                revealError = false
            )
        }
    }

    private data class ExpressionSnapshot(
        val expression: String,
        val precision: Int
    )

    private companion object {

        const val DEFAULT_DECIMAL_PRECISION = 1000

        const val INVALID_EXPRESSION_MESSAGE =
            "Invalid expression"
    }
}
