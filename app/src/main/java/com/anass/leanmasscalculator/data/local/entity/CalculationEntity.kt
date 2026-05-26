package com.anass.leanmasscalculator.data.local.entity

import com.anass.leanmasscalculator.core.model.Gender

data class CalculationEntity(
    val id: Long,
    val userId: Long,
    val weightKg: Double,
    val heightCm: Double,
    val gender: Gender,
    val lbmKg: Double,
    val isSatisfactory: Boolean,
    val message: String,
    val createdAt: Long
)
