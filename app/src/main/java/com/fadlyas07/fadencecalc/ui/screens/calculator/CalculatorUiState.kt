package com.fadlyas07.fadencecalc.ui.screens.calculator

/**
 * State tampilan kalkulator.
 *
 * TextFieldState tetap dikelola terpisah karena Compose
 * membutuhkan cursor dan selection state.
 */
data class CalculatorUiState(
    val previewText: String = "",
    val previewIsError: Boolean = false
)
