package com.anass.leanmasscalculator.core.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidatorTest {
    @Test
    fun strongPasswordIsAccepted() {
        assertNull(AuthValidator.validatePasswordStrength("StrongPass1!"))
    }

    @Test
    fun passwordWithoutUppercaseIsRejected() {
        assertEquals(
            "Password must contain at least one uppercase letter.",
            AuthValidator.validatePasswordStrength("strongpass1!")
        )
    }

    @Test
    fun passwordWithoutLowercaseIsRejected() {
        assertEquals(
            "Password must contain at least one lowercase letter.",
            AuthValidator.validatePasswordStrength("STRONGPASS1!")
        )
    }

    @Test
    fun passwordWithoutDigitIsRejected() {
        assertEquals(
            "Password must contain at least one digit.",
            AuthValidator.validatePasswordStrength("StrongPass!")
        )
    }

    @Test
    fun passwordWithoutSpecialCharacterIsRejected() {
        assertEquals(
            "Password must contain at least one special character.",
            AuthValidator.validatePasswordStrength("StrongPass1")
        )
    }

    @Test
    fun malformedEmailIsRejected() {
        assertEquals("Enter a valid email address.", AuthValidator.validateEmail("bad-email"))
    }

    @Test
    fun validEmailIsAccepted() {
        assertNull(AuthValidator.validateEmail("student@example.com"))
    }

    @Test
    fun mismatchedPasswordConfirmationIsRejected() {
        assertEquals(
            "Passwords do not match.",
            AuthValidator.validatePasswordConfirmation("StrongPass1!", "OtherPass1!")
        )
    }
}
