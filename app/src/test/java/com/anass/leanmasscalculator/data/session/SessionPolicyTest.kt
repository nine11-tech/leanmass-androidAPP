package com.anass.leanmasscalculator.data.session

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionPolicyTest {
    @Test
    fun sessionInsideTimeoutIsValid() {
        assertTrue(SessionPolicy.isValid(savedAtMillis = 1_000L, nowMillis = 2_000L, timeoutMillis = 2_000L))
    }

    @Test
    fun sessionAfterTimeoutIsInvalid() {
        assertFalse(SessionPolicy.isValid(savedAtMillis = 1_000L, nowMillis = 4_001L, timeoutMillis = 3_000L))
    }

    @Test
    fun missingTimestampIsInvalid() {
        assertFalse(SessionPolicy.isValid(savedAtMillis = 0L, nowMillis = 1_000L, timeoutMillis = 3_000L))
    }

    @Test
    fun futureTimestampIsInvalid() {
        assertFalse(SessionPolicy.isValid(savedAtMillis = 2_000L, nowMillis = 1_000L, timeoutMillis = 3_000L))
    }
}
