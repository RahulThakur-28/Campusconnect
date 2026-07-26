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
import androidx.compose.material.icons.filled.Close
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
import com.rahul.campusconnect.presentation.notes.viewmodel.EditNoteViewModel
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField

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

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    LaunchedEffect(uiState.note) {
        uiState.note?.let {
            title = it.title
            subject = it.subject
            branch = it.branch
            semester = it.semester
            description = it.description
            tags = it.tags.joinToString(", ")
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Study Notes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.note != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // File Selection Area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .clickable { filePickerLauncher.launch("*/*") }
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudUpload, null, tint = Color(0xFF2563EB))
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = selectedFileName ?: "Replace current file (Optional)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (selectedFileUri != null) {
                                IconButton(onClick = {
                                    selectedFileUri = null
                                    selectedFileName = null
                                }) {
                                    Icon(Icons.Default.Close, null)
                                }
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
                        text = "Update Notes",
                        onClick = {
                            viewModel.updateNote(
                                title = title,
                                description = description,
                                subject = subject,
                                semester = semester,
                                branch = branch,
                                tags = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                newFileUri = selectedFileUri,
                                newFileExtension = selectedFileExt,
                                newFileSize = selectedFileSize
                            )
                        },
                        enabled = title.isNotBlank() && subject.isNotBlank() && branch.isNotBlank() && semester.isNotBlank() && !uiState.isSubmitting
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            if (uiState.isSubmitting) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.1f)).clickable(enabled = false) {},
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
