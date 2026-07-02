package com.fadlyas07.fadencecalc.domain.calculator

import com.notkamui.keval.BigDecimal
import com.notkamui.keval.Keval
import com.notkamui.keval.KevalInvalidArgumentException
import com.notkamui.keval.KevalInvalidExpressionException
import com.notkamui.keval.KevalNumbers
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.PI
import kotlin.math.sqrt

/**
 * Implementasi ExpressionEngine menggunakan Keval.
 *
 * Class ini tidak menyimpan hasil perhitungan sebelumnya.
 * Setiap ekspresi selalu menghasilkan Value, Incomplete,
 * atau Failure secara independen.
 */
class KevalExpressionEngine : ExpressionEngine {

    private val parser = Keval.create(
        KevalNumbers.BigDecimal
    ) {
        includeDefault()

        binaryOperator {
            symbol = CalculatorSyntax.MULTIPLY
            precedence = 3
            isLeftAssociative = true

            implementation = { left, right ->
                left * right
            }
        }

        unaryOperator {
            symbol = CalculatorSyntax.FACTORIAL
            isPrefix = false

            implementation = { value ->
                factorial(value)
            }
        }

        unaryOperator {
            symbol = CalculatorSyntax.SQUARE_ROOT
            isPrefix = true

            implementation = { value ->
                squareRoot(value)
            }
        }

        unaryOperator {
            symbol = CalculatorSyntax.PERCENT
            isPrefix = false

            implementation = { value ->
                value.divide(
                    ONE_HUNDRED
                )
            }
        }

        constant {
            name = PI_CONSTANT_NAME
            value = PI.toBigDecimal()
        }
    }

    override fun evaluate(
        expression: String,
        decimalPrecision: Int
    ): CalculationResult {
        if (expression.isBlank()) {
            return CalculationResult.Incomplete
        }

        return try {
            val result = parser
                .eval(
                    normalize(expression)
                )
                .setScale(
                    decimalPrecision.coerceIn(
                        MINIMUM_PRECISION,
                        MAXIMUM_PRECISION
                    ),
                    RoundingMode.HALF_EVEN
                )
                .stripTrailingZeros()
                .toPlainString()

            CalculationResult.Value(result)
        } catch (
            _: KevalInvalidExpressionException
        ) {
            CalculationResult.Incomplete
        } catch (
            exception: Exception
        ) {
            CalculationResult.Failure(
                message = exception.message
                    ?.takeIf(String::isNotBlank)
                    ?: DEFAULT_ERROR_MESSAGE
            )
        }
    }

    /**
     * Mengubah simbol tampilan menjadi syntax yang
     * dipahami parser.
     */
    private fun normalize(
        expression: String
    ): String {
        val withConstants = expression.replace(
            CalculatorSyntax.PI.toString(),
            PI_CONSTANT_NAME
        )

        return relativePercentagePattern.replace(
            withConstants
        ) { match ->
            val base = match.groupValues[1]
            val operator = match.groupValues[2]
            val percentage = match.groupValues[3]

            when (operator) {
                "+" -> {
                    "$base + " +
                        "($base * $percentage / 100)"
                }

                "-" -> {
                    "$base - " +
                        "($base * $percentage / 100)"
                }

                else -> match.value
            }
        }
    }

    private fun factorial(
        value: BigDecimal
    ): BigDecimal {
        if (value < BigDecimal.ZERO) {
            throw KevalInvalidArgumentException(
                "Factorial requires a non-negative number"
            )
        }

        if (!value.isWholeNumber()) {
            throw KevalInvalidArgumentException(
                "Factorial requires a whole number"
            )
        }

        if (
            value >
            MAXIMUM_FACTORIAL_INPUT.toBigDecimal()
        ) {
            throw KevalInvalidArgumentException(
                "Factorial input is too large"
            )
        }

        var result = BigDecimal.ONE
        var factor = TWO

        while (factor <= value) {
            result = result.multiply(factor)
            factor = factor.add(BigDecimal.ONE)
        }

        return result
    }

    private fun squareRoot(
        value: BigDecimal
    ): BigDecimal {
        if (value < BigDecimal.ZERO) {
            throw KevalInvalidArgumentException(
                "Square root is not defined " +
                    "for negative numbers"
            )
        }

        return value.newtonSquareRoot(
            scale = SQUARE_ROOT_SCALE
        )
    }

    private companion object {

        const val PI_CONSTANT_NAME = "PI"

        const val MINIMUM_PRECISION = 0
        const val MAXIMUM_PRECISION = 1000

        const val MAXIMUM_FACTORIAL_INPUT = 5000
        const val SQUARE_ROOT_SCALE = 32

        const val DEFAULT_ERROR_MESSAGE =
            "Unable to calculate expression"

        val TWO = BigDecimal("2")
        val ONE_HUNDRED = BigDecimal("100")

        val relativePercentagePattern = Regex(
            pattern = """
                (-?\d+(?:\.\d+)?)
                \s*
                ([+\-])
                \s*
                (\d+(?:\.\d+)?)
                %
            """.trimIndent(),
            option = RegexOption.COMMENTS
        )
    }
}

private fun BigDecimal.isWholeNumber(): Boolean {
    return remainder(
        BigDecimal.ONE
    ).compareTo(
        BigDecimal.ZERO
    ) == 0
}

/**
 * Newton-Raphson square root untuk BigDecimal.
 *
 * Implementasi dipertahankan kompatibel dengan minSdk
 * project dan tidak membutuhkan API BigDecimal.sqrt().
 */
private fun BigDecimal.newtonSquareRoot(
    scale: Int
): BigDecimal {
    if (compareTo(BigDecimal.ZERO) == 0) {
        return BigDecimal.ZERO
    }

    val two = BigDecimal("2")

    var previous = BigDecimal.ZERO
    var estimate = sqrt(
        toDouble()
    ).toBigDecimal()

    while (previous != estimate) {
        previous = estimate

        estimate = divide(
            previous,
            scale,
            RoundingMode.HALF_UP
        )
            .add(previous)
            .divide(
                two,
                scale,
                RoundingMode.HALF_UP
            )
    }

    return estimate
}
