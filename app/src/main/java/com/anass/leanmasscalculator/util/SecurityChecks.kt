package com.anass.leanmasscalculator.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Debug
import java.io.File

object SecurityChecks {
    private val suPaths = listOf(
        "/system/bin/su",
        "/system/xbin/su",
        "/sbin/su",
        "/system/su",
        "/system/bin/.ext/su",
        "/system/usr/we-need-root/su",
        "/data/local/su",
        "/data/local/bin/su",
        "/data/local/xbin/su"
    )

    fun isDeviceProbablyRooted(): Boolean {
        val hasTestKeys = Build.TAGS?.contains("test-keys") == true
        val hasSuBinary = suPaths.any { File(it).exists() }
        return hasTestKeys || hasSuBinary
    }

    fun isDebuggerAttached(): Boolean = Debug.isDebuggerConnected() || Debug.waitingForDebugger()

    fun isAppDebuggable(context: Context): Boolean {
        return context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }

    fun getSecurityStatus(context: Context): SecurityStatus {
        val rooted = isDeviceProbablyRooted()
        val debugger = isDebuggerAttached()
        val debuggable = isAppDebuggable(context)
        return SecurityStatus(
            isNormal = !rooted && !debugger && !debuggable,
            rooted = rooted,
            debuggerAttached = debugger,
            appDebuggable = debuggable
        )
    }
}

data class SecurityStatus(
    val isNormal: Boolean,
    val rooted: Boolean,
    val debuggerAttached: Boolean,
    val appDebuggable: Boolean
)
