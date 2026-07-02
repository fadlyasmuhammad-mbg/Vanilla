package com.fadlyas07.fadencecalc.ui.screens.calculator

/**
 * Immutable state rendered by the calculator UI.
 *
 * TextFieldState remains separate because it also stores
 * cursor position and text selection.
 */
data class CalculatorUiState(
    val previewText: String = "",
    val previewIsError: Boolean = false,
    val revealError: Boolean = false
)
