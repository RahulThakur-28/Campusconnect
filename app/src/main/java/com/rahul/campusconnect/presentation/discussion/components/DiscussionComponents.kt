package com.rahul.campusconnect.presentation.discussion.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AskQuestionDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var question by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ask a Question", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("What is your question about?") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Question Details") },
                    placeholder = { Text("Explain your question in detail...") },
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(title, question) },
                enabled = title.isNotBlank() && question.isNotBlank()
            ) {
                Text("Post Question")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditDiscussionDialog(
    initialTitle: String,
    initialQuestion: String,
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var question by remember { mutableStateOf(initialQuestion) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Question", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    label = { Text("Question Details") },
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(title, question) },
                enabled = title.isNotBlank() && question.isNotBlank()
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditReplyDialog(
    initialMessage: String,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var message by remember { mutableStateOf(initialMessage) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Reply", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Message") },
                modifier = Modifier.fillMaxWidth().height(100.dp)
            )
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(message) },
                enabled = message.isNotBlank()
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ReplyInput(
    onSendClick: (String) -> Unit,
    placeholder: String = "Write a reply...",
    enabled: Boolean = true
) {
    var text by remember { mutableStateOf("") }

    Surface(
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(placeholder) },
                shape = RoundedCornerShape(24.dp),
                maxLines = 4,
                enabled = enabled
            )
            Spacer(Modifier.width(12.dp))
            IconButton(
                onClick = { 
                    if (text.isNotBlank()) {
                        onSendClick(text)
                        text = ""
                    }
                },
                enabled = text.isNotBlank() && enabled,
                colors = IconButtonDefaults.filledIconButtonColors()
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, null)
            }
        }
    }
}
