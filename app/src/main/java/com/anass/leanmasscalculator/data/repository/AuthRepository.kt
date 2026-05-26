package com.anass.leanmasscalculator.data.repository

import com.anass.leanmasscalculator.core.validation.AuthValidator
import com.anass.leanmasscalculator.data.local.dao.UserDao
import com.anass.leanmasscalculator.data.local.entity.UserEntity
import com.anass.leanmasscalculator.data.session.SessionManager
import com.anass.leanmasscalculator.util.PasswordHasher

class AuthRepository(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {
    fun register(fullName: String, email: String, password: String, confirmPassword: String): AuthResult {
        val cleanName = fullName.trim()
        val cleanEmail = email.trim().lowercase()

        AuthValidator.validateRequired(cleanName, "Full name")?.let { return AuthResult.Error(it) }
        if (!AuthValidator.isValidEmail(cleanEmail)) return AuthResult.Error("Enter a valid email address.")
        AuthValidator.validatePassword(password)?.let { return AuthResult.Error(it) }
        if (password != confirmPassword) return AuthResult.Error("Passwords do not match.")
        if (userDao.findByEmail(cleanEmail) != null) return AuthResult.Error("An account already exists for this email.")

        val salt = PasswordHasher.createSalt()
        val hash = PasswordHasher.hash(password, salt)
        val id = userDao.insert(cleanName, cleanEmail, hash, salt)
        sessionManager.saveSession(id)
        return AuthResult.Success(userDao.findById(id)!!)
    }

    fun login(email: String, password: String): AuthResult {
        val cleanEmail = email.trim().lowercase()
        if (!AuthValidator.isValidEmail(cleanEmail)) return AuthResult.Error("Enter a valid email address.")
        if (password.isBlank()) return AuthResult.Error("Password is required.")

        val user = userDao.findByEmail(cleanEmail) ?: return AuthResult.Error("Invalid email or password.")
        if (!PasswordHasher.verify(password, user.passwordSalt, user.passwordHash)) {
            return AuthResult.Error("Invalid email or password.")
        }
        sessionManager.saveSession(user.id)
        return AuthResult.Success(user)
    }

    fun currentUser(): UserEntity? = sessionManager.getUserId()?.let(userDao::findById)
}

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
