package com.anass.leanmasscalculator.util

import android.content.Context
import com.anass.leanmasscalculator.data.local.dao.CalculationDao
import com.anass.leanmasscalculator.data.local.dao.UserDao
import com.anass.leanmasscalculator.data.local.db.LeanMassDatabaseHelper
import com.anass.leanmasscalculator.data.repository.AuthRepository
import com.anass.leanmasscalculator.data.repository.CalculationRepository
import com.anass.leanmasscalculator.data.session.SessionManager

object AppDependencies {
    fun sessionManager(context: Context) = SessionManager(context)

    fun authRepository(context: Context): AuthRepository {
        val db = LeanMassDatabaseHelper(context)
        return AuthRepository(UserDao(db), sessionManager(context))
    }

    fun calculationRepository(context: Context): CalculationRepository {
        val db = LeanMassDatabaseHelper(context)
        return CalculationRepository(CalculationDao(db))
    }
}
