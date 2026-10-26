package com.example.freshtrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.freshtrack.domain.SortOption
import com.example.freshtrack.domain.StatusFilter
import com.example.freshtrack.ui.LastAction
import com.example.freshtrack.ui.ProductListUiState
import com.example.freshtrack.ui.components.AppIcons
import com.example.freshtrack.ui.components.AppScaffold
import com.example.freshtrack.ui.components.CenteredMessage
import com.example.freshtrack.ui.components.ChipGroup
import com.example.freshtrack.ui.components.MainBottomBar
import com.example.freshtrack.ui.components.MainTab
import com.example.freshtrack.ui.components.ProductCard
import com.example.freshtrack.ui.components.UndoSnackbarEffect

@Composable
fun ProductsScreen(
    state: ProductListUiState,
    query: String,
    lastAction: LastAction?,
    onQueryChange: (String) -> Unit,
    onFilterChange: (StatusFilter) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onClearFilters: () -> Unit,
    onTabSelected: (MainTab) -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (String) -> Unit,
    onMarkUsed: (String) -> Unit,
    onMarkDiscarded: (String) -> Unit,
    onUndo: () -> Unit,
    onUndoDismissed: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    UndoSnackbarEffect(lastAction, snackbarHostState, onUndo, onUndoDismissed)

    AppScaffold(
        title = "Products",
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { MainBottomBar(selected = MainTab.PRODUCTS, onSelect = onTabSelected) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProduct) {
                Icon(AppIcons.Add, contentDescription = "Add product")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                label = { Text("Search products") },
                singleLine = true,
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(AppIcons.Close, contentDescription = "Clear search")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, end = 16.dp)
            )
            ChipGroup(
                options = StatusFilter.entries,
                isSelected = { it == state.filter },
                label = { it.label },
                onClick = onFilterChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Sort by",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SortOption.entries.forEach { option ->
                    FilterChip(
                        selected = option == state.sort,
                        onClick = { onSortChange(option) },
                        label = { Text(option.label) }
                    )
                }
            }

            when {
                state.totalActive == 0 -> CenteredMessage(
                    title = "No products yet",
                    message = "Add what you have at home to see what to use first.",
                    modifier = Modifier.weight(1f),
                    actionLabel = "Add product",
                    onAction = onAddProduct
                )

                state.items.isEmpty() -> CenteredMessage(
                    title = "Nothing found",
                    message = "No products match your search or filter.",
                    modifier = Modifier.weight(1f),
                    actionLabel = "Clear filters",
                    onAction = onClearFilters
                )

                else -> LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Showing ${state.items.size} of ${state.totalActive}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(state.items, key = { it.product.id }) { item ->
                        ProductCard(
                            item = item,
                            onEdit = { onEditProduct(item.product.id) },
                            onUsed = { onMarkUsed(item.product.id) },
                            onDiscard = { onMarkDiscarded(item.product.id) }
                        )
                    }
                }
            }
        }
    }
}
