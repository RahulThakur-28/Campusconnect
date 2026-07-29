package com.rahul.campusconnect.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Attachment
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.campusconnect.common.utils.TimeUtils
import com.rahul.campusconnect.domain.model.Announcement

@Composable
fun AnnouncementCard(
    announcement: Announcement,
    modifier: Modifier = Modifier,
    cardWidth: Dp = CardConstants.HomeCardWidth,
    onCardClick: () -> Unit = {},
    onReadMoreClick: () -> Unit = onCardClick
){
    val hasImage = !announcement.imageUrl.isNullOrEmpty()

    ElevatedCard(
        onClick = onCardClick,
        modifier =
            if (cardWidth == Dp.Unspecified) {
                modifier.fillMaxWidth()
            } else {
                modifier.width(cardWidth)
            }
                .height(380.dp),
        shape = RoundedCornerShape(CardConstants.CornerRadius),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = CardConstants.Elevation
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ){
        Column(modifier = Modifier.fillMaxSize()) {
            if (hasImage) {
                // banner image 16:9 ratio
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 7f) // Proportional to other cards
                        .clip(RoundedCornerShape(topStart = CardConstants.CornerRadius, topEnd = CardConstants.CornerRadius))
                ) {
                    CardImageHeader(
                        imageUrl = announcement.imageUrl,
                        category = announcement.category,
                        categoryColor = MaterialTheme.colorScheme.primary,
                        height = Dp.Unspecified 
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(20.dp) // Consistent horizontal padding
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (!hasImage) {
                    // Category Chip at the top when no image
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Text(
                            text = announcement.category.uppercase(),
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Title - Max 2 lines
                Text(
                    text = announcement.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description - Max 2 lines
                Text(
                    text = announcement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                // Posted By & Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = announcement.postedByName.ifBlank { "Anonymous" },
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (announcement.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Rounded.Verified,
                                    contentDescription = "Verified",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Text(
                            text = announcement.postedByRole,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = TimeUtils.getRelativeTime(announcement.postedAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 20.dp),
                thickness = 0.5.dp, 
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Read More Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(
                    onClick = onReadMoreClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Read More",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AnnouncementLoadingShimmer(
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
            ShimmerAnnouncementCard(brush = brush)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ShimmerAnnouncementCard(brush: Brush) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(CardConstants.CornerRadius),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = CardConstants.Elevation),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 5.5f)
                    .background(brush)
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(20.dp)
                        .background(brush, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .background(brush, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(14.dp)
                        .background(brush, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.weight(1f))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .height(30.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(14.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}
