package com.anass.leanmasscalculator.data.local.dao

import android.content.ContentValues
import com.anass.leanmasscalculator.data.local.db.LeanMassDatabaseHelper
import com.anass.leanmasscalculator.data.local.entity.UserEntity

class UserDao(private val dbHelper: LeanMassDatabaseHelper) {
    fun insert(fullName: String, email: String, passwordHash: String, passwordSalt: String): Long {
        val values = ContentValues().apply {
            put("full_name", fullName)
            put("email", email.lowercase())
            put("password_hash", passwordHash)
            put("password_salt", passwordSalt)
            put("created_at", System.currentTimeMillis())
        }
        return dbHelper.writableDatabase.insertOrThrow("users", null, values)
    }

    fun findByEmail(email: String): UserEntity? {
        return dbHelper.readableDatabase.query(
            "users",
            null,
            "email = ?",
            arrayOf(email.lowercase()),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.toUserEntity() else null
        }
    }

    fun findById(id: Long): UserEntity? {
        return dbHelper.readableDatabase.query(
            "users",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.toUserEntity() else null
        }
    }

    private fun android.database.Cursor.toUserEntity(): UserEntity {
        return UserEntity(
            id = getLong(getColumnIndexOrThrow("id")),
            fullName = getString(getColumnIndexOrThrow("full_name")),
            email = getString(getColumnIndexOrThrow("email")),
            passwordHash = getString(getColumnIndexOrThrow("password_hash")),
            passwordSalt = getString(getColumnIndexOrThrow("password_salt")),
            createdAt = getLong(getColumnIndexOrThrow("created_at"))
        )
    }
}
