package com.example.freshtrack.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.freshtrack.domain.ExpiryRules
import com.example.freshtrack.domain.ExpiryStatus
import com.example.freshtrack.domain.TrackedProduct
import com.example.freshtrack.model.ProductStatus
import com.example.freshtrack.ui.LastAction

enum class MainTab(val label: String, val icon: ImageVector) {
    HOME("Home", AppIcons.Home),
    PRODUCTS("Products", AppIcons.Products),
    STATS("Statistics", AppIcons.Stats)
}

@Composable
fun MainBottomBar(selected: MainTab, onSelect: (MainTab) -> Unit) {
    NavigationBar {
        MainTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = tab == selected,
                onClick = { onSelect(tab) },
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(tab.label) }
            )
        }
    }
}

@Composable
fun statusColors(status: ExpiryStatus): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    return when (status) {
        ExpiryStatus.EXPIRED -> scheme.errorContainer to scheme.onErrorContainer
        ExpiryStatus.TODAY -> scheme.tertiaryContainer to scheme.onTertiaryContainer
        ExpiryStatus.SOON -> scheme.secondaryContainer to scheme.onSecondaryContainer
        ExpiryStatus.OK -> scheme.primaryContainer to scheme.onPrimaryContainer
    }
}

@Composable
fun StatusBadge(status: ExpiryStatus, modifier: Modifier = Modifier) {
    val (container, content) = statusColors(status)
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = container,
        contentColor = content
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ProductCard(
    item: TrackedProduct,
    onEdit: () -> Unit,
    onUsed: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val product = item.product
    InfoCard(modifier = modifier, onClick = onEdit) {
        Row(verticalAlignment = Alignment.Top) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${product.category.label} · Qty ${product.quantity}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${ExpiryRules.describe(item.daysLeft)} · ${formatDate(product.expiryDate)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            StatusBadge(status = item.status)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDiscard) {
                Text("Discard", color = MaterialTheme.colorScheme.error)
            }
            TextButton(onClick = onUsed) { Text("Used") }
        }
    }
}

@Composable
fun UndoSnackbarEffect(
    lastAction: LastAction?,
    hostState: SnackbarHostState,
    onUndo: () -> Unit,
    onDismissed: () -> Unit
) {
    LaunchedEffect(lastAction) {
        if (lastAction != null) {
            val outcome = if (lastAction.status == ProductStatus.USED) "marked as used" else "discarded"
            val result = hostState.showSnackbar(
                message = "${lastAction.productName} $outcome",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) onUndo() else onDismissed()
        }
    }
}
