package com.rahul.campusconnect.presentation.notification.components

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ManageSearch
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.campusconnect.domain.model.Notification
import com.rahul.campusconnect.domain.model.NotificationType

@Composable
fun NotificationItem(
    notification: Notification,
    onNotificationClick: (Notification) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUnread = !notification.isRead
    
    val containerColor = if (isUnread) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val (icon, iconColor) = when (notification.type) {
        NotificationType.ANNOUNCEMENT -> Icons.Rounded.Campaign to Color(0xFF2563EB)
        NotificationType.EVENT -> Icons.Rounded.EventAvailable to Color(0xFF7C3AED)
        NotificationType.PLACEMENT -> Icons.Rounded.WorkOutline to Color(0xFF059669)
        NotificationType.VERIFICATION_APPROVED -> Icons.Rounded.VerifiedUser to Color(0xFF10B981)
        NotificationType.VERIFICATION_REJECTED -> Icons.Rounded.GppBad to Color(0xFFEF4444)
        NotificationType.DISCUSSION_REPLY -> Icons.Rounded.ChatBubbleOutline to Color(0xFF2563EB)
        NotificationType.LOST_FOUND -> Icons.AutoMirrored.Rounded.ManageSearch to Color(0xFFF59E0B)
        else -> Icons.Rounded.NotificationsActive to Color(0xFF6B7280)
    }

    Card(
        onClick = { onNotificationClick(notification) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
        border = if (isUnread) {
            androidx.compose.foundation.BorderStroke(
                1.dp, 
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon with background
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = iconColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isUnread) FontWeight.ExtraBold else FontWeight.Bold,
                            fontSize = 15.sp,
                            lineHeight = 20.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = if (isUnread) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.weight(1f)
                    )

                    if (isUnread) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp, start = 8.dp)
                                .size(8.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 18.sp,
                        letterSpacing = 0.2.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = DateUtils.getRelativeTimeSpanString(notification.createdAt).toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
