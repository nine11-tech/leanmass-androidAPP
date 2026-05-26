package com.anass.leanmasscalculator.data.repository

import com.anass.leanmasscalculator.core.calculator.LeanMassCalculator
import com.anass.leanmasscalculator.core.model.Gender
import com.anass.leanmasscalculator.data.local.dao.CalculationDao
import com.anass.leanmasscalculator.data.local.dao.CalculationStats
import com.anass.leanmasscalculator.data.local.entity.CalculationEntity

class CalculationRepository(private val calculationDao: CalculationDao) {
    fun calculateAndSave(userId: Long, weightKg: Double, heightCm: Double, gender: Gender): CalculationEntity {
        val result = LeanMassCalculator.calculate(weightKg, heightCm, gender)
        val calculation = CalculationEntity(
            id = 0L,
            userId = userId,
            weightKg = weightKg,
            heightCm = heightCm,
            gender = gender,
            lbmKg = result.leanBodyMassKg,
            isSatisfactory = result.isSatisfactory,
            message = result.message,
            createdAt = System.currentTimeMillis()
        )
        val id = calculationDao.insert(calculation)
        return calculation.copy(id = id)
    }

    fun history(userId: Long): List<CalculationEntity> = calculationDao.findAllForUser(userId)

    fun delete(id: Long, userId: Long) = calculationDao.delete(id, userId)

    fun clear(userId: Long) = calculationDao.clearForUser(userId)

    fun stats(userId: Long): CalculationStats = calculationDao.statsForUser(userId)
}
