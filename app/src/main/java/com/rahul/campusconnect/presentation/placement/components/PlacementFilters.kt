package com.rahul.campusconnect.presentation.placement.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.presentation.event.components.CategoryChip
import com.rahul.campusconnect.presentation.placement.state.PlacementsUiState
import com.rahul.campusconnect.presentation.placement.viewmodel.PlacementsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacementFilters(
    uiState: PlacementsUiState,
    viewModel: PlacementsViewModel,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }

    // Single Horizontal Filter Row
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Sort Button
        item {
            Box {
                AssistChip(
                    onClick = { showSortMenu = true },
                    label = { 
                        Text(
                            text = uiState.selectedSort,
                            style = MaterialTheme.typography.labelLarge
                        ) 
                    },
                    leadingIcon = { Icon(Icons.Default.Sort, null, Modifier.size(18.dp)) },
                    shape = MaterialTheme.shapes.medium,
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = null
                )
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    uiState.sortOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.setFilters(sort = option)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }
        }

        // 2. Categories (All, IT, Finance, etc.)
        items(uiState.categories) { category ->
            CategoryChip(
                category = category,
                isSelected = uiState.selectedCategory == category,
                onClick = { viewModel.setFilters(category = category) }
            )
        }

        // 3. Job Types (Full-time, Internship)
        // Note: FilterChip would normally be used here, but we use CategoryChip for consistent design language
        items(uiState.jobTypes.filter { it != "All" }) { type ->
            CategoryChip(
                category = type,
                isSelected = uiState.selectedJobType == type,
                onClick = { 
                    val newType = if (uiState.selectedJobType == type) "All" else type
                    viewModel.setFilters(jobType = newType) 
                }
            )
        }

        // 4. Locations (Remote, On Campus, etc.)
        items(uiState.locations.filter { it != "All" }) { loc ->
            CategoryChip(
                category = loc,
                isSelected = uiState.selectedLocation == loc,
                onClick = { 
                    val newLoc = if (uiState.selectedLocation == loc) "All" else loc
                    viewModel.setFilters(location = newLoc) 
                }
            )
        }
    }
}
