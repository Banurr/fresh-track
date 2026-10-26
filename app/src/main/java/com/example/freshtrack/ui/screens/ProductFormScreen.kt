package com.example.freshtrack.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.freshtrack.domain.ExpiryRules
import com.example.freshtrack.domain.FormErrors
import com.example.freshtrack.model.Category
import com.example.freshtrack.model.Product
import com.example.freshtrack.ui.components.AppScaffold
import com.example.freshtrack.ui.components.BottomActionBar
import com.example.freshtrack.ui.components.CenteredMessage
import com.example.freshtrack.ui.components.ChipGroup
import com.example.freshtrack.ui.components.ConfirmDialog
import com.example.freshtrack.ui.components.formatDate
import java.time.LocalDate

private const val MILLIS_PER_DAY = 86_400_000L

private data class QuickDate(val label: String, val days: Long)

private val QuickDates = listOf(
    QuickDate("3 days", 3),
    QuickDate("1 week", 7),
    QuickDate("2 weeks", 14),
    QuickDate("30 days", 30)
)

@Composable
fun ProductFormScreen(
    product: Product?,
    today: LocalDate,
    onBack: () -> Unit,
    onValidate: (String, String, Long?) -> FormErrors,
    onSave: (String, Category, String, Long?) -> Boolean,
    onDelete: () -> Unit
) {
    val isEdit = product != null
    var name by rememberSaveable { mutableStateOf(product?.name.orEmpty()) }
    var category by rememberSaveable { mutableStateOf(product?.category ?: Category.OTHER) }
    var quantity by rememberSaveable { mutableStateOf((product?.quantity ?: 1).toString()) }
    var expiryEpochDay by rememberSaveable { mutableStateOf(product?.expiryDate?.toEpochDay()) }
    var attempted by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }

    val errors = if (attempted) onValidate(name, quantity, expiryEpochDay) else FormErrors()
    val todayEpochDay = today.toEpochDay()

    AppScaffold(
        title = if (isEdit) "Edit product" else "Add product",
        onBack = onBack,
        bottomBar = {
            BottomActionBar(
                label = "Save",
                onClick = {
                    attempted = true
                    if (onSave(name, category, quantity, expiryEpochDay)) onBack()
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product name") },
                singleLine = true,
                isError = errors.name != null,
                supportingText = { errors.name?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Category", style = MaterialTheme.typography.titleSmall)
                ChipGroup(
                    options = Category.entries,
                    isSelected = { it == category },
                    label = { it.label },
                    onClick = { category = it }
                )
            }

            OutlinedTextField(
                value = quantity,
                onValueChange = { input -> quantity = input.filter { it in '0'..'9' }.take(3) },
                label = { Text("Quantity") },
                singleLine = true,
                isError = errors.quantity != null,
                supportingText = { errors.quantity?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Expiry date", style = MaterialTheme.typography.titleSmall)
                ChipGroup(
                    options = QuickDates,
                    isSelected = { expiryEpochDay == todayEpochDay + it.days },
                    label = { it.label },
                    onClick = { expiryEpochDay = todayEpochDay + it.days }
                )
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        expiryEpochDay?.let { formatDate(LocalDate.ofEpochDay(it)) } ?: "Choose date"
                    )
                }
                expiryEpochDay?.let { day ->
                    Text(
                        text = ExpiryRules.describe(day - todayEpochDay),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                errors.date?.let { message ->
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            if (isEdit) {
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete product")
                }
            }
        }
    }

    if (showDatePicker) {
        ExpiryDatePickerDialog(
            initialEpochDay = expiryEpochDay ?: todayEpochDay,
            onConfirm = { day ->
                expiryEpochDay = day
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showDeleteConfirm) {
        ConfirmDialog(
            title = "Delete product?",
            message = "It will be removed completely and will not appear in statistics.",
            confirmLabel = "Delete",
            onConfirm = {
                showDeleteConfirm = false
                onDelete()
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExpiryDatePickerDialog(
    initialEpochDay: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialEpochDay * MILLIS_PER_DAY
    )
    val selectedMillis = pickerState.selectedDateMillis

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { selectedMillis?.let { onConfirm(it / MILLIS_PER_DAY) } },
                enabled = selectedMillis != null
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) {
        DatePicker(state = pickerState)
    }
}

@Composable
fun ProductNotFoundScreen(onBack: () -> Unit) {
    AppScaffold(title = "Product", onBack = onBack) { padding ->
        CenteredMessage(
            title = "Product not found",
            message = "This product no longer exists.",
            modifier = Modifier.padding(padding),
            actionLabel = "Go back",
            onAction = onBack
        )
    }
}
