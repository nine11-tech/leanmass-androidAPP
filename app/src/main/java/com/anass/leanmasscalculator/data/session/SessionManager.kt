package com.anass.leanmasscalculator.data.session

import android.content.Context

class SessionManager(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(userId: Long) {
        preferences.edit().putLong(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): Long? {
        val id = preferences.getLong(KEY_USER_ID, NO_USER)
        return if (id == NO_USER) null else id
    }

    fun isLoggedIn(): Boolean = getUserId() != null

    fun clear() {
        preferences.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "lean_mass_session"
        private const val KEY_USER_ID = "user_id"
        private const val NO_USER = -1L
    }
}
