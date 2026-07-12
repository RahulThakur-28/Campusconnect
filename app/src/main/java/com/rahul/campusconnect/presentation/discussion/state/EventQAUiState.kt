package com.rahul.campusconnect.presentation.discussion.state

import com.rahul.campusconnect.presentation.discussion.viewmodel.Question

data class EventQAUiState(
    val questions: List<Question> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val error: String? = null
)