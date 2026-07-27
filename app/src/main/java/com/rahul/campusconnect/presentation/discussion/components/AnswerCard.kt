package com.rahul.campusconnect.presentation.discussion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rahul.campusconnect.common.utils.TimeUtils
import com.rahul.campusconnect.domain.model.Reply
import com.rahul.campusconnect.domain.model.UserRole

@Composable
fun AnswerCard(
    reply: Reply,
    currentUserId: String,
    onLikeClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReportClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    currentUserRole: String = ""
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (reply.isOfficial) Color(0xFFF0FDF4) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = if (reply.isOfficial) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = if (reply.isOfficial) Color(0xFF22C55E) else MaterialTheme.colorScheme.primaryContainer
                ) {
                    if (reply.createdByPhoto.isNotBlank()) {
                        AsyncImage(
                            model = reply.createdByPhoto,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = reply.createdByName.take(1), 
                                fontWeight = FontWeight.Bold,
                                color = if (reply.isOfficial) Color.White else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = reply.createdByName,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (reply.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, null, modifier = Modifier.size(12.dp), tint = Color(0xFF2563EB))
                        }
                        if (reply.isOfficial) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF22C55E).copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "OFFICIAL",
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF166534),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                    Text(
                        text = "${reply.createdByRole.displayName} • ${TimeUtils.getRelativeTime(reply.createdAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.MoreVert, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        val canDelete = reply.createdBy == currentUserId || 
                                       currentUserRole == UserRole.ADMIN.name || 
                                       currentUserRole == UserRole.SUPER_ADMIN.name
                        
                        if (reply.createdBy == currentUserId) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = { onEditClick(); showMenu = false },
                                leadingIcon = { Icon(Icons.Default.Edit, null) }
                            )
                        }
                        
                        if (canDelete) {
                            DropdownMenuItem(
                                text = { Text("Delete", color = Color.Red) },
                                onClick = { onDeleteClick(); showMenu = false },
                                leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                            )
                        }
                        
                        if (reply.createdBy != currentUserId) {
                            DropdownMenuItem(
                                text = { Text("Report") },
                                onClick = { onReportClick("Spam"); showMenu = false },
                                leadingIcon = { Icon(Icons.Default.Report, null) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = reply.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                val isLiked = reply.likedBy.contains(currentUserId)
                IconButton(onClick = onLikeClick, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.ThumbUp,
                        contentDescription = "Like",
                        modifier = Modifier.size(18.dp),
                        tint = if (isLiked) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
                Text(
                    text = reply.likeCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp),
                    color = if (isLiked) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}
