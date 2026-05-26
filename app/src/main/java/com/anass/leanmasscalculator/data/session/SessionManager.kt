package com.anass.leanmasscalculator.data.session

import android.content.Context
import com.anass.leanmasscalculator.data.local.entity.UserEntity

class SessionManager(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(userId: Long) {
        preferences.edit()
            .putLong(KEY_USER_ID, userId)
            .putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }

    fun saveSession(user: UserEntity) = saveSession(user.id)

    fun getUserId(): Long? {
        val id = preferences.getLong(KEY_USER_ID, NO_USER)
        return if (id == NO_USER) null else id
    }

    fun isSessionValid(): Boolean {
        val userId = getUserId() ?: return false
        val savedAt = preferences.getLong(KEY_LOGIN_TIMESTAMP, 0L)
        val valid = userId != NO_USER && SessionPolicy.isValid(savedAt, System.currentTimeMillis())
        if (!valid) clearSession()
        return valid
    }

    fun isLoggedIn(): Boolean = isSessionValid()

    fun clearSession() {
        preferences.edit().clear().apply()
    }

    fun clear() = clearSession()

    companion object {
        private const val PREFS_NAME = "lean_mass_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_LOGIN_TIMESTAMP = "login_timestamp"
        private const val NO_USER = -1L
    }
}
