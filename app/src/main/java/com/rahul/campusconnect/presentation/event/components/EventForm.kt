package com.rahul.campusconnect.presentation.event.components

import android.net.Uri
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

    date: String,
    onDateChange: (String) -> Unit,
    dateError: String? = null,

    time: String,
    onTimeChange: (String) -> Unit,
    timeError: String? = null,

    venue: String,
    onVenueChange: (String) -> Unit,
    venueError: String? = null,


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
        "Academic",
        "Workshop",
        "Hackathon",
        "Seminar",
        "Sports",
        "Cultural",
        "Placement",
        "Competition",
        "Technical",
        "Fest"
    )

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {

        // ----------------- image picker -----------------------------

        ImagePicker(

            imageUri = imageUri,

            cropType = CropType.BANNER,

            title = "Upload Event Banner",

            subtitle = "PNG, JPG",

            onImageSelected = onImageSelected,

            onRemoveImage = onRemoveImage
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ---------------- Event Title ----------------

        AppTextField(
            value = title,
            onValueChange = onTitleChange,
            label = "Event Title",
            placeholder = "Enter event title"
        )

        ErrorText(titleError)

        Spacer(modifier = Modifier.height(16.dp))


        // ---------------- Description ----------------

        AppTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = "Description",
            placeholder = "Describe the event...",
            singleLine = false,
            modifier = Modifier.height(120.dp)
        )

        ErrorText(descriptionError)

        Spacer(modifier = Modifier.height(16.dp))


        // ---------------- Category Dropdown ----------------

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = {
                categoryExpanded = !categoryExpanded
            }
        ) {

            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Category")
                },
                placeholder = {
                    Text("Select event category")
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "Select Category"
                    )
                },
                isError = categoryError != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(18.dp)
            )

            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = {
                    categoryExpanded = false
                }
            ) {

                categories.forEach { item ->

                    DropdownMenuItem(
                        text = {
                            Text(item)
                        },
                        onClick = {
                            onCategoryChange(item)
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        ErrorText(categoryError)

        Spacer(modifier = Modifier.height(16.dp))


        // ---------------- Date ----------------

        Text(
            text = "Date",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showDatePicker = true
                },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.outlinedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {

                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = date.ifBlank {
                        "Select event date"
                    }
                )
            }
        }

        ErrorText(dateError)

        Spacer(modifier = Modifier.height(16.dp))


        // ---------------- Time ----------------

        Text(
            text = "Time",
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    showTimePicker = true
                },
            shape = RoundedCornerShape(18.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {

                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = time.ifBlank {
                        "Select event time"
                    }
                )
            }
        }

        ErrorText(timeError)

        Spacer(modifier = Modifier.height(16.dp))


        // ---------------- Venue ----------------

        AppTextField(
            value = venue,
            onValueChange = onVenueChange,
            label = "Venue",
            placeholder = "Enter event venue"
        )

        ErrorText(venueError)


        Spacer(modifier = Modifier.height(32.dp))


        // ---------------- Publish Button ----------------
        PrimaryButton(
            text = if (isLoading) loadingText else buttonText,
            onClick = onSubmit,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Extra space for keyboard / bottom navigation
        Spacer(modifier = Modifier.height(32.dp))
    }


    // ========================================================
    // DATE PICKER
    // ========================================================

    if (showDatePicker) {

        val datePickerState =
            rememberDatePickerState()

        DatePickerDialog(

            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        datePickerState.selectedDateMillis?.let { millis ->

                            val formatter =
                                SimpleDateFormat(
                                    "dd MMM yyyy",
                                    Locale.getDefault()
                                )

                            onDateChange(
                                formatter.format(Date(millis))
                            )
                        }

                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            }

        ) {

            DatePicker(
                state = datePickerState
            )
        }
    }


    // ========================================================
    // TIME PICKER
    // ========================================================

    if (showTimePicker) {

        val timePickerState =
            rememberTimePickerState(
                initialHour = 10,
                initialMinute = 0,
                is24Hour = false
            )

        AlertDialog(

            onDismissRequest = {
                showTimePicker = false
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        val hour = timePickerState.hour
                        val minute = timePickerState.minute

                        val amPm =
                            if (hour >= 12) "PM"
                            else "AM"

                        val displayHour =
                            when {
                                hour == 0 -> 12
                                hour > 12 -> hour - 12
                                else -> hour
                            }

                        val formattedTime =
                            String.format(
                                Locale.getDefault(),
                                "%02d:%02d %s",
                                displayHour,
                                minute,
                                amPm
                            )

                        onTimeChange(formattedTime)

                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showTimePicker = false
                    }
                ) {
                    Text("Cancel")
                }
            },

            text = {

                TimePicker(
                    state = timePickerState
                )
            }
        )
    }
}


@Composable
private fun ErrorText(
    error: String?
) {

    if (error != null) {

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}