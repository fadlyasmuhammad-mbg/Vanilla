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
import com.fadlyas07.fadencecalc.data.actions.CalcAction
import com.fadlyas07.fadencecalc.data.datastore.getDecimalPrecision
import com.fadlyas07.fadencecalc.domain.calculator.CalculationResult
import com.fadlyas07.fadencecalc.domain.calculator.ExpressionEngine
import com.fadlyas07.fadencecalc.domain.calculator.KevalExpressionEngine
import com.fadlyas07.fadencecalc.utils.backspace
import com.fadlyas07.fadencecalc.utils.insertText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private var screenState by mutableStateOf(
        CalculatorUiState()
    )

    /**
     * State baru untuk migrasi UI pada tahap berikutnya.
     */
    val uiState: CalculatorUiState
        get() = screenState

    /**
     * Compatibility property agar CalculationDisplay dan
     * history lama masih dapat digunakan.
     */
    val evaluatedCalculation: String
        get() = screenState.previewText

    private val _previewShowErrors =
        MutableStateFlow(false)

    val previewShowErrors =
        _previewShowErrors.asStateFlow()

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

                updatePreview(
                    expressionEngine.evaluate(
                        expression =
                            snapshot.expression,
                        decimalPrecision =
                            snapshot.precision
                    )
                )
            }
        }
    }

    fun handleAction(
        action: CalcAction
    ) {
        hidePreviewError()

        when (action) {
            CalcAction.GetResult -> {
                commitResult()
            }

            is CalcAction.AddToField -> {
                textFieldState.insertText(
                    action.char
                )
            }

            CalcAction.ResetField -> {
                clearCalculator()
            }

            CalcAction.Backspace -> {
                deletePreviousCharacter()
            }

            is CalcAction.AddExpressionToField -> {
                textFieldState
                    .setTextAndPlaceCursorAtEnd(
                        action.expression
                    )
            }
        }
    }

    /**
     * Evaluasi tombol sama dengan dilakukan secara sinkron.
     *
     * Dengan begitu CalculatorScreen bisa langsung membaca
     * evaluatedCalculation untuk menyimpan history tanpa
     * menunggu snapshotFlow.
     */
    private fun commitResult() {
        val result = expressionEngine.evaluate(
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
                showError(
                    result.message
                )
            }
        }
    }

    private fun clearCalculator() {
        screenState = CalculatorUiState()
        _previewShowErrors.value = false

        textFieldState.clearText()
    }

    private fun deletePreviousCharacter() {
        textFieldState.backspace()

        if (textFieldState.text.isEmpty()) {
            screenState = CalculatorUiState()
        }
    }

    private fun updatePreview(
        result: CalculationResult
    ) {
        screenState = when (result) {
            is CalculationResult.Value -> {
                CalculatorUiState(
                    previewText = result.text,
                    previewIsError = false
                )
            }

            CalculationResult.Incomplete -> {
                CalculatorUiState()
            }

            is CalculationResult.Failure -> {
                CalculatorUiState(
                    previewText = result.message,
                    previewIsError = true
                )
            }
        }

        _previewShowErrors.value = false
    }

    private fun showError(
        message: String
    ) {
        screenState = CalculatorUiState(
            previewText = message,
            previewIsError = true
        )

        _previewShowErrors.value = true
    }

    private fun hidePreviewError() {
        _previewShowErrors.value = false
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
