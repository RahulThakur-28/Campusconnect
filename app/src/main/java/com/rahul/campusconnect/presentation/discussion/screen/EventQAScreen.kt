package com.rahul.campusconnect.presentation.discussion.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.presentation.discussion.components.DiscussionSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventQAScreen(
    parentId: String,
    parentType: DiscussionParentType,
    onBackClick: () -> Unit,
    onViewDiscussionClick: (String) -> Unit // Now handled by expanded visibility in DiscussionSection, but kept for signature compatibility
) {
    val scrollState = rememberScrollState()
    val screenTitle = when (parentType) {
        DiscussionParentType.EVENT -> "Event Discussion"
        DiscussionParentType.PLACEMENT -> "Placement Discussion"
        else -> "Discussion"
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
        ) {
            DiscussionSection(
                moduleType = parentType,
                moduleId = parentId
            )
        }
    }
}
