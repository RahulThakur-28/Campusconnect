package com.rahul.campusconnect.presentation.announcement.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAnnouncementScreen(
    onBackClick: () -> Unit,
    onPublishSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageName by remember { mutableStateOf<String?>(null) }
    var selectedAttachmentName by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publish Announcement", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                modifier = Modifier.height(150.dp)
            )

            Text(
                text = "Optional Attachments",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Image Picker Placeholder
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                        .clickable { /* TODO: Image Picker */ },
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = selectedImageName ?: "Add Banner",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                // File Picker Placeholder
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(16.dp))
                        .clickable { /* TODO: PDF Picker */ },
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Attachment, contentDescription = null, tint = Color.Gray)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = selectedAttachmentName ?: "Add PDF",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Publish Announcement",
                onClick = {
                    // Simulate Publish logic
                    onPublishSuccess()
                },
                enabled = title.isNotEmpty() && category.isNotEmpty() && description.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
