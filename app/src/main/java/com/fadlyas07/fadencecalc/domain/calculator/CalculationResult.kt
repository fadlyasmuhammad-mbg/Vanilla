package com.fadlyas07.fadencecalc.domain.calculator

/**
 * Hasil evaluasi tidak lagi menggunakan String untuk
 * mewakili semua keadaan.
 */
sealed interface CalculationResult {

    data class Value(
        val text: String
    ) : CalculationResult

    /**
     * Ekspresi belum lengkap, misalnya:
     *
     * 10+
     * √
     * (20
     */
    data object Incomplete : CalculationResult

    data class Failure(
        val message: String
    ) : CalculationResult
}
