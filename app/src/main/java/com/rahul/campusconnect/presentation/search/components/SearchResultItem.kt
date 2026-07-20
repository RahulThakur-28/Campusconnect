package com.rahul.campusconnect.presentation.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.domain.model.SearchResult
import com.rahul.campusconnect.domain.model.SearchResultType

@Composable
fun SearchResultItem(
    result: SearchResult,
    onClick: (SearchResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, iconColor) = when (result.type) {
        SearchResultType.ANNOUNCEMENT -> Icons.Default.Campaign to Color(0xFF2563EB)
        SearchResultType.EVENT -> Icons.Default.Event to Color(0xFF7C3AED)
        SearchResultType.PLACEMENT -> Icons.Default.Work to Color(0xFF10B981)
        SearchResultType.NOTE -> Icons.Default.Description to Color(0xFFF59E0B)
        SearchResultType.LOST_FOUND -> Icons.Default.Search to Color(0xFFEF4444)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(result) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = iconColor
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = result.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = result.type.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = iconColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " • ${result.subtitle}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}
