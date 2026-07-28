package com.rahul.campusconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rahul.campusconnect.common.utils.TimeUtils
import com.rahul.campusconnect.domain.model.LostFoundItem

@Composable
fun LostFoundCard(
    item: LostFoundItem,
    modifier: Modifier = Modifier,
    fullWidth: Boolean = false,
    onClick: () -> Unit = {},
    onContactClick: () -> Unit = {}
) {
    val statusColor = when (item.status) {
        "ACTIVE" -> if (item.type == "LOST") Color(0xFFEF4444) else Color(0xFF10B981)
        "RESOLVED" -> Color(0xFF64748B)
        else -> MaterialTheme.colorScheme.outline
    }

    val typeLabel = if (item.type == "LOST") "LOST" else "FOUND"
    val categoryColor = if (item.type == "LOST") Color(0xFFEF4444) else Color(0xFF10B981)

    val cardModifier = if (fullWidth) {
        modifier.fillMaxWidth()
    } else {
        modifier.width(280.dp)
    }

    ElevatedCard(
        modifier = cardModifier.clickable { onClick() },
        shape = RoundedCornerShape(CardConstants.CornerRadius),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = CardConstants.Elevation)
    ) {
        Column {
            if (!item.imageUrl.isNullOrEmpty()) {
                CardImageHeader(
                    imageUrl = item.imageUrl,
                    category = typeLabel,
                    categoryColor = categoryColor,
                    height = 140.dp
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = if (item.type == "LOST") 
                                    listOf(Color(0xFFFECACA).copy(alpha = 0.5f), Color(0xFFFEE2E2).copy(alpha = 0.5f)) 
                                else 
                                    listOf(Color(0xFFBBF7D0).copy(alpha = 0.5f), Color(0xFFDCFCE7).copy(alpha = 0.5f))
                            )
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    StatusPill(
                        text = typeLabel,
                        containerColor = categoryColor,
                        contentColor = Color.White
                    )
                }
            }

            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = item.status,
                            color = statusColor,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.location,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(modifier = Modifier.background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = TimeUtils.getRelativeTime(item.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    
                    if (fullWidth && item.status == "ACTIVE") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = onContactClick,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Icon(Icons.Default.Phone, null, Modifier.size(14.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Contact", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = onClick,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(34.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Text("Details", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        TextButton(
                            onClick = onClick,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("View Details", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}
