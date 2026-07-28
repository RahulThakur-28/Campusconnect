package com.rahul.campusconnect.presentation.announcement.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.campusconnect.core.imagepicker.CropType
import com.rahul.campusconnect.core.imagepicker.ImagePicker
import com.rahul.campusconnect.core.imagepicker.ImagePickerState
import com.rahul.campusconnect.domain.model.Announcement
import com.rahul.campusconnect.ui.components.*

@Composable
fun AnnouncementForm(
    initialAnnouncement: Announcement? = null,
    imagePickerState: ImagePickerState,
    onImageSelected: (Uri) -> Unit,
    onRemoveImage: () -> Unit,
    attachmentUri: Uri?,
    attachmentUrl: String?,
    onAttachmentSelected: (Uri) -> Unit,
    onRemoveAttachment: () -> Unit,
    onSubmit: (String, String, String, Uri?, Uri?) -> Unit,
    buttonText: String,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf(initialAnnouncement?.title ?: "") }
    var category by remember { mutableStateOf(initialAnnouncement?.category ?: "") }
    var description by remember { mutableStateOf(initialAnnouncement?.description ?: "") }
    var showErrors by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    
    val categories = listOf("Academic", "Events", "Placement", "Exam", "Holiday", "Sports", "Other")

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onAttachmentSelected(it) }
    }

    val isFormValid = title.isNotBlank() && category.isNotBlank() && description.length >= 20

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
            title = "Announcement Banner",
            subtitle = "PNG, JPG (Recommended 16:9 ratio)",
            onImageSelected = onImageSelected,
            onRemoveImage = onRemoveImage
        )

        CampusTextField(
            value = title,
            onValueChange = { title = it; if(showErrors) showErrors = false },
            label = "Title",
            placeholder = "What is this announcement about?",
            leadingIcon = Icons.Rounded.Title,
            isError = showErrors && title.isBlank(),
            errorMessage = "Title is required"
        )

        CampusDropdownField(
            label = "Category",
            selectedItem = category,
            items = categories,
            onItemSelected = { category = it; if(showErrors) showErrors = false },
            placeholder = "Select Category",
            isError = showErrors && category.isBlank(),
            errorMessage = "Please select a category"
        )

        Column {
            CampusTextField(
                value = description,
                onValueChange = { description = it; if(showErrors) showErrors = false },
                label = "Description",
                placeholder = "Write detailed description here (Min 20 characters)...",
                leadingIcon = Icons.Rounded.Description,
                singleLine = false,
                modifier = Modifier.height(200.dp),
                isError = showErrors && description.length < 20,
                errorMessage = "Minimum 20 characters required"
            )
            
            Text(
                text = "${description.length} characters",
                style = MaterialTheme.typography.labelSmall,
                color = if (description.length < 20) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
            )
        }
        
        Column {
            Text(
                text = "Attachment (Optional)",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )

            Surface(
                onClick = { pdfPickerLauncher.launch("application/pdf") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, 
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                ),
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Attachment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = when {
                            attachmentUri != null -> "New PDF Attached"
                            !attachmentUrl.isNullOrEmpty() -> "Document_Attached.pdf"
                            else -> "Attach JD or PDF Document"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (attachmentUri != null || !attachmentUrl.isNullOrEmpty()) 
                            MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontWeight = if (attachmentUri != null || !attachmentUrl.isNullOrEmpty()) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                    
                    if (attachmentUri != null || !attachmentUrl.isNullOrEmpty()) {
                        IconButton(onClick = onRemoveAttachment) {
                            Icon(
                                imageVector = Icons.Rounded.Delete, 
                                contentDescription = "Remove",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.CloudUpload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = buttonText,
            onClick = {
                if (isFormValid) {
                    onSubmit(title, description, category, imagePickerState.imageUri, attachmentUri)
                } else {
                    showErrors = true
                }
            },
            isLoading = isLoading
        )
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}
