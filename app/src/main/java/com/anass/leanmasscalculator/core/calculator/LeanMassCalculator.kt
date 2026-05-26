package com.anass.leanmasscalculator.core.calculator

import com.anass.leanmasscalculator.core.config.LeanMassConfig
import com.anass.leanmasscalculator.core.model.Gender
import com.anass.leanmasscalculator.core.model.LeanMassResult
import kotlin.math.round

object LeanMassCalculator {

    fun calculate(
        weightKg: Double,
        heightCm: Double,
        gender: Gender
    ): LeanMassResult {
        require(weightKg > 0) { "Weight must be greater than 0." }
        require(heightCm > 0) { "Height must be greater than 0." }

        val rawLbm = when (gender) {
            Gender.MALE -> (0.407 * weightKg) + (0.267 * heightCm) - 19.2
            Gender.FEMALE -> (0.252 * weightKg) + (0.473 * heightCm) - 48.3
        }

        val roundedLbm = round(rawLbm * 10) / 10

        val threshold = when (gender) {
            Gender.MALE -> LeanMassConfig.MALE_MIN_LBM_KG
            Gender.FEMALE -> LeanMassConfig.FEMALE_MIN_LBM_KG
        }

        val isSatisfactory = roundedLbm >= threshold

        return LeanMassResult(
            leanBodyMassKg = roundedLbm,
            isSatisfactory = isSatisfactory,
            message = if (isSatisfactory) {
                "Result satisfactory"
            } else {
                "Result to monitor"
            }
        )
    }
}
