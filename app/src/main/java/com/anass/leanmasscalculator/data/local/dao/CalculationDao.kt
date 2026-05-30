package com.anass.leanmasscalculator.data.local.dao

import android.content.ContentValues
import com.anass.leanmasscalculator.core.model.Gender
import com.anass.leanmasscalculator.data.local.db.LeanMassDatabaseHelper
import com.anass.leanmasscalculator.data.local.entity.CalculationEntity
import com.anass.leanmasscalculator.util.SecureCrypto
import org.json.JSONObject

class CalculationDao(private val dbHelper: LeanMassDatabaseHelper) {
    fun insert(calculation: CalculationEntity): Long {
        val values = ContentValues().apply {
            put("user_id", calculation.userId)
            put("payload_enc", SecureCrypto.encrypt(calculation.toPayload()))
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
            "SELECT COUNT(*) AS total FROM calculations WHERE user_id = ?",
            arrayOf(userId.toString())
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(cursor.getColumnIndexOrThrow("total")) else 0
        }
        val average = averageLbmForUser(userId)
        val last = latestForUser(userId)
        return CalculationStats(total = total, averageLbm = average, last = last)
    }

    private fun android.database.Cursor.toCalculationEntity(): CalculationEntity {
        val payload = JSONObject(SecureCrypto.decrypt(getString(getColumnIndexOrThrow("payload_enc"))))
        return CalculationEntity(
            id = getLong(getColumnIndexOrThrow("id")),
            userId = getLong(getColumnIndexOrThrow("user_id")),
            weightKg = payload.getDouble("weightKg"),
            heightCm = payload.getDouble("heightCm"),
            gender = Gender.valueOf(payload.getString("gender")),
            lbmKg = payload.getDouble("lbmKg"),
            isSatisfactory = payload.getBoolean("isSatisfactory"),
            message = payload.getString("message"),
            createdAt = getLong(getColumnIndexOrThrow("created_at"))
        )
    }

    private fun latestForUser(userId: Long): CalculationEntity? {
        return dbHelper.readableDatabase.query(
            "calculations",
            null,
            "user_id = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "created_at DESC",
            "1"
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.toCalculationEntity() else null
        }
    }

    private fun averageLbmForUser(userId: Long): Double? {
        var total = 0.0
        var count = 0
        dbHelper.readableDatabase.query(
            "calculations",
            arrayOf("payload_enc"),
            "user_id = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val payload = JSONObject(SecureCrypto.decrypt(cursor.getString(cursor.getColumnIndexOrThrow("payload_enc"))))
                total += payload.getDouble("lbmKg")
                count++
            }
        }
        return if (count == 0) null else total / count
    }

    private fun CalculationEntity.toPayload(): String {
        return JSONObject()
            .put("weightKg", weightKg)
            .put("heightCm", heightCm)
            .put("gender", gender.name)
            .put("lbmKg", lbmKg)
            .put("isSatisfactory", isSatisfactory)
            .put("message", message)
            .put("createdAt", createdAt)
            .toString()
    }
}

data class CalculationStats(
    val total: Int,
    val averageLbm: Double?,
    val last: CalculationEntity?
)
