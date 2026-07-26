package com.rahul.campusconnect.presentation.notes.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rahul.campusconnect.common.utils.TimeUtils
import com.rahul.campusconnect.presentation.notes.viewmodel.NoteDetailsViewModel
import com.rahul.campusconnect.ui.components.CardImageHeader
import com.rahul.campusconnect.ui.components.EmptyState
import com.rahul.campusconnect.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsScreen(
    noteId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    navController: NavController,
    viewModel: NoteDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
            onBackClick()
            viewModel.resetDeleteState()
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete these notes? this action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteNote()
                }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Note Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.canEdit) {
                        IconButton(onClick = { onEditClick(noteId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, uiState.note?.title)
                            putExtra(Intent.EXTRA_TEXT, "Check out these notes for ${uiState.note?.subject}: ${uiState.note?.fileUrl}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share notes via"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.note != null) {
                Surface(tonalElevation = 8.dp, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PrimaryButton(
                            text = "Open Notes",
                            onClick = {
                                viewModel.incrementDownloadCount()
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uiState.note!!.fileUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(
                            onClick = {
                                viewModel.incrementDownloadCount()
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uiState.note!!.fileUrl))
                                context.startActivity(intent)
                                // Standard browser download usually works with ACTION_VIEW for files.
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Download, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Download")
                        }
                    }
                }
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                EmptyState(
                    message = uiState.error ?: "Error occurred",
                    buttonText = "Retry",
                    onButtonClick = { viewModel.loadNote(noteId) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            uiState.note != null -> {
                val note = uiState.note!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                ) {
                    // Thumbnail / Header
                    CardImageHeader(
                        imageUrl = note.thumbnailUrl,
                        category = note.subject,
                        categoryColor = Color(0xFF2563EB),
                        height = 220.dp
                    )

                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = note.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${note.branch} • Semester ${note.semester}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Uploader Info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = note.uploadedByName.ifBlank { "Anonymous" }, fontWeight = FontWeight.Bold)
                                    if (note.isVerified) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = Color(0xFF2563EB),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (note.uploadedByRole == "ADMIN") "Administrator" else "Verified Student",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Metadata Grid
                        Row(modifier = Modifier.fillMaxWidth()) {
                            MetadataItem(Icons.Default.FilePresent, "Format", note.fileType, Modifier.weight(1f))
                            MetadataItem(Icons.Default.Download, "Downloads", note.downloadCount.toString(), Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            MetadataItem(Icons.Default.CalendarToday, "Uploaded", TimeUtils.getRelativeTime(note.createdAt), Modifier.weight(1f))
                            MetadataItem(Icons.Default.Storage, "Size", note.fileSize, Modifier.weight(1f))
                        }

                        if (note.tags.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(text = "Tags", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                note.tags.forEach { tag ->
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(tag) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(text = "Description", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = note.description.ifBlank { "No description provided for these notes." },
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom bar
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        content = { content() }
    )
}

@Composable
private fun MetadataItem(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}
