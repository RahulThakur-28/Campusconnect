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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.presentation.placement.viewmodel.PlacementDetailsViewModel
import com.rahul.campusconnect.ui.components.EmptyState
import com.rahul.campusconnect.ui.components.PrimaryButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementDetailsScreen(
    placementId: String,
    onBackClick: () -> Unit,
    onViewDiscussionClick: () -> Unit,
    onEditClick: (String) -> Unit,
    viewModel: PlacementDetailsViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(placementId) {
        viewModel.loadPlacement(placementId)
    }



    val placement = uiState.placement

    val scrollState = rememberScrollState()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.error != null) {
        EmptyState(
            message = uiState.error.orEmpty(),
            buttonText = "Retry",
            onButtonClick = viewModel::refresh
        )
        return
    }

    if (placement == null) {
        EmptyState(
            message = "Placement not found"
        )
        return
    }



    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Placement Details")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {

                    // Share Placement
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    buildString {
                                        append("🏢 ${placement.companyName}\n")
                                        append("💼 ${placement.jobRole}\n")
                                        append("💰 ${placement.packageLpa}\n")
                                        append("📍 ${placement.location}\n\n")
                                        append("Apply Here:\n${placement.applyLink}")
                                    }
                                )
                            }

                            context.startActivity(
                                Intent.createChooser(
                                    shareIntent,
                                    "Share Placement"
                                )
                            )
                        }
                    ) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }

                    // Edit Placement
                    if (uiState.canEdit) {
                        IconButton(
                            onClick = {
                                onEditClick(placement.id)
                            }
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Placement"
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                PrimaryButton(
                    text = when {

                        placement.isDeleted ->
                            "Placement Removed"

                        uiState.isExpired ->
                            "Application Closed"

                        else ->
                            "Apply Now"
                    },

                    enabled = uiState.canApply,

                    onClick = {

                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(placement.applyLink)
                        )

                        context.startActivity(intent)
                    },

                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            // Header Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        text = placement.companyName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = placement.jobRole,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                PlacementStatusChip(
                    placement = placement,
                    isExpired = uiState.isExpired
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info Grid
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoItem(Icons.Default.CurrencyRupee, "Package", placement.packageLpa, Modifier.weight(1f))
                InfoItem(Icons.Default.LocationOn, "Location", placement.location, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoItem(Icons.Default.Work, "Job Type", placement.jobType, Modifier.weight(1f))
                InfoItem(Icons.Default.Group, "Openings", placement.openings.toString(), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            DetailSection("Eligibility", placement.eligibility)
            DetailSection("Deadline", formatDate(placement.deadline))


            if (uiState.isExpired) {

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Applications for this placement have closed.",
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }



            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Required Skills", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                placement.requiredSkills.forEach { skill ->
                    SuggestionChip(
                        onClick = {},
                        label = { Text(skill) },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            DetailSection("Description", placement.description)
            DetailSection("Application Process", placement.applicationProcess)

            Spacer(modifier = Modifier.height(32.dp))

            // Q&A Entry
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = onViewDiscussionClick,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Questions & Answers", fontWeight = FontWeight.Bold)
                        Text(
                                "Ask questions, view official replies, and discuss with verified students.",
                        style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Icon(Icons.Default.QuestionAnswer, contentDescription = null)
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun InfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

fun formatDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

@Composable
fun DetailSection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(content, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


@Composable
private fun PlacementStatusChip(
    placement: Placement,
    isExpired: Boolean
) {

    val (text, color) = when {

        placement.isDeleted ->
            "Deleted" to MaterialTheme.colorScheme.error

        isExpired ->
            "Expired" to Color(0xFFD32F2F)

        placement.status.equals("Active", true) ->
            "Active" to Color(0xFF2E7D32)

        else ->
            "Closed" to Color(0xFFF57C00)
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = text,
            color = color,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        content = { content() }
    )
}
