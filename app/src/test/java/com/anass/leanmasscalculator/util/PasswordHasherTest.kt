package com.anass.leanmasscalculator.util

import org.junit.Assert.assertFalse
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
}
