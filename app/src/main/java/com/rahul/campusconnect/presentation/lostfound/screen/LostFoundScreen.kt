package com.rahul.campusconnect.presentation.lostfound.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.campusconnect.presentation.lostfound.state.LostFoundTab
import com.rahul.campusconnect.presentation.lostfound.viewmodel.LostFoundViewModel
import com.rahul.campusconnect.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostFoundScreen(
    onItemClick: (String) -> Unit,
    onReportClick: () -> Unit,
    viewModel: LostFoundViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lost & Found", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = onReportClick) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Report Items")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            SearchBar(
                hint = "Search lost or found items...",
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Segmented Control / Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusFilterChip(
                    text = "Lost Items",
                    selected = uiState.selectedTab == LostFoundTab.LOST,
                    onClick = { viewModel.onTabSelected(LostFoundTab.LOST) },
                    selectedColor = Color(0xFFDC2626),
                    modifier = Modifier.weight(1f)
                )
                StatusFilterChip(
                    text = "Found Items",
                    selected = uiState.selectedTab == LostFoundTab.FOUND,
                    onClick = { viewModel.onTabSelected(LostFoundTab.FOUND) },
                    selectedColor = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredItems.isEmpty()) {
                EmptyState(message = "No items found in this category.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.filteredItems) { item ->
                        LostFoundCard(
                            item = item,
                            fullWidth = true,
                            onClick = { onItemClick(item.id) },
                            onContactClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.contact}"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (selected) selectedColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (selected) selectedColor else Color.Gray, RoundedCornerShape(50.dp))
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
