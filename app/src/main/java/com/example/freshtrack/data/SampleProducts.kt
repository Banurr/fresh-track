package com.example.freshtrack.data

import com.example.freshtrack.model.Category
import com.example.freshtrack.model.Product
import com.example.freshtrack.model.ProductStatus
import java.time.LocalDate
import java.util.UUID

object SampleProducts {

    fun create(today: LocalDate): List<Product> = listOf(
        sample("Milk", Category.DAIRY, 1, today.plusDays(1)),
        sample("Yogurt", Category.DAIRY, 4, today.plusDays(2)),
        sample("Chicken breast", Category.MEAT_FISH, 1, today),
        sample("Tomatoes", Category.PRODUCE, 6, today.minusDays(1)),
        sample("Bread", Category.BAKERY, 1, today.plusDays(3)),
        sample("Cheese", Category.DAIRY, 1, today.plusDays(9)),
        sample("Orange juice", Category.DRINKS, 2, today.plusDays(14)),
        sample("Frozen peas", Category.FROZEN, 1, today.plusDays(120)),
        sample("Rice", Category.PANTRY, 1, today.plusDays(300)),
        sample("Eggs", Category.DAIRY, 10, today.minusDays(4), ProductStatus.USED),
        sample("Apples", Category.PRODUCE, 5, today.minusDays(6), ProductStatus.USED),
        sample("Pasta", Category.PANTRY, 2, today.minusDays(9), ProductStatus.USED),
        sample("Spinach", Category.PRODUCE, 1, today.minusDays(3), ProductStatus.DISCARDED),
        sample("Cucumbers", Category.PRODUCE, 3, today.minusDays(5), ProductStatus.DISCARDED),
        sample("Cream", Category.DAIRY, 1, today.minusDays(2), ProductStatus.DISCARDED)
    )

    private fun sample(
        name: String,
        category: Category,
        quantity: Int,
        expiry: LocalDate,
        status: ProductStatus = ProductStatus.ACTIVE
    ) = Product(UUID.randomUUID().toString(), name, category, quantity, expiry, status)
}
