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

        AuthValidator.validateFullName(cleanName)?.let { return AuthResult.Error(it) }
        AuthValidator.validateEmail(cleanEmail)?.let { return AuthResult.Error(it) }
        AuthValidator.validatePasswordStrength(password)?.let { return AuthResult.Error(it) }
        AuthValidator.validatePasswordConfirmation(password, confirmPassword)?.let { return AuthResult.Error(it) }
        if (userDao.findByEmail(cleanEmail) != null) return AuthResult.Error("An account already exists for this email.")

        val salt = PasswordHasher.createSalt()
        val hash = PasswordHasher.hash(password, salt)
        val id = userDao.insert(cleanName, cleanEmail, hash, salt)
        val user = userDao.findById(id)!!
        sessionManager.saveSession(user)
        return AuthResult.Success(user)
    }

    fun login(email: String, password: String): AuthResult {
        val cleanEmail = email.trim().lowercase()
        AuthValidator.validateEmail(cleanEmail)?.let { return AuthResult.Error(it) }
        if (password.isBlank()) return AuthResult.Error("Password is required.")

        val user = userDao.findByEmail(cleanEmail) ?: return AuthResult.Error("Invalid email or password.")
        if (!PasswordHasher.verify(password, user.passwordSalt, user.passwordHash)) {
            return AuthResult.Error("Invalid email or password.")
        }
        sessionManager.saveSession(user)
        return AuthResult.Success(user)
    }

    fun currentUser(): UserEntity? {
        if (!sessionManager.isSessionValid()) return null
        return sessionManager.getUserId()?.let(userDao::findById)
    }
}

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}
