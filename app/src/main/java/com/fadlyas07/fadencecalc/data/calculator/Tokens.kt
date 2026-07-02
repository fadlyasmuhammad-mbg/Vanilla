package com.fadlyas07.fadencecalc.data.calculator

import com.fadlyas07.fadencecalc.domain.calculator.CalculatorSyntax

/**
 * Compatibility layer untuk UI lama.
 *
 * Setelah CalculatorScreen dimigrasikan ke CalculatorSyntax,
 * file ini dapat dihapus.
 */
object Tokens {

    const val ZERO = CalculatorSyntax.ZERO
    const val ONE = CalculatorSyntax.ONE
    const val TWO = CalculatorSyntax.TWO
    const val THREE = CalculatorSyntax.THREE
    const val FOUR = CalculatorSyntax.FOUR
    const val FIVE = CalculatorSyntax.FIVE
    const val SIX = CalculatorSyntax.SIX
    const val SEVEN = CalculatorSyntax.SEVEN
    const val EIGHT = CalculatorSyntax.EIGHT
    const val NINE = CalculatorSyntax.NINE

    const val DECIMAL = CalculatorSyntax.DECIMAL

    const val ADD = CalculatorSyntax.ADD
    const val SUBTRACT = CalculatorSyntax.SUBTRACT
    const val MULTIPLY = CalculatorSyntax.MULTIPLY
    const val DIVIDE = CalculatorSyntax.DIVIDE
    const val POWER = CalculatorSyntax.POWER

    const val FACTORIAL = CalculatorSyntax.FACTORIAL
    const val MODULO = CalculatorSyntax.PERCENT
    const val SQUARE_ROOT = CalculatorSyntax.SQUARE_ROOT
    const val PI = CalculatorSyntax.PI

    const val OPEN_PARENTHESIS =
        CalculatorSyntax.OPEN_PARENTHESIS

    const val CLOSED_PARENTHESIS =
        CalculatorSyntax.CLOSED_PARENTHESIS
}
