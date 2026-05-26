package com.anass.leanmasscalculator.core.validation

object AuthValidator {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun isValidEmail(email: String): Boolean = emailRegex.matches(email.trim())

    fun validatePassword(password: String): String? {
        return when {
            password.length < 8 -> "Password must be at least 8 characters."
            else -> null
        }
    }

    fun validateRequired(value: String, label: String): String? {
        return if (value.trim().isEmpty()) "$label is required." else null
    }
}
