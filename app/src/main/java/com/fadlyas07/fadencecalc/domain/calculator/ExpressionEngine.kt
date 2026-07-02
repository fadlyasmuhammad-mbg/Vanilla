package com.fadlyas07.fadencecalc.domain.calculator

/**
 * Kontrak perhitungan yang tidak bergantung pada ViewModel,
 * Compose, Activity, atau DataStore.
 */
fun interface ExpressionEngine {

    fun evaluate(
        expression: String,
        decimalPrecision: Int
    ): CalculationResult
}
