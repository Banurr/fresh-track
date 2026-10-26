package com.example.freshtrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.freshtrack.domain.ProductStats
import com.example.freshtrack.ui.components.AppScaffold
import com.example.freshtrack.ui.components.CenteredMessage
import com.example.freshtrack.ui.components.InfoCard
import com.example.freshtrack.ui.components.LabelValueRow
import com.example.freshtrack.ui.components.MainBottomBar
import com.example.freshtrack.ui.components.MainTab
import com.example.freshtrack.ui.components.StatCard

@Composable
fun StatsScreen(
    stats: ProductStats,
    onTabSelected: (MainTab) -> Unit
) {
    AppScaffold(
        title = "Statistics",
        bottomBar = { MainBottomBar(selected = MainTab.STATS, onSelect = onTabSelected) }
    ) { padding ->
        if (stats.total == 0) {
            CenteredMessage(
                title = "No history yet",
                message = "Mark products as used or discarded and your results will appear here.",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            label = "Used",
                            value = stats.used.toString(),
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        StatCard(
                            label = "Discarded",
                            value = stats.discarded.toString(),
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                item {
                    val rate = stats.wasteRatePercent ?: 0
                    InfoCard {
                        Text("Waste rate", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "$rate% of finished products were discarded",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        LinearProgressIndicator(
                            progress = { rate / 100f },
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                        )
                    }
                }
                item {
                    InfoCard {
                        Text("Discarded by category", style = MaterialTheme.typography.titleMedium)
                        if (stats.discardedByCategory.isEmpty()) {
                            Text(
                                text = "Nothing discarded yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        stats.discardedByCategory.forEach { entry ->
                            LabelValueRow(
                                label = entry.category.label,
                                value = entry.count.toString()
                            )
                        }
                    }
                }
            }
        }
    }
}
