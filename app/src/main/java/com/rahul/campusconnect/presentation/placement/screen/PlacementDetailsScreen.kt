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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.campusconnect.model.Placement
import com.rahul.campusconnect.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementDetailsScreen(
    placementId: String,
    onBackClick: () -> Unit,
    onViewDiscussionClick: () -> Unit
) {
    val context = LocalContext.current
    // Dummy Data - fetch by ID in real app
    val placement = Placement(
        id = placementId,
        companyName = "Google",
        role = "Software Engineer",
        packageAmount = "45 LPA",
        location = "Bangalore",
        jobType = "Full-time",
        openings = 10,
        deadline = "30 Oct 2024",
        applyLink = "https://google.com/careers",
        eligibility = "7.5 CGPA+",
        description = "Join Google's dynamic engineering team to build scalable systems. You will work on cutting-edge technologies and collaborate with brilliant minds across the globe.",
        requiredSkills = listOf("Kotlin", "Java", "System Design", "Algorithms"),
        applicationProcess = "1. Online Coding Round\n2. 3x Technical Interviews\n3. Leadership (Googlyness) Round"
    )

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Placement Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                    text = "Apply Now",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(placement.applyLink))
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
                        text = placement.role,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info Grid
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoItem(Icons.Default.CurrencyRupee, "Package", placement.packageAmount, Modifier.weight(1f))
                InfoItem(Icons.Default.LocationOn, "Location", placement.location, Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                InfoItem(Icons.Default.Work, "Job Type", placement.jobType, Modifier.weight(1f))
                InfoItem(Icons.Default.Group, "Openings", placement.openings.toString(), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            DetailSection("Eligibility", placement.eligibility)
            DetailSection("Deadline", placement.deadline)
            
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
                        Text("Have doubts? Ask the placement cell.", style = MaterialTheme.typography.bodySmall)
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

@Composable
fun DetailSection(title: String, content: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(content, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
