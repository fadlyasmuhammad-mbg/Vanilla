package com.fadlyas07.fadencecalc.ui.screens.calculator

/**
 * Describes every user interaction accepted by the
 * calculator feature.
 */
sealed interface CalculatorIntent {

    data object Evaluate : CalculatorIntent

    data object Clear : CalculatorIntent

    data object DeletePrevious : CalculatorIntent

    data class InsertSymbol(
        val symbol: Char
    ) : CalculatorIntent

    data class RestoreExpression(
        val expression: String
    ) : CalculatorIntent
}
