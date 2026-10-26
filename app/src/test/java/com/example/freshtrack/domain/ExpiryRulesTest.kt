package com.example.freshtrack.domain

import org.junit.Assert.assertEquals
import java.time.LocalDate
import org.junit.Test

class ExpiryRulesTest {

    private val today = LocalDate.of(2026, 10, 25)

    @Test
    fun statusFor_coversEveryBoundary() {
        assertEquals(ExpiryStatus.EXPIRED, ExpiryRules.statusFor(-1))
        assertEquals(ExpiryStatus.EXPIRED, ExpiryRules.statusFor(-30))
        assertEquals(ExpiryStatus.TODAY, ExpiryRules.statusFor(0))
        assertEquals(ExpiryStatus.SOON, ExpiryRules.statusFor(1))
        assertEquals(ExpiryStatus.SOON, ExpiryRules.statusFor(ExpiryRules.SOON_DAYS))
        assertEquals(ExpiryStatus.OK, ExpiryRules.statusFor(ExpiryRules.SOON_DAYS + 1))
    }

    @Test
    fun daysUntil_comparesCalendarDates() {
        assertEquals(0L, ExpiryRules.daysUntil(today, today))
        assertEquals(1L, ExpiryRules.daysUntil(today.plusDays(1), today))
        assertEquals(-2L, ExpiryRules.daysUntil(today.minusDays(2), today))
    }

    @Test
    fun daysUntil_worksAcrossMonthAndYearBoundaries() {
        val endOfYear = LocalDate.of(2026, 12, 31)
        assertEquals(2L, ExpiryRules.daysUntil(LocalDate.of(2027, 1, 2), endOfYear))
        assertEquals(3L, ExpiryRules.daysUntil(LocalDate.of(2026, 11, 2), LocalDate.of(2026, 10, 30)))
    }

    @Test
    fun describe_givesReadableText() {
        assertEquals("Expired 3 days ago", ExpiryRules.describe(-3))
        assertEquals("Expired yesterday", ExpiryRules.describe(-1))
        assertEquals("Expires today", ExpiryRules.describe(0))
        assertEquals("Expires tomorrow", ExpiryRules.describe(1))
        assertEquals("Expires in 12 days", ExpiryRules.describe(12))
    }
}
