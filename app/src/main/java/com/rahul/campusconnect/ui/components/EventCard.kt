package com.rahul.campusconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rahul.campusconnect.common.utils.TimeUtils
import com.rahul.campusconnect.domain.model.Event

enum class EventCardStyle {
    Small, Medium, Large
}

@Composable
fun EventCard(
    event: Event,
    modifier: Modifier = Modifier,
    imageHeight: Dp = 135.dp,
    showAttendance: Boolean = true,
    showCategory: Boolean = true,
    showRegisterButton: Boolean = false,
    cardStyle: EventCardStyle = EventCardStyle.Medium,
    isRegistered: Boolean = false,
    onClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val categoryColor = when (event.category) {
        "Academic" -> Color(0xFF2563EB)
        "Workshop" -> Color(0xFF7C3AED)
        "Cultural" -> Color(0xFFF59E0B)
        "Sports" -> Color(0xFF10B981)
        "Placement" -> Color(0xFF3B82F6)
        else -> MaterialTheme.colorScheme.primary
    }

    val cardModifier = if (cardStyle == EventCardStyle.Large) {
        modifier.fillMaxWidth()
    } else {
        val width = when (cardStyle) {
            EventCardStyle.Small -> 200.dp
            else -> 260.dp
        }
        modifier.width(width)
    }

    Card(
        modifier = cardModifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)))
                    )
            ) {
                if (!event.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = event.imageUrl,
                        contentDescription = event.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f))
                                )
                            )
                    )
                }

                if (showCategory) {
                    Surface(
                        modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
                        shape = RoundedCornerShape(50.dp),
                        color = categoryColor
                    ) {
                        Text(
                            text = event.category,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                if (isRegistered) {
                    Surface(
                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0xFFE8F5E9).copy(alpha = 0.9f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Registered", color = Color(0xFF2E7D32), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = event.title,
                    fontSize = if (cardStyle == EventCardStyle.Large) 18.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${TimeUtils.formatDate(event.startDate)} • ${event.time}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.venue,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (showAttendance) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.People, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${event.registeredCount} Registered",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
