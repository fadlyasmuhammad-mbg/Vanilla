package com.fadlyas07.fadencecalc.domain.calculator

/**
 * Seluruh simbol yang dipahami kalkulator.
 *
 * Domain memiliki simbolnya sendiri sehingga expression
 * engine tidak bergantung pada package UI atau data lama.
 */
object CalculatorSyntax {

    const val ZERO = '0'
    const val ONE = '1'
    const val TWO = '2'
    const val THREE = '3'
    const val FOUR = '4'
    const val FIVE = '5'
    const val SIX = '6'
    const val SEVEN = '7'
    const val EIGHT = '8'
    const val NINE = '9'

    const val DECIMAL = '.'

    const val ADD = '+'
    const val SUBTRACT = '-'
    const val MULTIPLY = '×'
    const val DIVIDE = '/'
    const val POWER = '^'

    const val FACTORIAL = '!'
    const val PERCENT = '%'
    const val SQUARE_ROOT = '√'
    const val PI = 'π'

    const val OPEN_PARENTHESIS = '('
    const val CLOSED_PARENTHESIS = ')'
}
