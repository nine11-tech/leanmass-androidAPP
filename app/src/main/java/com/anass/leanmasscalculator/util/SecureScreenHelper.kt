package com.anass.leanmasscalculator.util

import android.view.Window
import android.view.WindowManager
import com.anass.leanmasscalculator.core.config.SecurityConfig

object SecureScreenHelper {
    fun enableSecureMode(window: Window) {
        if (SecurityConfig.ENABLE_SECURE_SCREEN) {
            // MASVS-PLATFORM / MASVS-PRIVACY: prevent screenshots and screen recording
            // on screens that display credentials, personal data, or health history.
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
    }
}
