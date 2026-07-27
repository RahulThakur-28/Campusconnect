package com.rahul.campusconnect.presentation.discussion.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rahul.campusconnect.presentation.discussion.components.AnswerCard
import com.rahul.campusconnect.presentation.discussion.components.EditDiscussionDialog
import com.rahul.campusconnect.presentation.discussion.components.EditReplyDialog
import com.rahul.campusconnect.presentation.discussion.components.QuestionCard
import com.rahul.campusconnect.presentation.discussion.components.ReplyInput
import com.rahul.campusconnect.presentation.discussion.viewmodel.QuestionThreadViewModel
import com.rahul.campusconnect.ui.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionThreadScreen(
    questionId: String,
    onBackClick: () -> Unit,
    viewModel: QuestionThreadViewModel = hiltViewModel()
) {
    val question by viewModel.question.collectAsStateWithLifecycle()
    val answers by viewModel.answers.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val currentUserId by viewModel.currentUserId.collectAsStateWithLifecycle()
    val currentUserRole by viewModel.currentUserRole.collectAsStateWithLifecycle()

    var editingQuestion by remember { mutableStateOf(false) }
    var editingReply by remember { mutableStateOf<com.rahul.campusconnect.domain.model.Reply?>(null) }

    LaunchedEffect(questionId) {
        viewModel.loadThread(questionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Discussion", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        bottomBar = {
            ReplyInput(
                onSendClick = { message -> viewModel.submitAnswer(message) }
            )
        }
    ) { padding ->
        if (isLoading && question == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else if (error != null && question == null) {
            EmptyState(message = error ?: "An error occurred", modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                question?.let {
                    item {
                        QuestionCard(
                            discussion = it,
                            currentUserId = currentUserId,
                            currentUserRole = currentUserRole,
                            onLikeClick = { viewModel.likeQuestion(it.discussionId) },
                            onReplyClick = { /* Already in thread */ },
                            onEditClick = { editingQuestion = true },
                            onDeleteClick = { viewModel.deleteQuestion(); onBackClick() },
                            onReportClick = { reason -> viewModel.report(it.discussionId, "DISCUSSION", reason) }
                        )
                    }
                }

                item {
                    Text(
                        text = "Answers (${answers.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (answers.isEmpty()) {
                    item {
                        EmptyState(
                            message = "No answers yet",
                            description = "Be the first to help out!",
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                    }
                } else {
                    items(answers, key = { it.replyId }) { reply ->
                        AnswerCard(
                            reply = reply,
                            currentUserId = currentUserId,
                            currentUserRole = currentUserRole,
                            onLikeClick = { viewModel.likeAnswer(reply.replyId) },
                            onEditClick = { editingReply = reply },
                            onDeleteClick = { viewModel.deleteReply(reply.replyId) },
                            onReportClick = { reason -> viewModel.report(reply.replyId, "REPLY", reason) }
                        )
                    }
                }
                
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (editingQuestion && question != null) {
        EditDiscussionDialog(
            initialTitle = question!!.title,
            initialQuestion = question!!.question,
            onDismiss = { editingQuestion = false },
            onSubmit = { title, q ->
                viewModel.editQuestion(title, q)
                editingQuestion = false
            }
        )
    }

    editingReply?.let { reply ->
        EditReplyDialog(
            initialMessage = reply.message,
            onDismiss = { editingReply = null },
            onSubmit = { message ->
                viewModel.editReply(reply.replyId, message)
                editingReply = null
            }
        )
    }
}
