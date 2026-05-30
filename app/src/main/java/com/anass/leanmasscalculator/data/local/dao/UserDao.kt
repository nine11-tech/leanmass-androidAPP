package com.anass.leanmasscalculator.data.local.dao

import android.content.ContentValues
import com.anass.leanmasscalculator.data.local.db.LeanMassDatabaseHelper
import com.anass.leanmasscalculator.data.local.entity.UserEntity
import com.anass.leanmasscalculator.util.SecureCrypto

class UserDao(private val dbHelper: LeanMassDatabaseHelper) {
    fun insert(fullName: String, email: String, passwordHash: String, passwordSalt: String): Long {
        val cleanEmail = email.lowercase()
        val values = ContentValues().apply {
            put("full_name_enc", SecureCrypto.encrypt(fullName))
            put("email_hash", SecureCrypto.emailLookupHash(cleanEmail))
            put("email_enc", SecureCrypto.encrypt(cleanEmail))
            put("password_hash_enc", SecureCrypto.encrypt(passwordHash))
            put("password_salt_enc", SecureCrypto.encrypt(passwordSalt))
            put("created_at", System.currentTimeMillis())
        }
        return dbHelper.writableDatabase.insertOrThrow("users", null, values)
    }

    fun findByEmail(email: String): UserEntity? {
        return dbHelper.readableDatabase.query(
            "users",
            null,
            "email_hash = ?",
            arrayOf(SecureCrypto.emailLookupHash(email)),
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
            fullName = SecureCrypto.decrypt(getString(getColumnIndexOrThrow("full_name_enc"))),
            email = SecureCrypto.decrypt(getString(getColumnIndexOrThrow("email_enc"))),
            passwordHash = SecureCrypto.decrypt(getString(getColumnIndexOrThrow("password_hash_enc"))),
            passwordSalt = SecureCrypto.decrypt(getString(getColumnIndexOrThrow("password_salt_enc"))),
            createdAt = getLong(getColumnIndexOrThrow("created_at"))
        )
    }
}
