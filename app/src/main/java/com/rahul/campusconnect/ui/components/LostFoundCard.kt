package com.rahul.campusconnect.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.campusconnect.common.utils.TimeUtils
import com.rahul.campusconnect.domain.model.LostFoundItem

/**
 * PREMIUM REDESIGNED LOST & FOUND CARD
 * Optimized for Material 3 design language with high information density.
 */
@Composable
fun LostFoundCard(
    item: LostFoundItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val hasImage = !item.imageUrl.isNullOrEmpty()

    val semanticColor = when {
        item.status == "RESOLVED" -> Color(0xFF10B981) // Green
        item.type == "LOST" -> Color(0xFFEF4444) // Red
        else -> Color(0xFF3B82F6) // Blue
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        label = "scale"
    )

    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(if (hasImage) 380.dp else 260.dp) // Increased height (approx +20dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(scale),
        shape = RoundedCornerShape(CardConstants.CornerRadius),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 1.dp
        ),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        interactionSource = interactionSource
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // HERO IMAGE SECTION (Collapsible)
            if (hasImage) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 8.5f) // Adjusted ratio to allow more space for content
                        .clip(
                            RoundedCornerShape(
                                topStart = CardConstants.CornerRadius,
                                topEnd = CardConstants.CornerRadius
                            )
                        )
                ) {
                    CardImageHeader(
                        imageUrl = item.imageUrl,
                        category = item.type,
                        categoryColor = semanticColor,
                        height = Dp.Unspecified
                    )
                }
            }

            // CONTENT SECTION
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .weight(1f)
            ) {
                // STATUS & TIME ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Type Badge (M3 Style)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = semanticColor.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            semanticColor.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = item.type,
                            color = semanticColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = TimeUtils.getRelativeTime(item.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // TITLE - Max 2 lines
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        lineHeight = 22.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                // DESCRIPTION - Max 2 lines
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // LOCATION
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.location,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // REPORTER & ACTIONS ROW
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // LEFT: REPORTER & CALL ACTION
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Quick Call Action (Circular)
                        if (!item.contactPhone.isNullOrBlank()) {
                            Surface(
                                onClick = {
                                    val intent = Intent(
                                        Intent.ACTION_DIAL,
                                        Uri.parse("tel:${item.contactPhone}")
                                    )
                                    context.startActivity(intent)
                                },
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.Call,
                                        contentDescription = "Call Owner",
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        // Author Info
                        Column {
                            Text(
                                text = item.ownerName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = item.ownerRole.ifBlank { "Verified Student" },
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // RIGHT: PRIMARY ACTION
                    FilledTonalButton(
                        onClick = onClick,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Details",
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.width(4.dp))

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
}

    /**
     * SHIMMER STATES FOR REDESIGNED CARD
     */
    @Composable
    fun LostFoundLoadingShimmer(
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
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer"
        )

        val brush = Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnim.value, y = translateAnim.value)
        )

        Column(modifier = modifier.padding(vertical = 8.dp)) {
            repeat(3) {
                ShimmerLostFoundCard(brush = brush)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    @Composable
    fun ShimmerLostFoundCard(brush: Brush) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(CardConstants.CornerRadius),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(brush)
                )
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier.size(60.dp, 20.dp)
                                .background(brush, RoundedCornerShape(4.dp))
                        )
                        Box(
                            modifier = Modifier.size(40.dp, 14.dp)
                                .background(brush, RoundedCornerShape(4.dp))
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(0.7f).height(22.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth().height(16.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(0.9f).height(16.dp)
                            .background(brush, RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(36.dp).background(brush, CircleShape))
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier.size(80.dp, 14.dp)
                                    .background(brush, RoundedCornerShape(4.dp))
                            )
                        }
                        Box(
                            modifier = Modifier.size(100.dp, 36.dp)
                                .background(brush, RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
        }
    }
