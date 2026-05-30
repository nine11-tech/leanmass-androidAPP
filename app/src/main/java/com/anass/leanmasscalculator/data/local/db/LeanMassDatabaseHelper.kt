package com.anass.leanmasscalculator.data.local.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.anass.leanmasscalculator.util.SecureCrypto
import org.json.JSONObject

class LeanMassDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        createCurrentSchema(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            migrateV1ToV2(db)
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    companion object {
        private const val DATABASE_NAME = "lean_mass.db"
        private const val DATABASE_VERSION = 2
    }

    private fun createCurrentSchema(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                full_name_enc TEXT NOT NULL,
                email_hash TEXT UNIQUE NOT NULL,
                email_enc TEXT NOT NULL,
                password_hash_enc TEXT NOT NULL,
                password_salt_enc TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE calculations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                payload_enc TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX index_calculations_user_created ON calculations(user_id, created_at DESC)")
    }

    private fun migrateV1ToV2(db: SQLiteDatabase) {
        db.beginTransaction()
        try {
            db.execSQL("ALTER TABLE users RENAME TO users_legacy")
            db.execSQL("ALTER TABLE calculations RENAME TO calculations_legacy")
            db.execSQL("DROP INDEX IF EXISTS index_calculations_user_created")
            createCurrentSchema(db)
            migrateUsers(db)
            migrateCalculations(db)
            db.execSQL("DROP TABLE users_legacy")
            db.execSQL("DROP TABLE calculations_legacy")
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun migrateUsers(db: SQLiteDatabase) {
        db.query("users_legacy", null, null, null, null, null, null).use { cursor ->
            while (cursor.moveToNext()) {
                val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                val values = ContentValues().apply {
                    put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")))
                    put("full_name_enc", SecureCrypto.encrypt(cursor.getString(cursor.getColumnIndexOrThrow("full_name"))))
                    put("email_hash", SecureCrypto.emailLookupHash(email))
                    put("email_enc", SecureCrypto.encrypt(email))
                    put("password_hash_enc", SecureCrypto.encrypt(cursor.getString(cursor.getColumnIndexOrThrow("password_hash"))))
                    put("password_salt_enc", SecureCrypto.encrypt(cursor.getString(cursor.getColumnIndexOrThrow("password_salt"))))
                    put("created_at", cursor.getLong(cursor.getColumnIndexOrThrow("created_at")))
                }
                db.insertOrThrow("users", null, values)
            }
        }
    }

    private fun migrateCalculations(db: SQLiteDatabase) {
        db.query("calculations_legacy", null, null, null, null, null, null).use { cursor ->
            while (cursor.moveToNext()) {
                val createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"))
                val payload = JSONObject()
                    .put("weightKg", cursor.getDouble(cursor.getColumnIndexOrThrow("weight_kg")))
                    .put("heightCm", cursor.getDouble(cursor.getColumnIndexOrThrow("height_cm")))
                    .put("gender", cursor.getString(cursor.getColumnIndexOrThrow("gender")))
                    .put("lbmKg", cursor.getDouble(cursor.getColumnIndexOrThrow("lbm_kg")))
                    .put("isSatisfactory", cursor.getInt(cursor.getColumnIndexOrThrow("is_satisfactory")) == 1)
                    .put("message", cursor.getString(cursor.getColumnIndexOrThrow("message")))
                    .put("createdAt", createdAt)
                    .toString()
                val values = ContentValues().apply {
                    put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")))
                    put("user_id", cursor.getLong(cursor.getColumnIndexOrThrow("user_id")))
                    put("payload_enc", SecureCrypto.encrypt(payload))
                    put("created_at", createdAt)
                }
                db.insertOrThrow("calculations", null, values)
            }
        }
    }
}
