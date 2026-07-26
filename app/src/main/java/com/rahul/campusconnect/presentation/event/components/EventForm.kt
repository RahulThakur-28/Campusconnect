package com.rahul.campusconnect.presentation.event.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.core.imagepicker.CropType
import com.rahul.campusconnect.core.imagepicker.ImagePicker
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventForm(
    modifier: Modifier = Modifier,

    title: String,
    onTitleChange: (String) -> Unit,
    titleError: String? = null,

    description: String,
    onDescriptionChange: (String) -> Unit,
    descriptionError: String? = null,

    category: String,
    onCategoryChange: (String) -> Unit,
    categoryError: String? = null,

    startDate: Long,
    onStartDateChange: (Long) -> Unit,
    dateError: String? = null,

    time: String,
    onTimeChange: (String) -> Unit,
    timeError: String? = null,

    venue: String,
    onVenueChange: (String) -> Unit,
    venueError: String? = null,

    maxParticipants: String,
    onMaxParticipantsChange: (String) -> Unit,

    isRegistrationOpen: Boolean,
    onRegistrationOpenChange: (Boolean) -> Unit,

    buttonText: String,
    isLoading: Boolean = false,
    onSubmit: () -> Unit,

    imageUrl: String? = null,
    imageUri: Uri?,
    onImageSelected: (Uri) -> Unit,
    onRemoveImage: () -> Unit,

    loadingText: String = "Please wait..."
) {
    val categories = listOf(
        "Academic", "Workshop", "Hackathon", "Seminar", "Sports",
        "Cultural", "Placement", "Competition", "Technical", "Fest"
    )

    var categoryExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // Image Picker
        ImagePicker(
            imageUri = imageUri,
            imageUrl = imageUrl,
            cropType = CropType.BANNER,
            title = "Upload Event Banner",
            subtitle = "PNG, JPG (Recommended 16:9)",
            onImageSelected = onImageSelected,
            onRemoveImage = onRemoveImage
        )

        Spacer(modifier = Modifier.height(20.dp))

        AppTextField(
            value = title,
            onValueChange = onTitleChange,
            label = "Event Title *",
            placeholder = "Enter event title",
            isError = titleError != null,
            errorMessage = titleError
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = "Description *",
            placeholder = "Describe the event...",
            singleLine = false,
            modifier = Modifier.height(120.dp),
            isError = descriptionError != null,
            errorMessage = descriptionError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Dropdown
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category *") },
                placeholder = { Text("Select event category") },
                trailingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                isError = categoryError != null,
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(18.dp)
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categories.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            onCategoryChange(item)
                            categoryExpanded = false
                        }
                    )
                }
            }
        }
        if (categoryError != null) ErrorText(categoryError)

        Spacer(modifier = Modifier.height(16.dp))

        // Date Picker Trigger
        Text(text = "Start Date *", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
            shape = RoundedCornerShape(18.dp),
            border = if (dateError != null) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = if (startDate == 0L) "Select event date" else dateFormatter.format(Date(startDate)))
            }
        }
        if (dateError != null) ErrorText(dateError)

        Spacer(modifier = Modifier.height(16.dp))

        // Time Picker Trigger
        Text(text = "Start Time *", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().clickable { showTimePicker = true },
            shape = RoundedCornerShape(18.dp),
            border = if (timeError != null) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(18.dp)) {
                Icon(imageVector = Icons.Outlined.AccessTime, contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = time.ifBlank { "Select event time" })
            }
        }
        if (timeError != null) ErrorText(timeError)

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = venue,
            onValueChange = onVenueChange,
            label = "Venue *",
            placeholder = "Enter event venue",
            isError = venueError != null,
            errorMessage = venueError
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AppTextField(
                value = maxParticipants,
                onValueChange = onMaxParticipantsChange,
                label = "Max Participants",
                placeholder = "0 for unlimited",
                modifier = Modifier.weight(1f)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Registration", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = if (isRegistrationOpen) "Open" else "Closed", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isRegistrationOpen, onCheckedChange = onRegistrationOpenChange)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = if (isLoading) loadingText else buttonText,
            onClick = onSubmit,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(64.dp))
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { onStartDateChange(it) }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(initialHour = 10, initialMinute = 0, is24Hour = false)
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    val amPm = if (hour >= 12) "PM" else "AM"
                    val displayHour = when {
                        hour == 0 -> 12
                        hour > 12 -> hour - 12
                        else -> hour
                    }
                    val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, amPm)
                    onTimeChange(formattedTime)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}

@Composable
private fun ErrorText(error: String) {
    Text(
        text = error,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
    )
}
