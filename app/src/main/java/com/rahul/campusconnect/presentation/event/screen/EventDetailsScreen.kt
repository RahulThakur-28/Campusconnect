package com.rahul.campusconnect.presentation.event.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.campusconnect.domain.model.Event
import com.rahul.campusconnect.presentation.event.components.RegisterButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    eventId: String,
    onBackClick: () -> Unit,
    onViewDiscussionClick: () -> Unit
) {
    // For demo, using a dummy event. In real app, fetch via ViewModel using eventId
    val event = Event(
        id = eventId,
        title = "TechQuest 2024",
        description = "Annual coding competition for all engineering students. Showcase your skills and win exciting prizes. The event will cover various programming languages and algorithmic challenges. Teams of 2-3 members are allowed. Registration is mandatory for all participants. Refreshments will be provided to all attendees.",
        category = "Academic",
        organizerName = "Coding Club",
        organizerRole = "Official Club",
        venue = "Auditorium A",
        date = "Oct 24, 2024",
        time = "10:00 AM",
        registeredCount = 245
    )

    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    RegisterButton(
                        isRegistered = false, // State from ViewModel
                        onClick = { /* TODO */ }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            // 1. Banner Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))
                        )
                    )
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }

            Column(
                modifier = Modifier
                    .offset(y = (-30).dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(24.dp)
            ) {
                // Category Tag
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        text = event.category,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Organizer
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = event.organizerName, fontWeight = FontWeight.Bold)
                        Text(text = event.organizerRole, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Info Rows
                InfoRow(icon = Icons.Default.CalendarToday, text = event.date)
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(icon = Icons.Default.Schedule, text = event.time)
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(icon = Icons.Default.LocationOn, text = event.venue)
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(icon = Icons.Default.Groups, text = "${event.registeredCount} Participants")

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "About Event", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Discussion Preview
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Questions & Answers", fontWeight = FontWeight.Bold)
                            Text(
                                text = "2 Questions",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = onViewDiscussionClick,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(text = "View Discussion →")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}
