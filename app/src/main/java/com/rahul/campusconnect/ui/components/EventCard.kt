package com.rahul.campusconnect.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
    imageHeight: Dp = 150.dp,
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

    val cardWidth = when (cardStyle) {
        EventCardStyle.Small -> 220.dp
        EventCardStyle.Medium -> 280.dp
        EventCardStyle.Large -> Dp.Unspecified
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.98f else 1f, label = "scale")

    ElevatedCard(
        modifier = modifier
            .then(if (cardWidth != Dp.Unspecified) Modifier.width(cardWidth) else Modifier.fillMaxWidth())
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        shape = RoundedCornerShape(CardConstants.CornerRadius),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isPressed) 2.dp else CardConstants.Elevation
        )
    ) {
        Column {
            // Header Image Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (cardStyle == EventCardStyle.Large) 180.dp else imageHeight)
                    .clip(RoundedCornerShape(topStart = CardConstants.CornerRadius, topEnd = CardConstants.CornerRadius))
                    .background(
                        Brush.verticalGradient(listOf(Color(0xFF6366F1), Color(0xFF4F46E5)))
                    )
            ) {
                if (!event.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = event.imageUrl,
                        contentDescription = event.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Modern Gradient Overlay
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                                )
                            )
                    )
                }

                // Category Badge
                if (showCategory) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp),
                        shape = RoundedCornerShape(100.dp),
                        color = categoryColor,
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = event.category.uppercase(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Registration Status Badge
                if (isRegistered) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                        shape = RoundedCornerShape(100.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle, 
                                null, 
                                tint = MaterialTheme.colorScheme.primary, 
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Registered", 
                                color = MaterialTheme.colorScheme.primary, 
                                fontSize = 10.sp, 
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // Content Column
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = if (cardStyle == EventCardStyle.Large) 20.sp else 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = if (cardStyle == EventCardStyle.Large) 26.sp else 22.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Date & Time Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday, 
                        null, 
                        modifier = Modifier.size(14.dp), 
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = TimeUtils.formatDate(event.startDate),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        Icons.Rounded.Schedule, 
                        null, 
                        modifier = Modifier.size(14.dp), 
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.time,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Venue Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn, 
                        null, 
                        modifier = Modifier.size(14.dp), 
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = event.venue,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (showAttendance || cardStyle == EventCardStyle.Large) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.People, 
                                null, 
                                modifier = Modifier.size(16.dp), 
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${event.registeredCount} Registered",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (cardStyle == EventCardStyle.Large) {
                            TextButton(
                                onClick = onClick,
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text("View Details", fontWeight = FontWeight.ExtraBold)
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
