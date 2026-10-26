package com.example.freshtrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.freshtrack.domain.ExpiryRules
import com.example.freshtrack.domain.HomeSummary
import com.example.freshtrack.ui.LastAction
import com.example.freshtrack.ui.components.AppIcons
import com.example.freshtrack.ui.components.AppScaffold
import com.example.freshtrack.ui.components.CenteredMessage
import com.example.freshtrack.ui.components.MainBottomBar
import com.example.freshtrack.ui.components.MainTab
import com.example.freshtrack.ui.components.ProductCard
import com.example.freshtrack.ui.components.SectionHeader
import com.example.freshtrack.ui.components.StatCard
import com.example.freshtrack.ui.components.UndoSnackbarEffect

@Composable
fun HomeScreen(
    summary: HomeSummary,
    lastAction: LastAction?,
    onTabSelected: (MainTab) -> Unit,
    onAddProduct: () -> Unit,
    onEditProduct: (String) -> Unit,
    onMarkUsed: (String) -> Unit,
    onMarkDiscarded: (String) -> Unit,
    onUndo: () -> Unit,
    onUndoDismissed: () -> Unit,
    onLoadSamples: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    UndoSnackbarEffect(lastAction, snackbarHostState, onUndo, onUndoDismissed)

    AppScaffold(
        title = "FreshTrack",
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { MainBottomBar(selected = MainTab.HOME, onSelect = onTabSelected) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProduct) {
                Icon(AppIcons.Add, contentDescription = "Add product")
            }
        }
    ) { padding ->
        if (summary.activeCount == 0) {
            CenteredMessage(
                title = "Nothing tracked yet",
                message = "Add products with their expiry dates and FreshTrack will show what to use first.",
                modifier = Modifier.padding(padding),
                actionLabel = "Add product",
                onAction = onAddProduct,
                secondaryLabel = "Load sample products",
                onSecondary = onLoadSamples
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            label = "In stock",
                            value = summary.activeCount.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Expired",
                            value = summary.expiredCount.toString(),
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            label = "Expire today",
                            value = summary.todayCount.toString(),
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        StatCard(
                            label = "Soon (within ${ExpiryRules.SOON_DAYS} days)",
                            value = summary.soonCount.toString(),
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                item {
                    SectionHeader(text = "Use first") {
                        TextButton(onClick = { onTabSelected(MainTab.PRODUCTS) }) {
                            Text("See all")
                        }
                    }
                }
                items(summary.useFirst, key = { it.product.id }) { item ->
                    ProductCard(
                        item = item,
                        onEdit = { onEditProduct(item.product.id) },
                        onUsed = { onMarkUsed(item.product.id) },
                        onDiscard = { onMarkDiscarded(item.product.id) }
                    )
                }
                item {
                    Text(
                        text = "FreshTrack only shows the dates you enter. It cannot tell whether food is safe to eat.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
