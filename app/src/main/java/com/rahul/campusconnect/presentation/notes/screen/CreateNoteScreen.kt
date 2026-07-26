package com.rahul.campusconnect.presentation.notes.screen

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rahul.campusconnect.presentation.notes.viewmodel.CreateNoteViewModel
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteScreen(
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: CreateNoteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedFileSize by remember { mutableStateOf<String?>(null) }
    var selectedFileExt by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedFileUri = it
            val cursor = context.contentResolver.query(it, null, null, null, null)
            cursor?.use { c ->
                val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = c.getColumnIndex(OpenableColumns.SIZE)
                if (c.moveToFirst()) {
                    val name = c.getString(nameIndex)
                    selectedFileName = name
                    selectedFileExt = name.substringAfterLast(".", "pdf")
                    val size = c.getLong(sizeIndex)
                    selectedFileSize = formatFileSize(size)
                }
            }
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
            onBackClick()
            viewModel.resetSuccessState()
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Study Notes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Guidelines Card
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Ensure your notes are clear and follow campus guidelines. Support PDF, DOCX, PPTX, etc.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // File Picker Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable { filePickerLauncher.launch("*/*") }
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CloudUpload,
                            null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = selectedFileName ?: "Tap to Select File",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedFileName != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        if (selectedFileSize != null) {
                            Text(
                                text = "Size: $selectedFileSize",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        } else {
                            Text(
                                text = "Max file size: 20MB",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                AppTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Note Title *",
                    placeholder = "e.g. Data Structures Unit 1"
                )

                AppTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = "Subject *",
                    placeholder = "e.g. DSA"
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    AppTextField(
                        value = branch,
                        onValueChange = { branch = it },
                        label = "Branch *",
                        placeholder = "e.g. CSE",
                        modifier = Modifier.weight(1f)
                    )
                    AppTextField(
                        value = semester,
                        onValueChange = { semester = it },
                        label = "Semester *",
                        placeholder = "e.g. 4th",
                        modifier = Modifier.weight(1f)
                    )
                }

                AppTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description",
                    placeholder = "What's covered in these notes?",
                    singleLine = false,
                    modifier = Modifier.height(120.dp)
                )

                AppTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = "Tags (Comma separated)",
                    placeholder = "e.g. recursion, trees, exam"
                )

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(
                    text = "Upload Notes",
                    onClick = {
                        viewModel.createNote(
                            title = title,
                            description = description,
                            subject = subject,
                            semester = semester,
                            branch = branch,
                            fileUri = selectedFileUri!!,
                            fileExtension = selectedFileExt!!,
                            fileSize = selectedFileSize!!,
                            tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        )
                    },
                    enabled = title.isNotBlank() && subject.isNotBlank() && branch.isNotBlank() && semester.isNotBlank() && selectedFileUri != null && !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2563EB))
                }
            }
        }
    }

    uiState.error?.let { error ->
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
}

private fun formatFileSize(size: Long): String {
    val kb = size / 1024.0
    val mb = kb / 1024.0
    return when {
        mb >= 1 -> String.format("%.2f MB", mb)
        else -> String.format("%.2f KB", kb)
    }
}
