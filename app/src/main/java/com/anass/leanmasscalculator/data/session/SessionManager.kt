package com.anass.leanmasscalculator.data.session

import android.content.Context
import com.anass.leanmasscalculator.data.local.entity.UserEntity
import com.anass.leanmasscalculator.util.SecureCrypto

class SessionManager(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(userId: Long) {
        preferences.edit()
            .putString(KEY_USER_ID, SecureCrypto.encrypt(userId.toString()))
            .putString(KEY_LOGIN_TIMESTAMP, SecureCrypto.encrypt(System.currentTimeMillis().toString()))
            .apply()
    }

    fun saveSession(user: UserEntity) = saveSession(user.id)

    fun getUserId(): Long? {
        val id = getSecureLong(KEY_USER_ID) ?: NO_USER
        return if (id == NO_USER) null else id
    }

    fun isSessionValid(): Boolean {
        val userId = getUserId() ?: return false
        val savedAt = getSecureLong(KEY_LOGIN_TIMESTAMP) ?: 0L
        val valid = userId != NO_USER && SessionPolicy.isValid(savedAt, System.currentTimeMillis())
        if (!valid) clearSession()
        return valid
    }

    fun isLoggedIn(): Boolean = isSessionValid()

    fun clearSession() {
        preferences.edit().clear().apply()
    }

    fun clear() = clearSession()

    private fun getSecureLong(key: String): Long? {
        return when (val stored = preferences.all[key]) {
            is Long -> {
                preferences.edit()
                    .putString(key, SecureCrypto.encrypt(stored.toString()))
                    .apply()
                stored
            }
            is String -> runCatching { SecureCrypto.decrypt(stored).toLong() }.getOrNull()
            else -> null
        }
    }

    companion object {
        private const val PREFS_NAME = "lean_mass_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_LOGIN_TIMESTAMP = "login_timestamp"
        private const val NO_USER = -1L
    }
}
