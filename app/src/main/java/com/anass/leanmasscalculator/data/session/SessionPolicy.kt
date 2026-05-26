package com.anass.leanmasscalculator.data.session

import com.anass.leanmasscalculator.core.config.SecurityConfig

object SessionPolicy {
    fun isValid(savedAtMillis: Long, nowMillis: Long, timeoutMillis: Long = SecurityConfig.SESSION_TIMEOUT_MILLIS): Boolean {
        if (savedAtMillis <= 0L) return false
        if (nowMillis < savedAtMillis) return false
        return nowMillis - savedAtMillis <= timeoutMillis
    }
}
