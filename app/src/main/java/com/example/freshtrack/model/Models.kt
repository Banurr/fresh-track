package com.example.freshtrack.model

import java.time.LocalDate

enum class Category(val label: String) {
    DAIRY("Dairy"),
    MEAT_FISH("Meat & fish"),
    PRODUCE("Fruit & veg"),
    BAKERY("Bakery"),
    DRINKS("Drinks"),
    FROZEN("Frozen"),
    PANTRY("Pantry"),
    OTHER("Other")
}

enum class ProductStatus {
    ACTIVE,
    USED,
    DISCARDED
}

data class Product(
    val id: String,
    val name: String,
    val category: Category,
    val quantity: Int,
    val expiryDate: LocalDate,
    val status: ProductStatus = ProductStatus.ACTIVE
)
