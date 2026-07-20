package com.rahul.campusconnect.presentation.discussion.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.presentation.discussion.components.EmptyQuestionState
import com.rahul.campusconnect.presentation.discussion.components.QuestionCard
import com.rahul.campusconnect.presentation.discussion.components.QuestionLoadingShimmer
import com.rahul.campusconnect.presentation.discussion.viewmodel.EventQAViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventQAScreen(
    parentId: String,
    parentType: DiscussionParentType,
    onBackClick: () -> Unit,
    onViewDiscussionClick: (String) -> Unit,
    viewModel: EventQAViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val screenTitle = when (parentType) {
        DiscussionParentType.EVENT -> "Event Discussion"
        DiscussionParentType.PLACEMENT -> "Placement Discussion"
    }

    LaunchedEffect(parentId, parentType) {
        viewModel.loadQuestions(parentId, parentType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = screenTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: Open Ask Question Dialog */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Ask Question") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search questions...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true
            )

            if (uiState.isLoading) {
                QuestionLoadingShimmer()
            } else if (uiState.questions.isEmpty()) {
                EmptyQuestionState()
            } else {
                val filteredQuestions = uiState.questions.filter {
                    it.content.contains(uiState.searchQuery, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredQuestions) { question ->
                        QuestionCard(
                            question = question,
                            onLikeClick = { viewModel.onLikeQuestion(question.id) },
                            onViewDiscussionClick = { onViewDiscussionClick(question.id) }
                        )
                    }
                }
            }
        }
    }
}
