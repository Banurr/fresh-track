package com.example.freshtrack.domain

import java.time.LocalDate

data class FormErrors(
    val name: String? = null,
    val quantity: String? = null,
    val date: String? = null
) {
    val isValid: Boolean get() = name == null && quantity == null && date == null
}

object ProductValidator {
    const val MAX_NAME_LENGTH = 40
    const val MAX_QUANTITY = 999

    fun validate(
        name: String,
        quantityText: String,
        expiryEpochDay: Long?,
        today: LocalDate
    ): FormErrors {
        val nameError = when {
            name.isBlank() -> "Enter a product name"
            name.trim().length > MAX_NAME_LENGTH -> "Name is too long (max $MAX_NAME_LENGTH)"
            else -> null
        }

        val quantity = quantityText.toIntOrNull()
        val quantityError =
            if (quantity == null || quantity !in 1..MAX_QUANTITY) {
                "Enter a quantity from 1 to $MAX_QUANTITY"
            } else {
                null
            }

        val earliest = today.minusYears(1).toEpochDay()
        val latest = today.plusYears(10).toEpochDay()
        val dateError = when {
            expiryEpochDay == null -> "Choose an expiry date"
            expiryEpochDay !in earliest..latest -> "Date must be within a year back and 10 years ahead"
            else -> null
        }

        return FormErrors(nameError, quantityError, dateError)
    }
}
