package com.anass.leanmasscalculator.core.validation

object CalculatorInputValidator {
    fun parsePositiveDouble(value: String): Double? {
        val number = value.trim().toDoubleOrNull()
        return number?.takeIf { it > 0.0 }
    }
}
