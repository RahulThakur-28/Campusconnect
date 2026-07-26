package com.rahul.campusconnect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.rahul.campusconnect.domain.model.Placement
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PlacementCard(
    placement: Placement,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val statusColor = when (placement.status) {
        "Open", "Active" -> Color(0xFF16A34A)
        "Closing Soon" -> Color(0xFFF59E0B)
        "Closed" -> Color(0xFFDC2626)
        else -> Color(0xFF2563EB)
    }

    val statusBackground = statusColor.copy(alpha = 0.1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (placement.logoUrl.isNotBlank()) {
                    AsyncImage(
                        model = placement.logoUrl,
                        contentDescription = placement.companyName,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF3F4F6)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F0FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = placement.companyName.take(1).uppercase(),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = placement.companyName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = placement.jobRole,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    color = statusBackground,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = placement.status,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = placement.packageLpa,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = placement.location,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val deadlineStr = try {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(placement.deadline))
            } catch (e: Exception) {
                "TBA"
            }
            
            Text(
                text = "Apply before $deadlineStr",
                style = MaterialTheme.typography.labelSmall,
                color = if (placement.deadline < System.currentTimeMillis() && placement.deadline > 0) Color.Red else Color.Gray
            )
        }
    }
}
