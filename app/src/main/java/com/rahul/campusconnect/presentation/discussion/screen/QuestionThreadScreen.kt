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
import com.rahul.campusconnect.presentation.discussion.components.QuestionCard
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

    var answerText by remember { mutableStateOf("") }

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
            Surface(tonalElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = answerText,
                        onValueChange = { answerText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Write an answer...") },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4
                    )
                    Spacer(Modifier.width(12.dp))
                    FilledIconButton(
                        onClick = {
                            if (answerText.isNotBlank()) {
                                viewModel.submitAnswer(answerText)
                                answerText = ""
                            }
                        },
                        enabled = answerText.isNotBlank()
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null)
                    }
                }
            }
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
                            question = it,
                            onLikeClick = { viewModel.likeQuestion(it.id) },
                            onViewDiscussionClick = {} // Already on this screen
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
                    items(answers, key = { it.id }) { answer ->
                        AnswerCard(
                            answer = answer,
                            onLikeClick = { viewModel.likeAnswer(answer.id) }
                        )
                    }
                }
                
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}
