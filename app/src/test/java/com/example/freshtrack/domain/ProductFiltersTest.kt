package com.example.freshtrack.domain

import com.example.freshtrack.model.Category
import com.example.freshtrack.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ProductFiltersTest {

    private val today = LocalDate.of(2026, 10, 25)

    private fun item(name: String, daysFromToday: Long) = TrackedProduct.of(
        Product(name, name, Category.OTHER, 1, today.plusDays(daysFromToday)),
        today
    )

    private val items = listOf(
        item("Yogurt", 2),
        item("Bread", -1),
        item("Cheese", 10),
        item("Apples", 0),
        item("Milk", 3)
    )

    private fun names(result: List<TrackedProduct>) = result.map { it.product.name }

    @Test
    fun sortByExpiry_putsMostUrgentFirst() {
        val result = ProductFilters.apply(items, "", StatusFilter.ALL, SortOption.EXPIRY)
        assertEquals(listOf("Bread", "Apples", "Yogurt", "Milk", "Cheese"), names(result))
    }

    @Test
    fun sortByName_isAlphabetical() {
        val result = ProductFilters.apply(items, "", StatusFilter.ALL, SortOption.NAME)
        assertEquals(listOf("Apples", "Bread", "Cheese", "Milk", "Yogurt"), names(result))
    }

    @Test
    fun filterByStatus_keepsOnlyMatchingProducts() {
        val soon = ProductFilters.apply(items, "", StatusFilter.SOON, SortOption.EXPIRY)
        assertEquals(listOf("Yogurt", "Milk"), names(soon))
        val expired = ProductFilters.apply(items, "", StatusFilter.EXPIRED, SortOption.EXPIRY)
        assertEquals(listOf("Bread"), names(expired))
    }

    @Test
    fun search_ignoresCaseAndSpaces() {
        val result = ProductFilters.apply(items, "  mIl ", StatusFilter.ALL, SortOption.EXPIRY)
        assertEquals(listOf("Milk"), names(result))
    }

    @Test
    fun searchAndFilter_combine() {
        val result = ProductFilters.apply(items, "e", StatusFilter.OK, SortOption.EXPIRY)
        assertEquals(listOf("Cheese"), names(result))
    }

    @Test
    fun noMatches_returnsEmptyList() {
        assertTrue(ProductFilters.apply(items, "zzz", StatusFilter.ALL, SortOption.EXPIRY).isEmpty())
    }
}
