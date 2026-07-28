package com.rahul.campusconnect.presentation.event.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.core.imagepicker.CropType
import com.rahul.campusconnect.core.imagepicker.ImagePicker
import com.rahul.campusconnect.ui.components.*

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

        Spacer(modifier = Modifier.height(32.dp))

        CampusTextField(
            value = title,
            onValueChange = onTitleChange,
            label = "Event Title",
            placeholder = "What is the event called?",
            leadingIcon = Icons.Rounded.Title,
            isError = titleError != null,
            errorMessage = titleError
        )

        Spacer(modifier = Modifier.height(20.dp))

        CampusTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = "Description",
            placeholder = "Provide full details about the event",
            leadingIcon = Icons.Rounded.Description,
            singleLine = false,
            modifier = Modifier.height(140.dp),
            isError = descriptionError != null,
            errorMessage = descriptionError
        )

        Spacer(modifier = Modifier.height(20.dp))

        CampusDropdownField(
            label = "Category",
            selectedItem = category,
            items = categories,
            onItemSelected = onCategoryChange,
            placeholder = "Select Category",
            isError = categoryError != null,
            errorMessage = categoryError
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CampusDatePickerField(
                label = "Date",
                selectedDate = startDate,
                onDateSelected = onStartDateChange,
                isError = dateError != null,
                errorMessage = dateError,
                modifier = Modifier.weight(1f)
            )

            CampusTimePickerField(
                label = "Time",
                selectedTime = time,
                onTimeSelected = onTimeChange,
                isError = timeError != null,
                errorMessage = timeError,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        CampusTextField(
            value = venue,
            onValueChange = onVenueChange,
            label = "Venue",
            placeholder = "Where will it happen?",
            leadingIcon = Icons.Rounded.LocationOn,
            isError = venueError != null,
            errorMessage = venueError
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), 
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CampusTextField(
                value = maxParticipants,
                onValueChange = onMaxParticipantsChange,
                label = "Max Participants",
                placeholder = "0 for unlimited",
                leadingIcon = Icons.Rounded.People,
                modifier = Modifier.weight(1.2f)
            )
            
            Column(
                modifier = Modifier.weight(0.8f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Registration", 
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isRegistrationOpen) "Open" else "Closed", 
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isRegistrationOpen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = isRegistrationOpen, 
                        onCheckedChange = onRegistrationOpenChange,
                        scale = 0.8f
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButton(
            text = buttonText,
            onClick = onSubmit,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(64.dp))
    }
}

// Helper to scale components like Switch if needed, though not standard. 
// For now keeping it simple.
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    scale: Float = 1f
) {
    androidx.compose.material3.Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier.scale(scale)
    )
}
