package com.rahul.campusconnect.model

data class Announcement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val category: String = "General",
    val postedBy: String = "",
    val isVerified: Boolean = false,
    val timestamp: String = "",
    val hasAttachment: Boolean = false,
    val attachmentUrl: String? = null
)
