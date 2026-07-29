package com.rahul.campusconnect.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import com.rahul.campusconnect.domain.model.Note
import java.util.Locale

@Composable
fun NoteCard(
    note: Note,
    modifier: Modifier = Modifier,
    fillMaxWidth: Boolean = false,
    onClick: () -> Unit = {},
    onViewNotes: () -> Unit = {}
) {
    val downloadsText = formatDownloads(note.downloadCount)
    val hasImage = !note.thumbnailUrl.isNullOrEmpty()

    val cardModifier =
        if (fillMaxWidth) {
            modifier.fillMaxWidth()
        } else {
            modifier.width(CardConstants.HomeCardWidth)
        }

    ElevatedCard(
        onClick = onClick,
        modifier = cardModifier.height(380.dp),
        shape = RoundedCornerShape(CardConstants.CornerRadius),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = CardConstants.Elevation
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (hasImage) {
                // 16:9 Aspect Ratio Hero Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 7f) // Consistent banner ratio
                        .clip(RoundedCornerShape(topStart = CardConstants.CornerRadius, topEnd = CardConstants.CornerRadius))
                ) {
                    CardImageHeader(
                        imageUrl = note.thumbnailUrl,
                        category = note.subject,
                        categoryColor = MaterialTheme.colorScheme.secondary,
                        height = Dp.Unspecified
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(20.dp) // Consistent horizontal padding
                    .weight(1f)
            ) {
                if (!hasImage) {
                    // Subject Chip at top if no image
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = note.subject.uppercase(),
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Title - Max 2 lines
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Short Description - Max 2 lines
                Text(
                    text = note.description.ifBlank { "Study materials for ${note.subject}." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Metadata Row: Dept • Sem • Time
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Description,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${note.branch} • Sem ${note.semester} • ${TimeUtils.getRelativeTime(note.createdAt)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Posted By Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = note.uploadedByName.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 14.sp
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = note.uploadedByName.ifBlank { "Anonymous" },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        val roleText = when(note.uploadedByRole) {
                            "ADMIN" -> "Admin"
                            "VERIFIED_TEACHER" -> "Teacher"
                            "PLACEMENT_CELL" -> "Placement"
                            else -> "Student"
                        }
                        val roleColor = when(note.uploadedByRole) {
                            "ADMIN" -> Color(0xFF0369A1)
                            "VERIFIED_TEACHER" -> Color(0xFF15803D)
                            "PLACEMENT_CELL" -> Color(0xFFC2410C)
                            else -> Color(0xFF7E22CE)
                        }

                        Text(
                            text = roleText,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = roleColor
                        )
                    }
                }
            }

            // Bottom Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 8.dp,
                        bottom = 20.dp
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⬇ $downloadsText Downloads",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }

                FilledTonalButton(
                    onClick = onViewNotes,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(
                        text = "Download Notes",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NoteLoadingShimmer(
    modifier: Modifier = Modifier
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Column(modifier = modifier.padding(vertical = 16.dp)) {
        repeat(3) {
            ShimmerNoteCard(brush = brush)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ShimmerNoteCard(brush: Brush) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(CardConstants.CornerRadius),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = CardConstants.Elevation),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(brush)
            )
            Column(modifier = Modifier.padding(20.dp)) {
                Box(modifier = Modifier.fillMaxWidth(0.7f).height(24.dp).background(brush, RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().height(16.dp).background(brush, RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(0.9f).height(16.dp).background(brush, RoundedCornerShape(4.dp)))
                
                Spacer(modifier = Modifier.weight(1f))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.width(120.dp).height(32.dp).background(brush, RoundedCornerShape(8.dp)))
                    Box(modifier = Modifier.width(60.dp).height(16.dp).background(brush, RoundedCornerShape(4.dp)))
                }
            }
        }
    }
}

private fun formatDownloads(downloads: Int): String {
    return when {
        downloads >= 1_000_000 ->
            String.format(Locale.US, "%.1fM", downloads / 1_000_000f)
        downloads >= 1000 ->
            String.format(Locale.US, "%.1fK", downloads / 1000f)
        else ->
            "$downloads"
    }
}
