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
import com.fadlyas07.fadencecalc.data.calculator.Evaluator
import com.fadlyas07.fadencecalc.data.datastore.getDecimalPrecision
import com.fadlyas07.fadencecalc.utils.backspace
import com.fadlyas07.fadencecalc.utils.insertText
import com.fadlyas07.fadencecalc.utils.isErrorMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CalculatorViewModel(
    private val application: Application
) : AndroidViewModel(application) {


    val textFieldState = TextFieldState()
    var evaluatedCalculation by mutableStateOf("")
        private set

    private val _previewShowErrors = MutableStateFlow(false)
    val previewShowErrors = _previewShowErrors.asStateFlow()


    init {
        viewModelScope.launch {
            snapshotFlow { textFieldState.text.toString() }
                .collectLatest { text ->
                    val decimalPrecision =
                        getDecimalPrecision(application.applicationContext).first()
                    evaluatedCalculation = if (textFieldState.text.isEmpty()) {
                        ""
                    } else {
                        Evaluator.eval(text, decimalPrecision)
                    }
                }
        }
    }

    fun handleAction(action: CalcAction) {
        _previewShowErrors.update { false }

        when (action) {
            is CalcAction.GetResult -> {
                if (evaluatedCalculation.isErrorMessage()) {
                    _previewShowErrors.update { true }
                } else {
                    textFieldState.setTextAndPlaceCursorAtEnd(evaluatedCalculation)
                }
            }

            is CalcAction.AddToField -> textFieldState.insertText(action.char)
            is CalcAction.ResetField -> textFieldState.clearText()
            is CalcAction.Backspace -> textFieldState.backspace()
            is CalcAction.AddExpressionToField -> textFieldState.setTextAndPlaceCursorAtEnd(action.expression)
        }
    }

}