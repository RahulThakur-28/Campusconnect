package com.rahul.campusconnect.presentation.announcement.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.core.imagepicker.CropType
import com.rahul.campusconnect.core.imagepicker.ImagePicker
import com.rahul.campusconnect.core.imagepicker.ImagePickerState
import com.rahul.campusconnect.domain.model.Announcement
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField

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

    val scrollState = rememberScrollState()
    
    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onAttachmentSelected(it) }
    }

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
            title = "Announcement Banner",
            subtitle = "Optional banner image",
            onImageSelected = onImageSelected,
            onRemoveImage = onRemoveImage
        )

        AppTextField(
            value = title,
            onValueChange = { title = it },
            label = "Announcement Title *",
            placeholder = "e.g. Exam Schedule Revised"
        )

        AppTextField(
            value = category,
            onValueChange = { category = it },
            label = "Category *",
            placeholder = "e.g. Academic, Exam, Holiday"
        )

        AppTextField(
            value = description,
            onValueChange = { description = it },
            label = "Description *",
            placeholder = "Detailed information...",
            singleLine = false,
            modifier = Modifier.height(200.dp)
        )
        
        Text(
            text = "PDF Attachment",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                .clickable { pdfPickerLauncher.launch("application/pdf") },
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Attachment, contentDescription = null, tint = Color.Gray)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = when {
                        attachmentUri != null -> "PDF Selected"
                        !attachmentUrl.isNullOrEmpty() -> "Existing PDF"
                        else -> "Attach PDF (Optional)"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
                
                if (attachmentUri != null || !attachmentUrl.isNullOrEmpty()) {
                    IconButton(onClick = onRemoveAttachment) {
                        Icon(Icons.Default.Close, contentDescription = "Remove")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = buttonText,
            onClick = {
                onSubmit(title, description, category, imagePickerState.imageUri, attachmentUri)
            },
            enabled = title.isNotEmpty() && category.isNotEmpty() && description.isNotEmpty() && !isLoading
        )
    }
}
