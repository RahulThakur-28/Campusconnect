package com.rahul.campusconnect.presentation.placement.screen

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
import androidx.compose.material.icons.rounded.Attachment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rahul.campusconnect.common.utils.TimeUtils
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.presentation.discussion.components.DiscussionSection
import com.rahul.campusconnect.presentation.placement.viewmodel.PlacementDetailsViewModel
import com.rahul.campusconnect.ui.components.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlacementDetailsScreen(
    placementId: String,
    onBackClick: () -> Unit,
    onViewDiscussionClick: () -> Unit,
    onEditClick: (String) -> Unit,
    navController: NavController,
    viewModel: PlacementDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(placementId) {
        viewModel.loadPlacement(placementId)
    }

    LaunchedEffect(uiState.deleted) {
        if (uiState.deleted) {
            viewModel.resetDeleteState()
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
            onBackClick()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Placement") },
            text = { Text("Are you sure you want to delete this placement drive? This action is reversible only by admins.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deletePlacement()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    if (uiState.deleting) CircularProgressIndicator(Modifier.size(18.dp))
                    else Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Placement Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "🏢 ${uiState.placement?.companyName}\n💼 ${uiState.placement?.jobRole}\n💰 ${uiState.placement?.packageLpa}\n📍 ${uiState.placement?.location}\nApply: ${uiState.placement?.applyLink}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Placement"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }

                    if (uiState.canEdit) {
                        IconButton(onClick = { onEditClick(placementId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            )
        },
        bottomBar = {
            uiState.placement?.let { placement ->
                Surface(tonalElevation = 8.dp, shadowElevation = 8.dp) {
                    PrimaryButton(
                        text = when {
                            placement.deleted -> "Placement Removed"
                            uiState.isExpired -> "Application Closed"
                            else -> "Apply Now"
                        },
                        enabled = uiState.canApply,
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(placement.applyLink))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.padding(16.dp)
                    )
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
                    message = uiState.error.orEmpty(),
                    buttonText = "Retry",
                    onButtonClick = viewModel::refresh,
                    modifier = Modifier.fillMaxSize()
                )
            }
            uiState.placement != null -> {
                val placement = uiState.placement!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                        .padding(24.dp)
                ) {
                    // Company Info Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (placement.logoUrl.isNotBlank()) {
                            Card(
                                modifier = Modifier.size(80.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                AsyncImage(
                                    model = placement.logoUrl,
                                    contentDescription = placement.companyName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Business, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Column {
                            Text(text = placement.companyName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text(text = placement.jobRole, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(4.dp))
                            StatusPill(
                                text = if (uiState.isExpired) "Expired" else placement.status,
                                containerColor = if (uiState.isExpired) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                                contentColor = if (uiState.isExpired) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Quick Info Grid
                    Row(modifier = Modifier.fillMaxWidth()) {
                        DetailInfoItem(Icons.Default.CurrencyRupee, "Package", placement.packageLpa, Modifier.weight(1f))
                        DetailInfoItem(Icons.Default.LocationOn, "Location", placement.location, Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        DetailInfoItem(Icons.Default.Work, "Job Type", placement.jobType, Modifier.weight(1f))
                        DetailInfoItem(Icons.Default.Group, "Openings", placement.openings.toString(), Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    SectionTitle("Eligibility")
                    Text(text = placement.eligibility, style = MaterialTheme.typography.bodyLarge)

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionTitle("Deadline")
                    Text(
                        text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(placement.deadline)),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (uiState.isExpired) Color.Red else MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionTitle("Required Skills")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        placement.requiredSkills.forEach { skill ->
                            SuggestionChip(onClick = {}, label = { Text(skill) })
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    SectionTitle("Description")
                    Text(text = placement.description, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)

                    if (placement.applicationProcess.isNotBlank()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionTitle("Application Process")
                        Text(text = placement.applicationProcess, style = MaterialTheme.typography.bodyLarge, lineHeight = 24.sp)
                    }

                    if (!placement.attachmentUrl.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(32.dp))
                        SectionTitle("Resources")
                        OutlinedCard(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(placement.attachmentUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.Attachment, null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Job Description PDF", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Reusable Discussion Module
                    DiscussionSection(
                        moduleType = DiscussionParentType.PLACEMENT,
                        moduleId = placementId
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Posted ${TimeUtils.getRelativeTime(placement.postedAt)} by ${placement.createdByName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun DetailInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
