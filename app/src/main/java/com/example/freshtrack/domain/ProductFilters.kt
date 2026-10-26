package com.example.freshtrack.domain

enum class StatusFilter(val label: String) {
    ALL("All"),
    EXPIRED("Expired"),
    TODAY("Today"),
    SOON("Soon"),
    OK("OK");

    fun matches(status: ExpiryStatus): Boolean = when (this) {
        ALL -> true
        EXPIRED -> status == ExpiryStatus.EXPIRED
        TODAY -> status == ExpiryStatus.TODAY
        SOON -> status == ExpiryStatus.SOON
        OK -> status == ExpiryStatus.OK
    }
}

enum class SortOption(val label: String) {
    EXPIRY("Expiry date"),
    NAME("Name")
}

object ProductFilters {

    fun apply(
        items: List<TrackedProduct>,
        query: String,
        filter: StatusFilter,
        sort: SortOption
    ): List<TrackedProduct> {
        val needle = query.trim()
        val filtered = items.filter { item ->
            filter.matches(item.status) &&
                (needle.isEmpty() || item.product.name.contains(needle, ignoreCase = true))
        }
        return when (sort) {
            SortOption.EXPIRY -> filtered.sortedWith(
                compareBy<TrackedProduct> { it.daysLeft }.thenBy { it.product.name.lowercase() }
            )
            SortOption.NAME -> filtered.sortedWith(
                compareBy<TrackedProduct> { it.product.name.lowercase() }.thenBy { it.daysLeft }
            )
        }
    }
}
