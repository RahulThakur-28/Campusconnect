package com.rahul.campusconnect.presentation.discussion.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rahul.campusconnect.domain.model.Discussion
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.domain.model.Reply
import com.rahul.campusconnect.presentation.discussion.state.DiscussionSort
import com.rahul.campusconnect.presentation.discussion.viewmodel.DiscussionViewModel
import com.rahul.campusconnect.ui.components.EmptyState

@Composable
fun DiscussionSection(
    moduleType: DiscussionParentType,
    moduleId: String,
    viewModel: DiscussionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    var showAskDialog by remember { mutableStateOf(false) }
    var expandedDiscussionId by remember { mutableStateOf<String?>(null) }
    
    var editingDiscussion by remember { mutableStateOf<Discussion?>(null) }
    var editingReply by remember { mutableStateOf<Pair<String, Reply>?>(null) } // discussionId to Reply

    var deleteConfirmDiscussionId by remember { mutableStateOf<String?>(null) }
    var deleteConfirmReplyId by remember { mutableStateOf<Pair<String, String>?>(null) } // discussionId to replyId

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(moduleId, moduleType) {
        viewModel.setModule(moduleType, moduleId)
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        SnackbarHost(hostState = snackbarHostState)
        
        if (uiState.isActionLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        }

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Discussion (${uiState.discussions.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { showAskDialog = true },
                enabled = !uiState.isActionLoading,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Ask")
            }
        }

        Spacer(Modifier.height(16.dp))

        // Search & Sort
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Search discussions...") },
                leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )
            
            var sortMenuExpanded by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { sortMenuExpanded = true }) {
                    Icon(Icons.Default.Sort, null)
                }
                DropdownMenu(expanded = sortMenuExpanded, onDismissRequest = { sortMenuExpanded = false }) {
                    DiscussionSort.entries.forEach { sort ->
                        DropdownMenuItem(
                            text = { Text(sort.label) },
                            onClick = { 
                                viewModel.onSortChanged(sort)
                                sortMenuExpanded = false 
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.discussions.isEmpty()) {
            EmptyState(
                message = "No Questions Yet",
                description = "Be the first person to ask a question.",
                icon = Icons.Default.Add, // Using Add as a placeholder for "bubble" icon if needed
                modifier = Modifier.padding(vertical = 32.dp)
            )
        } else {
            val filteredDiscussions = uiState.discussions.filter {
                it.title.contains(uiState.searchQuery, true) || 
                it.question.contains(uiState.searchQuery, true)
            }.let { list ->
                when (uiState.sortBy) {
                    DiscussionSort.NEWEST -> list.sortedByDescending { it.createdAt }
                    DiscussionSort.OLDEST -> list.sortedBy { it.createdAt }
                    DiscussionSort.MOST_LIKED -> list.sortedByDescending { it.likeCount }
                    DiscussionSort.MOST_REPLIED -> list.sortedByDescending { it.replyCount }
                }
            }

            filteredDiscussions.forEach { discussion ->
                QuestionCard(
                    discussion = discussion,
                    currentUserId = uiState.currentUserId,
                    currentUserRole = uiState.currentUserRole,
                    onLikeClick = { viewModel.likeQuestion(discussion.discussionId) },
                    onReplyClick = { 
                        if (expandedDiscussionId == discussion.discussionId) {
                            expandedDiscussionId = null
                        } else {
                            expandedDiscussionId = discussion.discussionId
                            viewModel.loadReplies(discussion.discussionId)
                        }
                    },
                    onEditClick = { editingDiscussion = discussion },
                    onDeleteClick = { deleteConfirmDiscussionId = discussion.discussionId },
                    onReportClick = { reason -> viewModel.report(discussion.discussionId, "DISCUSSION", reason) },
                    onCardClick = { 
                        if (expandedDiscussionId == discussion.discussionId) {
                            expandedDiscussionId = null
                        } else {
                            expandedDiscussionId = discussion.discussionId
                            viewModel.loadReplies(discussion.discussionId)
                        }
                    }
                )
                
                AnimatedVisibility(visible = expandedDiscussionId == discussion.discussionId) {
                    Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 16.dp)) {
                        val replies = uiState.repliesMap[discussion.discussionId] ?: emptyList()
                        replies.forEach { reply ->
                            AnswerCard(
                                reply = reply,
                                currentUserId = uiState.currentUserId,
                                currentUserRole = uiState.currentUserRole,
                                onLikeClick = { viewModel.likeReply(discussion.discussionId, reply.replyId) },
                                onDeleteClick = { deleteConfirmReplyId = discussion.discussionId to reply.replyId },
                                onReportClick = { reason -> viewModel.report(reply.replyId, "REPLY", reason) },
                                onEditClick = { editingReply = discussion.discussionId to reply },
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        
                        Spacer(Modifier.height(8.dp))
                        
                        ReplyInput(
                            onSendClick = { msg -> viewModel.answerQuestion(discussion.discussionId, msg) },
                            enabled = uiState.currentUserRole != "STUDENT"
                        )
                        if (uiState.currentUserRole == "STUDENT") {
                            Text(
                                "Only verified users can answer questions.",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }

    if (showAskDialog) {
        AskQuestionDialog(
            onDismiss = { showAskDialog = false },
            onSubmit = { title, q -> 
                viewModel.askQuestion(title, q)
                showAskDialog = false
            }
        )
    }

    editingDiscussion?.let { discussion ->
        EditDiscussionDialog(
            initialTitle = discussion.title,
            initialQuestion = discussion.question,
            onDismiss = { editingDiscussion = null },
            onSubmit = { title, question ->
                viewModel.editQuestion(discussion.discussionId, title, question)
                editingDiscussion = null
            }
        )
    }

    editingReply?.let { (discussionId, reply) ->
        EditReplyDialog(
            initialMessage = reply.message,
            onDismiss = { editingReply = null },
            onSubmit = { message ->
                viewModel.editReply(discussionId, reply.replyId, message)
                editingReply = null
            }
        )
    }

    deleteConfirmDiscussionId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteConfirmDiscussionId = null },
            title = { Text("Delete Question") },
            text = { Text("Are you sure you want to delete this question? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.deleteQuestion(id)
                    deleteConfirmDiscussionId = null
                }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmDiscussionId = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    deleteConfirmReplyId?.let { (discussionId, replyId) ->
        AlertDialog(
            onDismissRequest = { deleteConfirmReplyId = null },
            title = { Text("Delete Reply") },
            text = { Text("Are you sure you want to delete this reply?") },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.deleteReply(discussionId, replyId)
                    deleteConfirmReplyId = null
                }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmReplyId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
