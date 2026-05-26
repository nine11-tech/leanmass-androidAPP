package com.anass.leanmasscalculator.core.validation

object AuthValidator {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun isValidEmail(email: String): Boolean = emailRegex.matches(email.trim())

    fun validateEmail(email: String): String? {
        return when {
            email.trim().isEmpty() -> "Email is required."
            !isValidEmail(email) -> "Enter a valid email address."
            else -> null
        }
    }

    fun validatePasswordStrength(password: String): String? {
        return when {
            password.length < 8 -> "Password must be at least 8 characters."
            password.none { it.isUpperCase() } -> "Password must contain at least one uppercase letter."
            password.none { it.isLowerCase() } -> "Password must contain at least one lowercase letter."
            password.none { it.isDigit() } -> "Password must contain at least one digit."
            password.none { !it.isLetterOrDigit() } -> "Password must contain at least one special character."
            else -> null
        }
    }

    fun validatePassword(password: String): String? = validatePasswordStrength(password)

    fun validateFullName(fullName: String): String? {
        val cleanName = fullName.trim()
        return when {
            cleanName.isEmpty() -> "Full name is required."
            cleanName.length < 2 -> "Full name must contain at least 2 characters."
            cleanName.any { it.isDigit() } -> "Full name should not contain digits."
            else -> null
        }
    }

    fun validatePasswordConfirmation(password: String, confirmPassword: String): String? {
        return if (password != confirmPassword) "Passwords do not match." else null
    }
}
