package com.rahul.campusconnect.presentation.discussion.state

import com.rahul.campusconnect.domain.model.Discussion
import com.rahul.campusconnect.domain.model.Reply

data class DiscussionUiState(
    val discussions: List<Discussion> = emptyList(),
    val repliesMap: Map<String, List<Reply>> = emptyMap(), // discussionId -> list of replies
    val isLoading: Boolean = false,
    val isActionLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val searchQuery: String = "",
    val sortBy: DiscussionSort = DiscussionSort.NEWEST,
    val currentUserId: String = "",
    val currentUserRole: String = ""
)

enum class DiscussionSort(val label: String) {
    NEWEST("Newest"),
    OLDEST("Oldest"),
    MOST_LIKED("Most Liked"),
    MOST_REPLIED("Most Replied")
}
