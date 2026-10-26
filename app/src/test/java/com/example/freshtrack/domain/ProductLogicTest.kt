package com.example.freshtrack.domain

import com.example.freshtrack.model.Category
import com.example.freshtrack.model.Product
import com.example.freshtrack.model.ProductStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ProductLogicTest {

    private val today = LocalDate.of(2026, 10, 25)
    private val validDay = today.plusDays(5).toEpochDay()

    private fun product(
        name: String,
        daysFromToday: Long,
        category: Category = Category.OTHER,
        status: ProductStatus = ProductStatus.ACTIVE
    ) = Product(name, name, category, 1, today.plusDays(daysFromToday), status)

    @Test
    fun validator_acceptsCorrectInput() {
        assertTrue(ProductValidator.validate("Milk", "2", validDay, today).isValid)
    }

    @Test
    fun validator_rejectsBlankName() {
        val errors = ProductValidator.validate("   ", "2", validDay, today)
        assertFalse(errors.isValid)
        assertEquals("Enter a product name", errors.name)
    }

    @Test
    fun validator_rejectsBadQuantity() {
        assertFalse(ProductValidator.validate("Milk", "0", validDay, today).isValid)
        assertFalse(ProductValidator.validate("Milk", "-3", validDay, today).isValid)
        assertFalse(ProductValidator.validate("Milk", "", validDay, today).isValid)
        assertFalse(ProductValidator.validate("Milk", "1000", validDay, today).isValid)
        assertTrue(ProductValidator.validate("Milk", "999", validDay, today).isValid)
    }

    @Test
    fun validator_rejectsMissingOrAbsurdDate() {
        assertEquals("Choose an expiry date", ProductValidator.validate("Milk", "1", null, today).date)
        val tooFar = today.plusYears(11).toEpochDay()
        assertFalse(ProductValidator.validate("Milk", "1", tooFar, today).isValid)
        val tooOld = today.minusYears(2).toEpochDay()
        assertFalse(ProductValidator.validate("Milk", "1", tooOld, today).isValid)
        val yesterday = today.minusDays(1).toEpochDay()
        assertTrue(ProductValidator.validate("Milk", "1", yesterday, today).isValid)
    }

    @Test
    fun summarize_countsStatusesAndLimitsUseFirst() {
        val active = listOf(
            product("A", -2), product("B", 0), product("C", 1), product("D", 2),
            product("E", 20), product("F", 30), product("G", 40)
        ).map { TrackedProduct.of(it, today) }
        val summary = ProductInsights.summarize(active)
        assertEquals(7, summary.activeCount)
        assertEquals(1, summary.expiredCount)
        assertEquals(1, summary.todayCount)
        assertEquals(2, summary.soonCount)
        assertEquals(listOf("A", "B", "C", "D", "E"), summary.useFirst.map { it.product.name })
    }

    @Test
    fun stats_separateUsedAndDiscarded() {
        val products = listOf(
            product("a", 1, status = ProductStatus.USED),
            product("b", 1, status = ProductStatus.USED),
            product("c", 1, Category.DAIRY, ProductStatus.DISCARDED),
            product("d", 1, Category.PRODUCE, ProductStatus.DISCARDED),
            product("e", 1, Category.PRODUCE, ProductStatus.DISCARDED),
            product("f", 1)
        )
        val stats = ProductInsights.stats(products)
        assertEquals(2, stats.used)
        assertEquals(3, stats.discarded)
        assertEquals(60, stats.wasteRatePercent)
        assertEquals(Category.PRODUCE, stats.discardedByCategory.first().category)
        assertEquals(2, stats.discardedByCategory.first().count)
    }

    @Test
    fun stats_withoutHistoryHasNoWasteRate() {
        val stats = ProductInsights.stats(listOf(product("a", 1)))
        assertNull(stats.wasteRatePercent)
        assertEquals(0, stats.total)
    }
}
