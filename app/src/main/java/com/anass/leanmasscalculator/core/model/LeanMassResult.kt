package com.anass.leanmasscalculator.core.model

data class LeanMassResult(
    val leanBodyMassKg: Double,
    val isSatisfactory: Boolean,
    val message: String
)
