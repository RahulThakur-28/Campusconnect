package com.rahul.campusconnect.presentation.notes.screen

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rahul.campusconnect.core.imagepicker.CropType
import com.rahul.campusconnect.core.imagepicker.ImagePicker
import com.rahul.campusconnect.presentation.notes.viewmodel.EditNoteViewModel
import com.rahul.campusconnect.ui.components.*
import com.rahul.campusconnect.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    noteId: String,
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: EditNoteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var branch by remember { mutableStateOf("") }
    var semester by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedFileSize by remember { mutableStateOf<String?>(null) }
    var selectedFileExt by remember { mutableStateOf<String?>(null) }

    var newThumbnailUri by remember { mutableStateOf<Uri?>(null) }
    var removeThumbnail by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    LaunchedEffect(uiState.note) {
        uiState.note?.let { note ->
            title = note.title
            subject = note.subject
            branch = note.branch
            semester = note.semester
            description = note.description
            tags = note.tags.joinToString(", ")
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
            navController.previousBackStackEntry?.savedStateHandle?.set("snackbar_message", "Study notes updated successfully")
            onBackClick()
            viewModel.resetSuccessState()
        }
    }

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

    val scrollState = rememberScrollState()
    val branches = listOf("CSE", "IT", "ECE", "ME", "CE", "EE", "MBA", "MCA", "Other")
    val semesters = listOf("1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th")

    Scaffold(
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                val msg = data.visuals.message.lowercase()
                val containerColor = if (msg.contains("success")) SuccessGreen else MaterialTheme.colorScheme.error
                Snackbar(
                    snackbarData = data,
                    containerColor = containerColor,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Edit Notes", fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading && uiState.note == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 3.dp)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .imePadding()
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // File Change Section
                    Column {
                        Text(
                            text = "Note File",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                        Surface(
                            onClick = { filePickerLauncher.launch("*/*") },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, 
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                            ),
                            tonalElevation = 2.dp
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    if (selectedFileUri != null) Icons.Rounded.TaskAlt else Icons.Rounded.CloudUpload,
                                    null,
                                    tint = if (selectedFileUri != null) SuccessGreen else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = selectedFileName ?: "Change File (Optional)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (selectedFileSize != null) {
                                    Text(
                                        text = "$selectedFileSize • ${selectedFileExt?.uppercase()}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                } else {
                                    Text(
                                        text = "Current: ${uiState.note?.fileType} (${uiState.note?.fileSize})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }

                    // Image Picker
                    ImagePicker(
                        imageUri = newThumbnailUri,
                        imageUrl = if (!removeThumbnail) uiState.note?.thumbnailUrl else null,
                        cropType = CropType.BANNER,
                        title = "Cover Image (Optional)",
                        subtitle = "Update or remove notes cover image",
                        onImageSelected = { 
                            newThumbnailUri = it
                            removeThumbnail = false
                        },
                        onRemoveImage = { 
                            newThumbnailUri = null
                            removeThumbnail = true
                        }
                    )

                    CampusTextField(
                        value = title,
                        onValueChange = { title = it; if(showErrors) showErrors = false },
                        label = "Title *",
                        placeholder = "e.g. Computer Networks Unit 1",
                        leadingIcon = Icons.Rounded.Title,
                        isError = showErrors && title.isBlank(),
                        errorMessage = "Title is required"
                    )

                    CampusTextField(
                        value = subject,
                        onValueChange = { subject = it; if(showErrors) showErrors = false },
                        label = "Subject *",
                        placeholder = "e.g. Operating Systems",
                        leadingIcon = Icons.AutoMirrored.Rounded.MenuBook,
                        isError = showErrors && subject.isBlank(),
                        errorMessage = "Subject is required"
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            CampusDropdownField(
                                label = "Branch *",
                                selectedItem = branch,
                                items = branches,
                                onItemSelected = { branch = it; if(showErrors) showErrors = false },
                                placeholder = "Select Branch",
                                isError = showErrors && branch.isBlank()
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            CampusDropdownField(
                                label = "Semester *",
                                selectedItem = semester,
                                items = semesters,
                                onItemSelected = { semester = it; if(showErrors) showErrors = false },
                                placeholder = "Select Sem",
                                isError = showErrors && semester.isBlank()
                            )
                        }
                    }

                    Column {
                        CampusTextField(
                            value = description,
                            onValueChange = { description = it; if(showErrors) showErrors = false },
                            label = "Summary / Description",
                            placeholder = "What topics are covered in these notes?",
                            leadingIcon = Icons.Rounded.Description,
                            singleLine = false,
                            modifier = Modifier.height(150.dp),
                            isError = showErrors && description.length > 500,
                            errorMessage = "Description too long"
                        )
                        Text(
                            text = "${description.length}/500",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                        )
                    }

                    CampusTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = "Tags (Optional)",
                        placeholder = "e.g. important, exam, ip, tcp",
                        leadingIcon = Icons.Rounded.Tag
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    PrimaryButton(
                        text = "Save Changes",
                        onClick = {
                            if (title.isNotBlank() && subject.isNotBlank() && branch.isNotBlank() && semester.isNotBlank()) {
                                viewModel.updateNote(
                                    title = title,
                                    description = description,
                                    subject = subject,
                                    semester = semester,
                                    branch = branch,
                                    tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                    newFileUri = selectedFileUri,
                                    newFileExtension = selectedFileExt,
                                    newFileSize = selectedFileSize,
                                    newThumbnailUri = newThumbnailUri,
                                    removeThumbnail = removeThumbnail
                                )
                            } else {
                                showErrors = true
                            }
                        },
                        isLoading = uiState.isSubmitting
                    )

                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
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
