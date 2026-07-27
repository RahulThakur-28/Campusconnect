package com.rahul.campusconnect.presentation.discussion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rahul.campusconnect.common.utils.TimeUtils
import com.rahul.campusconnect.domain.model.Discussion
import com.rahul.campusconnect.domain.model.UserRole

@Composable
fun QuestionCard(
    discussion: Discussion,
    currentUserId: String,
    onLikeClick: () -> Unit,
    onReplyClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onReportClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = {},
    currentUserRole: String = ""
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: User Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    if (discussion.createdByPhoto.isNotBlank()) {
                        AsyncImage(
                            model = discussion.createdByPhoto,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = discussion.createdByName.take(1), fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = discussion.createdByName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (discussion.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, null, modifier = Modifier.size(14.dp), tint = Color(0xFF2563EB))
                        }
                    }
                    Text(
                        text = "${discussion.createdByRole.displayName} • ${TimeUtils.getRelativeTime(discussion.createdAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, null, tint = Color.Gray)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        val canDelete = discussion.createdBy == currentUserId || 
                                       currentUserRole == UserRole.ADMIN.name || 
                                       currentUserRole == UserRole.SUPER_ADMIN.name
                        
                        if (discussion.createdBy == currentUserId) {
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
                        
                        if (discussion.createdBy != currentUserId) {
                            DropdownMenuItem(
                                text = { Text("Report") },
                                onClick = { onReportClick("Inappropriate content"); showMenu = false },
                                leadingIcon = { Icon(Icons.Default.Report, null) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title & Question
            Text(
                text = discussion.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = discussion.question,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Footer: Likes & Replies
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isLiked = discussion.likedBy.contains(currentUserId)
                    IconButton(onClick = onLikeClick, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.ThumbUp,
                            contentDescription = "Like",
                            modifier = Modifier.size(20.dp),
                            tint = if (isLiked) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    Text(
                        text = discussion.likeCount.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(start = 4.dp),
                        color = if (isLiked) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }

                TextButton(
                    onClick = onReplyClick,
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.AutoMirrored.Outlined.Chat, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "${discussion.replyCount} Replies", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
