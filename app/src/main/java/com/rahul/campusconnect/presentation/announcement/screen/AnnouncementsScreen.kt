package com.rahul.campusconnect.presentation.announcement.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.campusconnect.domain.model.UserRole
import com.rahul.campusconnect.presentation.announcement.viewmodel.AnnouncementViewModel
import com.rahul.campusconnect.presentation.event.components.CategoryChip
import com.rahul.campusconnect.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(
    onAnnouncementClick: (String) -> Unit,
    onCreateAnnouncementClick: () -> Unit,
    viewModel: AnnouncementViewModel = hiltViewModel()
) {


    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Announcements", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            if (uiState.userRole == UserRole.VERIFIED_TEACHER || uiState.userRole == UserRole.ADMIN) {
                FloatingActionButton(
                    onClick = onCreateAnnouncementClick,
                    containerColor = Color(0xFF2563EB),
                    contentColor = Color.White
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Create Announcement")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            SearchTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                placeholder = "Search announcements...",
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Category Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(uiState.categories) { category ->
                    CategoryChip(
                        category = category,
                        isSelected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF2563EB))
                }
            } else if (uiState.filteredAnnouncements.isEmpty()) {
                EmptyState(message = "No announcements found.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.filteredAnnouncements) { announcement ->
                        AnnouncementCard(
                            announcement = announcement,
                            onCardClick = {
                                onAnnouncementClick(announcement.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
