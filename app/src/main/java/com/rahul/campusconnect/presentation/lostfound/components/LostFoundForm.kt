package com.rahul.campusconnect.presentation.lostfound.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.core.imagepicker.CropType
import com.rahul.campusconnect.core.imagepicker.ImagePicker
import com.rahul.campusconnect.core.imagepicker.ImagePickerState
import com.rahul.campusconnect.domain.model.LostFoundItem
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField

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

    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Electronics", "Documents", "Accessories", "Books", "Personal Items", "Others")

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ImagePicker(
            imageUri = imagePickerState.imageUri,
            imageUrl = imagePickerState.imageUrl,
            cropType = CropType.BANNER,
            title = "Item Image",
            subtitle = "Optional photo of the item",
            onImageSelected = onImageSelected,
            onRemoveImage = onRemoveImage
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FilterChip(
                selected = type == "LOST",
                onClick = { type = "LOST" },
                label = { Text("LOST") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = type == "FOUND",
                onClick = { type = "FOUND" },
                label = { Text("FOUND") },
                modifier = Modifier.weight(1f)
            )
        }

        AppTextField(
            value = title,
            onValueChange = { title = it },
            label = "Item Title *",
            placeholder = "e.g. Blue Milton Water Bottle"
        )

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category *") },
                placeholder = { Text("Select Category") },
                trailingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(18.dp)
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            category = cat
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        AppTextField(
            value = location,
            onValueChange = { location = it },
            label = "Location *",
            placeholder = "e.g. Library, 2nd Floor"
        )

        AppTextField(
            value = contactEmail,
            onValueChange = { contactEmail = it },
            label = "Contact Email *",
            placeholder = "yourname@example.com"
        )

        AppTextField(
            value = contactPhone ?: "",
            onValueChange = { contactPhone = it },
            label = "Contact Phone (Optional)",
            placeholder = "e.g. 9876543210"
        )

        AppTextField(
            value = description,
            onValueChange = { description = it },
            label = "Description *",
            placeholder = "Detailed description of the item...",
            singleLine = false,
            modifier = Modifier.height(150.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = buttonText,
            onClick = {
                onSubmit(title, description, category, type, location, contactEmail, contactPhone)
            },
            enabled = title.isNotBlank() && category.isNotBlank() && location.isNotBlank() && contactEmail.isNotBlank() && description.isNotBlank() && !isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
