package com.rahul.campusconnect.presentation.event.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rahul.campusconnect.presentation.event.viewmodel.MyEventsViewModel
import com.rahul.campusconnect.ui.components.EmptyState
import com.rahul.campusconnect.ui.components.EventCard
import com.rahul.campusconnect.ui.components.EventCardStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(
    onBackClick: () -> Unit,
    onEventClick: (String) -> Unit,
    navController: NavController,
    viewModel: MyEventsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Observe refresh signal from Navigation BackStack
    val refreshSignal = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("refresh")
        ?.observeAsState()

    LaunchedEffect(refreshSignal?.value) {
        if (refreshSignal?.value == true) {
            viewModel.refresh()
            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("refresh")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Events", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = viewModel::refresh,
            modifier = Modifier.padding(padding)
        ) {
            if (uiState.isLoading && !uiState.isRefreshing) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.isEmpty) {
                EmptyState(
                    message = "You haven't organized any events yet.",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = uiState.events,
                        key = { it.id }
                    ) { event ->
                        EventCard(
                            event = event,
                            cardStyle = EventCardStyle.Large,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onEventClick(event.id) }
                        )
                    }
                }
            }
        }
    }
}
