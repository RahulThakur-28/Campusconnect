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
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
    val snackbarHostState = remember { SnackbarHostState() }

    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(placementId) {
        viewModel.loadPlacement(placementId)
    }

    LaunchedEffect(uiState.deleted) {
        if (uiState.deleted) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
            navController.previousBackStackEntry?.savedStateHandle?.set("snackbar_message", "Placement Deleted Successfully")
            onBackClick()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Placement", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to PERMANENTLY delete this placement drive? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deletePlacement()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", fontWeight = FontWeight.ExtraBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        snackbarHost = { 
            SnackbarHost(snackbarHostState) { data ->
                val isDelete = data.visuals.message.contains("Deleted", ignoreCase = true)
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isDelete) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.inverseSurface,
                    contentColor = if (isDelete) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.inverseOnSurface,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Drive Details", fontWeight = FontWeight.ExtraBold) },
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
                        Icon(Icons.Rounded.Share, contentDescription = "Share")
                    }

                    if (uiState.canEdit) {
                        IconButton(onClick = { onEditClick(placementId) }) {
                            Icon(Icons.Rounded.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Rounded.DeleteForever, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        bottomBar = {
            uiState.placement?.let { placement ->
                Surface(
                    tonalElevation = 8.dp, 
                    shadowElevation = 16.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Box(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                        PrimaryButton(
                            text = when {
                                uiState.isExpired -> "Application Closed"
                                else -> "Apply Now"
                            },
                            enabled = uiState.canApply,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(placement.applyLink))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.isLoading && uiState.placement == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(strokeWidth = 3.dp)
                    }
                }
                uiState.placement != null -> {
                    val placement = uiState.placement!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .verticalScroll(scrollState)
                    ) {
                        // Header Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                modifier = Modifier.size(100.dp),
                                shape = RoundedCornerShape(24.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                shadowElevation = 4.dp
                            ) {
                                if (placement.logoUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = placement.logoUrl,
                                        contentDescription = placement.companyName,
                                        modifier = Modifier.fillMaxSize().padding(16.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = placement.companyName.take(1).uppercase(),
                                            style = MaterialTheme.typography.displayMedium,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Text(
                                text = placement.companyName,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            
                            Text(
                                text = placement.jobRole,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            StatusPill(
                                text = if (uiState.isExpired) "Expired" else placement.status,
                                containerColor = if (uiState.isExpired) MaterialTheme.colorScheme.error else Color(0xFF10B981),
                                contentColor = Color.White
                            )
                        }

                        // Info Grid
                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                InfoCard(
                                    icon = Icons.Rounded.CurrencyRupee,
                                    label = "Package",
                                    value = placement.packageLpa,
                                    modifier = Modifier.weight(1f),
                                    contentColor = Color(0xFF10B981)
                                )
                                InfoCard(
                                    icon = Icons.Rounded.LocationOn,
                                    label = "Location",
                                    value = placement.location,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                InfoCard(
                                    icon = Icons.Rounded.Work,
                                    label = "Job Type",
                                    value = placement.jobType,
                                    modifier = Modifier.weight(1f)
                                )
                                InfoCard(
                                    icon = Icons.Rounded.Group,
                                    label = "Openings",
                                    value = placement.openings.toString(),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                            SectionHeader(title = "Eligibility", actionText = null)
                            Text(
                                text = placement.eligibility,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            SectionHeader(title = "Required Skills", actionText = null)
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                placement.requiredSkills.forEach { skill ->
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(skill, fontWeight = FontWeight.Bold) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        border = null
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            SectionHeader(title = "Description", actionText = null)
                            Text(
                                text = placement.description,
                                style = MaterialTheme.typography.bodyLarge,
                                lineHeight = 26.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (placement.applicationProcess.isNotBlank()) {
                                Spacer(modifier = Modifier.height(32.dp))
                                SectionHeader(title = "Application Process", actionText = null)
                                Text(
                                    text = placement.applicationProcess,
                                    style = MaterialTheme.typography.bodyLarge,
                                    lineHeight = 26.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (!placement.attachmentUrl.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(32.dp))
                                SectionHeader(title = "Resources", actionText = null)
                                Surface(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(placement.attachmentUrl))
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Rounded.Attachment, null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Column {
                                            Text("Job Description PDF", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            Text("Tap to view or download", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(40.dp))

                            // Reusable Discussion Module
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(24.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                            ) {
                                DiscussionSection(
                                    moduleType = DiscussionParentType.PLACEMENT,
                                    moduleId = placementId
                                )
                            }

                            Spacer(modifier = Modifier.height(40.dp))
                            
                            HorizontalDivider(modifier = Modifier.alpha(0.3f))
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Rounded.Person, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Posted by ${placement.createdByName}",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = TimeUtils.getRelativeTime(placement.postedAt),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(48.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label, 
                style = MaterialTheme.typography.labelSmall, 
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value, 
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
