package com.anass.leanmasscalculator.core.calculator

import com.anass.leanmasscalculator.core.model.Gender
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class LeanMassCalculatorTest {

    @Test
    fun calculateMaleLeanMassReturnsCorrectValue() {
        val result = LeanMassCalculator.calculate(
            weightKg = 75.0,
            heightCm = 180.0,
            gender = Gender.MALE
        )

        assertEquals(59.4, result.leanBodyMassKg, 0.0)
        assertTrue(result.isSatisfactory)
        assertEquals("Result satisfactory", result.message)
    }

    @Test
    fun calculateFemaleLeanMassReturnsCorrectValue() {
        val result = LeanMassCalculator.calculate(
            weightKg = 60.0,
            heightCm = 165.0,
            gender = Gender.FEMALE
        )

        assertEquals(44.9, result.leanBodyMassKg, 0.0)
        assertTrue(result.isSatisfactory)
        assertEquals("Result satisfactory", result.message)
    }

    @Test
    fun maleResultBelowThresholdIsNotSatisfactory() {
        val result = LeanMassCalculator.calculate(
            weightKg = 40.0,
            heightCm = 130.0,
            gender = Gender.MALE
        )

        assertFalse(result.isSatisfactory)
        assertEquals("Result to monitor", result.message)
    }

    @Test
    fun femaleResultBelowThresholdIsNotSatisfactory() {
        val result = LeanMassCalculator.calculate(
            weightKg = 35.0,
            heightCm = 120.0,
            gender = Gender.FEMALE
        )

        assertFalse(result.isSatisfactory)
        assertEquals("Result to monitor", result.message)
    }

    @Test
    fun invalidWeightThrowsException() {
        assertThrows(IllegalArgumentException::class.java) {
            LeanMassCalculator.calculate(
                weightKg = 0.0,
                heightCm = 170.0,
                gender = Gender.MALE
            )
        }
    }

    @Test
    fun invalidHeightThrowsException() {
        assertThrows(IllegalArgumentException::class.java) {
            LeanMassCalculator.calculate(
                weightKg = 70.0,
                heightCm = 0.0,
                gender = Gender.MALE
            )
        }
    }
}
