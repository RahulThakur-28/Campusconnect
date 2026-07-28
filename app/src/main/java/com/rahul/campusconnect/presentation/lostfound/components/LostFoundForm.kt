package com.rahul.campusconnect.presentation.lostfound.components

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.campusconnect.core.imagepicker.CropType
import com.rahul.campusconnect.core.imagepicker.ImagePicker
import com.rahul.campusconnect.core.imagepicker.ImagePickerState
import com.rahul.campusconnect.domain.model.LostFoundItem
import com.rahul.campusconnect.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostFoundForm(
    initialItem: LostFoundItem? = null,
    imagePickerState: ImagePickerState,
    onImageSelected: (Uri) -> Unit,
    onRemoveImage: () -> Unit,
    onSubmit: (String, String, String, String, String, String, String?) -> Unit,
    buttonText: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(initialItem?.title ?: "") }
    var description by remember { mutableStateOf(initialItem?.description ?: "") }
    var category by remember { mutableStateOf(initialItem?.category ?: "") }
    var type by remember { mutableStateOf(initialItem?.type ?: "LOST") }
    var location by remember { mutableStateOf(initialItem?.location ?: "") }
    var contactEmail by remember { mutableStateOf(initialItem?.contactEmail ?: "") }
    var contactPhone by remember { mutableStateOf(initialItem?.contactPhone ?: "") }
    var showErrors by remember { mutableStateOf(false) }

    val categories = listOf("Electronics", "Documents", "Accessories", "Books", "Personal Items", "Others")
    val scrollState = rememberScrollState()

    val isFormValid = title.isNotBlank() && category.isNotBlank() && location.isNotBlank() && 
            contactEmail.isNotBlank() && description.length >= 10

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Image Picker
        ImagePicker(
            imageUri = imagePickerState.imageUri,
            imageUrl = imagePickerState.imageUrl,
            cropType = CropType.BANNER,
            title = "Item Photograph",
            subtitle = "Recommended for better identification",
            onImageSelected = onImageSelected,
            onRemoveImage = onRemoveImage
        )

        // Type Selector
        Column {
            Text(
                text = "Report Type *",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TypeButton(
                    label = "LOST",
                    isSelected = type == "LOST",
                    selectedColor = Color(0xFFEF4444),
                    onClick = { type = "LOST" },
                    modifier = Modifier.weight(1f)
                )
                TypeButton(
                    label = "FOUND",
                    isSelected = type == "FOUND",
                    selectedColor = Color(0xFF3B82F6),
                    onClick = { type = "FOUND" },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        CampusTextField(
            value = title,
            onValueChange = { title = it; if(showErrors) showErrors = false },
            label = "Item Name *",
            placeholder = "e.g. Black HP Laptop Charger",
            leadingIcon = Icons.Rounded.Title,
            isError = showErrors && title.isBlank(),
            errorMessage = "Item name is required"
        )

        CampusDropdownField(
            label = "Category *",
            selectedItem = category,
            items = categories,
            onItemSelected = { category = it; if(showErrors) showErrors = false },
            placeholder = "Select Category",
            isError = showErrors && category.isBlank(),
            errorMessage = "Please select a category"
        )

        CampusTextField(
            value = location,
            onValueChange = { location = it; if(showErrors) showErrors = false },
            label = "Location *",
            placeholder = "e.g. Near Canteen Table 4",
            leadingIcon = Icons.Rounded.LocationOn,
            isError = showErrors && location.isBlank(),
            errorMessage = "Location is required"
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                CampusTextField(
                    value = contactEmail,
                    onValueChange = { contactEmail = it; if(showErrors) showErrors = false },
                    label = "Email *",
                    placeholder = "e.g. user@gmail.com",
                    leadingIcon = Icons.Rounded.Email,
                    isError = showErrors && contactEmail.isBlank()
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                CampusTextField(
                    value = contactPhone ?: "",
                    onValueChange = { contactPhone = it },
                    label = "Phone",
                    placeholder = "e.g. 98765...",
                    leadingIcon = Icons.Rounded.Phone
                )
            }
        }

        Column {
            CampusTextField(
                value = description,
                onValueChange = { description = it; if(showErrors) showErrors = false },
                label = "Additional Details *",
                placeholder = "Describe marks, brands, or unique features...",
                leadingIcon = Icons.Rounded.Description,
                singleLine = false,
                modifier = Modifier.height(150.dp),
                isError = showErrors && description.length < 10,
                errorMessage = "Description must be at least 10 characters"
            )
            Text(
                text = "${description.length} characters",
                style = MaterialTheme.typography.labelSmall,
                color = if (description.length < 10 && showErrors) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = buttonText,
            onClick = {
                if (isFormValid) {
                    onSubmit(title, description, category, type, location, contactEmail, contactPhone)
                } else {
                    showErrors = true
                }
            },
            isLoading = isLoading
        )
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
private fun TypeButton(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) selectedColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) selectedColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
        ),
        tonalElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}
