package com.rahul.campusconnect.domain.model

enum class SearchResultType(val label: String) {
    ANNOUNCEMENT("Announcement"),
    EVENT("Event"),
    PLACEMENT("Placement"),
    NOTE("Note"),
    LOST_FOUND("Lost & Found")
}

data class SearchResult(
    val id: String,
    val title: String,
    val subtitle: String,
    val type: SearchResultType,
    val metadata: String? = null
)
