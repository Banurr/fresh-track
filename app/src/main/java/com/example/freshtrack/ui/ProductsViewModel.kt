package com.example.freshtrack.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.freshtrack.data.ProductRepository
import com.example.freshtrack.data.SampleProducts
import com.example.freshtrack.domain.FormErrors
import com.example.freshtrack.domain.HomeSummary
import com.example.freshtrack.domain.ProductFilters
import com.example.freshtrack.domain.ProductInsights
import com.example.freshtrack.domain.ProductStats
import com.example.freshtrack.domain.ProductValidator
import com.example.freshtrack.domain.SortOption
import com.example.freshtrack.domain.StatusFilter
import com.example.freshtrack.domain.TrackedProduct
import com.example.freshtrack.domain.todayFlow
import com.example.freshtrack.model.Category
import com.example.freshtrack.model.Product
import com.example.freshtrack.model.ProductStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.util.UUID

data class ProductListUiState(
    val items: List<TrackedProduct> = emptyList(),
    val totalActive: Int = 0,
    val filter: StatusFilter = StatusFilter.ALL,
    val sort: SortOption = SortOption.EXPIRY
)

data class LastAction(
    val productId: String,
    val productName: String,
    val status: ProductStatus
)

class ProductsViewModel(private val repository: ProductRepository) : ViewModel() {

    val today: StateFlow<LocalDate> = todayFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS),
        initialValue = LocalDate.now()
    )

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val filter = MutableStateFlow(StatusFilter.ALL)
    private val sort = MutableStateFlow(SortOption.EXPIRY)

    private val _lastAction = MutableStateFlow<LastAction?>(null)
    val lastAction: StateFlow<LastAction?> = _lastAction.asStateFlow()

    private val activeProducts: Flow<List<TrackedProduct>> =
        combine(repository.products, today) { products, day ->
            products
                .filter { it.status == ProductStatus.ACTIVE }
                .map { TrackedProduct.of(it, day) }
        }

    val homeState: StateFlow<HomeSummary> = activeProducts
        .map { ProductInsights.summarize(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), HomeSummary())

    val listState: StateFlow<ProductListUiState> =
        combine(activeProducts, _query, filter, sort) { active, text, statusFilter, sortOption ->
            ProductListUiState(
                items = ProductFilters.apply(active, text, statusFilter, sortOption),
                totalActive = active.size,
                filter = statusFilter,
                sort = sortOption
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), ProductListUiState())

    val statsState: StateFlow<ProductStats> = repository.products
        .map { ProductInsights.stats(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MS), ProductStats())

    fun setQuery(text: String) {
        _query.value = text
    }

    fun setFilter(value: StatusFilter) {
        filter.value = value
    }

    fun setSort(value: SortOption) {
        sort.value = value
    }

    fun clearFilters() {
        _query.value = ""
        filter.value = StatusFilter.ALL
    }

    fun productById(id: String): Product? =
        repository.products.value.firstOrNull { it.id == id }

    fun validate(name: String, quantityText: String, expiryEpochDay: Long?): FormErrors =
        ProductValidator.validate(name, quantityText, expiryEpochDay, today.value)

    fun saveProduct(
        id: String?,
        name: String,
        category: Category,
        quantityText: String,
        expiryEpochDay: Long?
    ): Boolean {
        if (!validate(name, quantityText, expiryEpochDay).isValid) return false
        val quantity = quantityText.toInt()
        val expiry = LocalDate.ofEpochDay(checkNotNull(expiryEpochDay))
        val existing = id?.let { productById(it) }
        if (existing == null) {
            repository.add(
                Product(UUID.randomUUID().toString(), name.trim(), category, quantity, expiry)
            )
        } else {
            repository.update(
                existing.copy(name = name.trim(), category = category, quantity = quantity, expiryDate = expiry)
            )
        }
        return true
    }

    fun deleteProduct(id: String) {
        repository.delete(id)
    }

    fun markUsed(id: String) = finish(id, ProductStatus.USED)

    fun markDiscarded(id: String) = finish(id, ProductStatus.DISCARDED)

    fun undoLastAction() {
        val action = _lastAction.value ?: return
        repository.setStatus(action.productId, ProductStatus.ACTIVE)
        _lastAction.value = null
    }

    fun dismissLastAction() {
        _lastAction.value = null
    }

    fun loadSamples() {
        repository.addAll(SampleProducts.create(today.value))
    }

    private fun finish(id: String, status: ProductStatus) {
        val product = productById(id) ?: return
        repository.setStatus(id, status)
        _lastAction.value = LastAction(id, product.name, status)
    }

    companion object {
        private const val STOP_TIMEOUT_MS = 5_000L

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { ProductsViewModel(ProductRepository) }
        }
    }
}
