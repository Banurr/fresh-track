package com.example.freshtrack.domain

import com.example.freshtrack.model.Category
import com.example.freshtrack.model.Product
import com.example.freshtrack.model.ProductStatus
import kotlin.math.roundToInt

data class HomeSummary(
    val activeCount: Int = 0,
    val expiredCount: Int = 0,
    val todayCount: Int = 0,
    val soonCount: Int = 0,
    val useFirst: List<TrackedProduct> = emptyList()
)

data class CategoryCount(val category: Category, val count: Int)

data class ProductStats(
    val used: Int = 0,
    val discarded: Int = 0,
    val wasteRatePercent: Int? = null,
    val discardedByCategory: List<CategoryCount> = emptyList()
) {
    val total: Int get() = used + discarded
}

object ProductInsights {
    const val USE_FIRST_LIMIT = 5

    fun summarize(active: List<TrackedProduct>): HomeSummary = HomeSummary(
        activeCount = active.size,
        expiredCount = active.count { it.status == ExpiryStatus.EXPIRED },
        todayCount = active.count { it.status == ExpiryStatus.TODAY },
        soonCount = active.count { it.status == ExpiryStatus.SOON },
        useFirst = ProductFilters
            .apply(active, "", StatusFilter.ALL, SortOption.EXPIRY)
            .take(USE_FIRST_LIMIT)
    )

    fun stats(products: List<Product>): ProductStats {
        val used = products.count { it.status == ProductStatus.USED }
        val discardedProducts = products.filter { it.status == ProductStatus.DISCARDED }
        val discarded = discardedProducts.size
        val closed = used + discarded
        val wasteRate = if (closed == 0) null else (discarded * 100.0 / closed).roundToInt()
        val byCategory = discardedProducts
            .groupingBy { it.category }
            .eachCount()
            .map { (category, count) -> CategoryCount(category, count) }
            .sortedWith(
                compareByDescending<CategoryCount> { it.count }.thenBy { it.category.ordinal }
            )
        return ProductStats(used, discarded, wasteRate, byCategory)
    }
}
