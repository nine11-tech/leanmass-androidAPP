package com.anass.leanmasscalculator.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {
    @Test
    fun verifyAcceptsCorrectPassword() {
        val salt = PasswordHasher.createSalt()
        val hash = PasswordHasher.hash("strong-password", salt)

        assertTrue(PasswordHasher.verify("strong-password", salt, hash))
    }

    @Test
    fun verifyRejectsWrongPassword() {
        val salt = PasswordHasher.createSalt()
        val hash = PasswordHasher.hash("strong-password", salt)

        assertFalse(PasswordHasher.verify("wrong-password", salt, hash))
    }

    @Test
    fun samePasswordWithDifferentSaltsProducesDifferentHashes() {
        val firstSalt = PasswordHasher.createSalt()
        val secondSalt = PasswordHasher.createSalt()

        val firstHash = PasswordHasher.hash("Strong-password1!", firstSalt)
        val secondHash = PasswordHasher.hash("Strong-password1!", secondSalt)

        assertNotEquals(firstSalt, secondSalt)
        assertNotEquals(firstHash, secondHash)
    }

    @Test
    fun generatedSaltIsNotEmpty() {
        val salt = PasswordHasher.createSalt()

        assertNotNull(salt)
        assertTrue(salt.isNotBlank())
    }
}
