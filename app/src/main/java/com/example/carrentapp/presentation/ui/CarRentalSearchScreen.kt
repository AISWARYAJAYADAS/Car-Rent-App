package com.example.carrentapp.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carrentapp.ui.theme.CarRentAppTheme
import com.example.carrentapp.ui.theme.LocalDarkMode
import com.example.carrentapp.presentation.state.CarRentalState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarRentalSearchScreen(
    state: CarRentalState,
    pickupSuggestions: List<String>,
    dropOffSuggestions: List<String>,
    onPickupLocationChanged: (String) -> Unit,
    onDropOffLocationChanged: (String) -> Unit,
    onPickupDateChanged: (String) -> Unit,
    onDropOffDateChanged: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val isDarkTheme = LocalDarkMode.current
    var showPickupDatePicker by remember { mutableStateOf(false) }
    var showDropOffDatePicker by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val todayMillis = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    LaunchedEffect(state) {
        when (state) {
            is CarRentalState.Navigate -> context.startActivity(state.intent)
            is CarRentalState.Error -> snackBarHostState.showSnackbar(state.message)
            is CarRentalState.Inputs -> {}
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            val inputs = state as? CarRentalState.Inputs ?: CarRentalState.Inputs()

            // Header with Dark Mode Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Car Rental Search",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onToggleDarkMode,
                    modifier = Modifier.semantics { contentDescription = "Toggle dark mode" },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                )
            }

            // Pickup Location (Mandatory)
            LocationTextField(
                value = inputs.pickupLocation,
                onValueChange = onPickupLocationChanged,
                label = "Pickup Location", // Base label without asterisk
                placeholder = "e.g., Los Angeles, CA",
                suggestions = pickupSuggestions,
                isError = state is CarRentalState.Error && inputs.pickupLocation.isBlank(),
                errorMessage = "Pickup location is required",
                contentDescription = "Pickup location input",
                isMandatory = true // Add red asterisk
            )

            // Drop-off Location (Optional)
            LocationTextField(
                value = inputs.dropOffLocation ?: "",
                onValueChange = onDropOffLocationChanged,
                label = "Drop-off Location", // No asterisk
                placeholder = "e.g., SFO",
                suggestions = dropOffSuggestions,
                isError = false,
                errorMessage = "",
                contentDescription = "Drop-off location input",
                isMandatory = false // No red asterisk
            )

            // Pickup Date (Mandatory)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        text = "Pickup",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = " *",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = ": ${inputs.pickupDate.ifBlank { "Select Date" }}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                FilledTonalButton(
                    onClick = { showPickupDatePicker = true },
                    modifier = Modifier.semantics { contentDescription = "Select pickup date" },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Pick Date", fontSize = 14.sp)
                }
            }

            if (showPickupDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = todayMillis,
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis >= todayMillis
                        }
                    }
                )
                DatePickerDialog(
                    onDismissRequest = { showPickupDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = dateFormat.format(Date(millis))
                                onPickupDateChanged(date)
                            }
                            showPickupDatePicker = false
                        }) { Text("OK", color = MaterialTheme.colorScheme.primary) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showPickupDatePicker = false }) { Text("Cancel") }
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    DatePicker(
                        state = datePickerState,
                        title = { Text("Select Pickup Date", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold) },
                        colors = DatePickerDefaults.colors(
                            selectedDayContainerColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            // Drop-off Date (Mandatory)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        text = "Drop-off",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = " *",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = ": ${inputs.dropOffDate.ifBlank { "Select Date" }}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                FilledTonalButton(
                    onClick = { showDropOffDatePicker = true },
                    modifier = Modifier.semantics { contentDescription = "Select drop-off date" },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Pick Date", fontSize = 14.sp)
                }
            }

            if (showDropOffDatePicker) {
                val pickupDateMillis = inputs.pickupDate.takeIf { it.isNotBlank() }
                    ?.let { dateFormat.parse(it)?.time ?: todayMillis } ?: todayMillis
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = pickupDateMillis,
                    selectableDates = object : SelectableDates {
                        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                            return utcTimeMillis >= pickupDateMillis
                        }
                    }
                )
                DatePickerDialog(
                    onDismissRequest = { showDropOffDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = dateFormat.format(Date(millis))
                                onDropOffDateChanged(date)
                            }
                            showDropOffDatePicker = false
                        }) { Text("OK", color = MaterialTheme.colorScheme.primary) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDropOffDatePicker = false }) { Text("Cancel") }
                    },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    DatePicker(
                        state = datePickerState,
                        title = { Text("Select Drop-off Date", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold) },
                        colors = DatePickerDefaults.colors(
                            selectedDayContainerColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            // Search Button
            Button(
                onClick = onSearchClicked,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = inputs.isValid(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    "Search on Kayak",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    fontSize = 16.sp
                )
            }
        }
    }
}

// Reusable composable for location text fields
@Composable
fun LocationTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    suggestions: List<String>,
    isError: Boolean,
    errorMessage: String,
    contentDescription: String,
    isMandatory: Boolean = false // Added parameter for red asterisk
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                expanded = it.isNotEmpty() && suggestions.isNotEmpty()
            },
            label = {
                Row {
                    Text(label, fontWeight = FontWeight.Medium)
                    if (isMandatory) {
                        Text(
                            text = " *",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            placeholder = {
                Text(
                    placeholder,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { this.contentDescription = contentDescription },
            singleLine = true,
            isError = isError,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        )
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
        DropdownMenu(
            expanded = expanded && suggestions.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            suggestions.forEach { suggestion ->
                DropdownMenuItem(
                    text = { Text(suggestion, style = MaterialTheme.typography.bodyMedium) },
                    onClick = {
                        onValueChange(suggestion)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CarRentalSearchScreenPreviewLight() {
    CarRentAppTheme(useDarkTheme = false) {
        CarRentalSearchScreen(
            state = CarRentalState.Inputs(
                pickupLocation = "Los Angeles, CA",
                dropOffLocation = "SFO",
                pickupDate = "2025-03-01",
                dropOffDate = "2025-03-05"
            ),
            pickupSuggestions = listOf("Los Angeles, CA", "Los Alamos, NM"),
            dropOffSuggestions = listOf("SFO", "San Diego, CA"),
            onPickupLocationChanged = {},
            onDropOffLocationChanged = {},
            onPickupDateChanged = {},
            onDropOffDateChanged = {},
            onSearchClicked = {},
            onToggleDarkMode = {}
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CarRentalSearchScreenPreviewDark() {
    CarRentAppTheme(useDarkTheme = true) {
        CarRentalSearchScreen(
            state = CarRentalState.Inputs(
                pickupLocation = "Los Angeles, CA",
                dropOffLocation = "SFO",
                pickupDate = "2025-03-01",
                dropOffDate = "2025-03-05"
            ),
            pickupSuggestions = listOf("Los Angeles, CA", "Los Alamos, NM"),
            dropOffSuggestions = listOf("SFO", "San Diego, CA"),
            onPickupLocationChanged = {},
            onDropOffLocationChanged = {},
            onPickupDateChanged = {},
            onDropOffDateChanged = {},
            onSearchClicked = {},
            onToggleDarkMode = {}
        )
    }
}