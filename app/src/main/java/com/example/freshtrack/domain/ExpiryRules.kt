package com.example.freshtrack.domain

import com.example.freshtrack.model.Product
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class ExpiryStatus(val label: String) {
    EXPIRED("Expired"),
    TODAY("Today"),
    SOON("Soon"),
    OK("OK")
}

object ExpiryRules {
    const val SOON_DAYS = 3L

    fun daysUntil(expiry: LocalDate, today: LocalDate): Long =
        ChronoUnit.DAYS.between(today, expiry)

    fun statusFor(daysLeft: Long): ExpiryStatus = when {
        daysLeft < 0 -> ExpiryStatus.EXPIRED
        daysLeft == 0L -> ExpiryStatus.TODAY
        daysLeft <= SOON_DAYS -> ExpiryStatus.SOON
        else -> ExpiryStatus.OK
    }

    fun describe(daysLeft: Long): String = when {
        daysLeft < -1 -> "Expired ${-daysLeft} days ago"
        daysLeft == -1L -> "Expired yesterday"
        daysLeft == 0L -> "Expires today"
        daysLeft == 1L -> "Expires tomorrow"
        else -> "Expires in $daysLeft days"
    }
}

data class TrackedProduct(
    val product: Product,
    val daysLeft: Long,
    val status: ExpiryStatus
) {
    companion object {
        fun of(product: Product, today: LocalDate): TrackedProduct {
            val days = ExpiryRules.daysUntil(product.expiryDate, today)
            return TrackedProduct(product, days, ExpiryRules.statusFor(days))
        }
    }
}
