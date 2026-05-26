package com.anass.leanmasscalculator.util

import java.security.SecureRandom
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 150_000
    private const val KEY_LENGTH_BITS = 256
    private const val SALT_LENGTH_BYTES = 16

    fun createSalt(): String {
        val salt = ByteArray(SALT_LENGTH_BYTES)
        SecureRandom().nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun hash(password: String, saltBase64: String): String {
        val salt = Base64.getDecoder().decode(saltBase64)
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS)
        val bytes = SecretKeyFactory.getInstance(ALGORITHM)
            .generateSecret(spec)
            .encoded
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun verify(password: String, saltBase64: String, expectedHash: String): Boolean {
        val actual = Base64.getDecoder().decode(hash(password, saltBase64))
        val expected = Base64.getDecoder().decode(expectedHash)
        return MessageDigest.isEqual(actual, expected)
    }
}
