package com.example.freshtrack.data

import com.example.freshtrack.model.Product
import com.example.freshtrack.model.ProductStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object ProductRepository {

    private val state = MutableStateFlow<List<Product>>(emptyList())

    val products: StateFlow<List<Product>> = state.asStateFlow()

    fun add(product: Product) = state.update { it + product }

    fun addAll(items: List<Product>) = state.update { it + items }

    fun update(product: Product) = state.update { list ->
        list.map { if (it.id == product.id) product else it }
    }

    fun delete(id: String) = state.update { list -> list.filterNot { it.id == id } }

    fun setStatus(id: String, status: ProductStatus) = state.update { list ->
        list.map { if (it.id == id) it.copy(status = status) else it }
    }
}
