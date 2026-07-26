package com.rahul.campusconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        "ACTIVE" -> if (item.type == "LOST") Color(0xFFDC2626) else Color(0xFF16A34A)
        "RESOLVED" -> Color(0xFF6B7280)
        else -> Color.Gray
    }

    val statusBackground = statusColor.copy(alpha = 0.1f)

    val categoryColor = when (item.type) {
        "LOST" -> Color(0xFFDC2626)
        else -> Color(0xFF16A34A)
    }

    val cardModifier = if (fullWidth) {
        modifier.fillMaxWidth()
    } else {
        modifier.width(260.dp)
    }

    Card(
        modifier = cardModifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column {
            if (!item.imageUrl.isNullOrEmpty()) {
                CardImageHeader(
                    imageUrl = item.imageUrl,
                    category = item.category,
                    categoryColor = categoryColor,
                    height = 140.dp
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = if (item.type == "LOST") 
                                    listOf(Color(0xFFFECACA), Color(0xFFFEE2E2)) 
                                else 
                                    listOf(Color(0xFFBBF7D0), Color(0xFFDCFCE7))
                            )
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = categoryColor
                    ) {
                        Text(
                            text = item.category,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Surface(
                        color = statusBackground,
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = item.status,
                            color = statusColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.location,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = TimeUtils.getRelativeTime(item.createdAt),
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    
                    if (fullWidth && item.status == "ACTIVE") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = onContactClick,
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.Phone, null, Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Contact", fontSize = 11.sp)
                            }
                            Button(
                                onClick = onClick,
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Details", fontSize = 11.sp)
                            }
                        }
                    } else if (fullWidth) {
                        TextButton(onClick = onClick) {
                            Text("View Details", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
