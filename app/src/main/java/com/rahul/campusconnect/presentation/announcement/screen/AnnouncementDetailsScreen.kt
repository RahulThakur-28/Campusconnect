package com.rahul.campusconnect.presentation.announcement.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Attachment
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.campusconnect.domain.model.Announcement
import com.rahul.campusconnect.ui.components.CardImageHeader
import com.rahul.campusconnect.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementDetailsScreen(
    announcementId: String,
    onBackClick: () -> Unit
) {
    // Dummy Data - In real app, fetch via ViewModel
    val announcement = Announcement(
        id = announcementId,
        title = "End Semester Examination Schedule Out",
        description = "The end semester examinations for all departments will commence from 15th December. Students are advised to check the detailed schedule on the official notice board or download the attached PDF.\n\nAll students must carry their ID cards and hall tickets. Electronic gadgets are strictly prohibited in the examination hall. Please reach the venue 30 minutes before the scheduled time.\n\nGood luck to everyone!",
        category = "Exam",
        postedBy = "Admin",
        isVerified = true,
        timestamp = "2 hours ago",
        hasAttachment = true,
        imageUrl = null
    )

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Announcement Details") },
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
        ) {
            // Optional Banner Image
            if (announcement.imageUrl != null) {
                CardImageHeader(
                    imageUrl = announcement.imageUrl,
                    category = announcement.category,
                    categoryColor = Color(0xFF2563EB),
                    height = 240.dp
                )
            }

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                if (announcement.imageUrl == null) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFFEFF4FF),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = announcement.category,
                            color = Color(0xFF2563EB),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Posted by ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = announcement.postedBy,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (announcement.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Rounded.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = announcement.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = announcement.description,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (announcement.hasAttachment) {
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Attachment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Exam_Schedule_Dec_2024.pdf",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "1.2 MB",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                            IconButton(onClick = { /* TODO: Download */ }) {
                                Icon(
                                    imageVector = Icons.Rounded.Download,
                                    contentDescription = "Download",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                PrimaryButton(
                    text = "Back to Announcements",
                    onClick = onBackClick
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
