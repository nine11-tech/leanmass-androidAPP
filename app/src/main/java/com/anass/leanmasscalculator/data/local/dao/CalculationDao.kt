package com.anass.leanmasscalculator.data.local.dao

import android.content.ContentValues
import com.anass.leanmasscalculator.core.model.Gender
import com.anass.leanmasscalculator.data.local.db.LeanMassDatabaseHelper
import com.anass.leanmasscalculator.data.local.entity.CalculationEntity

class CalculationDao(private val dbHelper: LeanMassDatabaseHelper) {
    fun insert(calculation: CalculationEntity): Long {
        val values = ContentValues().apply {
            put("user_id", calculation.userId)
            put("weight_kg", calculation.weightKg)
            put("height_cm", calculation.heightCm)
            put("gender", calculation.gender.name)
            put("lbm_kg", calculation.lbmKg)
            put("is_satisfactory", if (calculation.isSatisfactory) 1 else 0)
            put("message", calculation.message)
            put("created_at", calculation.createdAt)
        }
        return dbHelper.writableDatabase.insertOrThrow("calculations", null, values)
    }

    fun findAllForUser(userId: Long): List<CalculationEntity> {
        // MASVS-STORAGE / MASVS-PRIVACY: history reads are scoped to the authenticated user.
        return dbHelper.readableDatabase.query(
            "calculations",
            null,
            "user_id = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "created_at DESC"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(cursor.toCalculationEntity())
            }
        }
    }

    fun delete(id: Long, userId: Long): Int {
        // MASVS-STORAGE: item deletion is scoped to the authenticated user.
        return dbHelper.writableDatabase.delete(
            "calculations",
            "id = ? AND user_id = ?",
            arrayOf(id.toString(), userId.toString())
        )
    }

    fun clearForUser(userId: Long): Int {
        // MASVS-STORAGE: bulk deletion only removes rows owned by the authenticated user.
        return dbHelper.writableDatabase.delete(
            "calculations",
            "user_id = ?",
            arrayOf(userId.toString())
        )
    }

    fun statsForUser(userId: Long): CalculationStats {
        val total = dbHelper.readableDatabase.rawQuery(
            "SELECT COUNT(*) AS total, AVG(lbm_kg) AS average_lbm FROM calculations WHERE user_id = ?",
            arrayOf(userId.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                val count = cursor.getInt(cursor.getColumnIndexOrThrow("total"))
                val average = if (cursor.isNull(cursor.getColumnIndexOrThrow("average_lbm"))) null else cursor.getDouble(cursor.getColumnIndexOrThrow("average_lbm"))
                count to average
            } else {
                0 to null
            }
        }
        val last = findAllForUser(userId).firstOrNull()
        return CalculationStats(total = total.first, averageLbm = total.second, last = last)
    }

    private fun android.database.Cursor.toCalculationEntity(): CalculationEntity {
        return CalculationEntity(
            id = getLong(getColumnIndexOrThrow("id")),
            userId = getLong(getColumnIndexOrThrow("user_id")),
            weightKg = getDouble(getColumnIndexOrThrow("weight_kg")),
            heightCm = getDouble(getColumnIndexOrThrow("height_cm")),
            gender = Gender.valueOf(getString(getColumnIndexOrThrow("gender"))),
            lbmKg = getDouble(getColumnIndexOrThrow("lbm_kg")),
            isSatisfactory = getInt(getColumnIndexOrThrow("is_satisfactory")) == 1,
            message = getString(getColumnIndexOrThrow("message")),
            createdAt = getLong(getColumnIndexOrThrow("created_at"))
        )
    }
}

data class CalculationStats(
    val total: Int,
    val averageLbm: Double?,
    val last: CalculationEntity?
)
