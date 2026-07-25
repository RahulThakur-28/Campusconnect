package com.rahul.campusconnect.domain.model

data class Announcement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val imageStoragePath: String? = null,
    val category: String = "General",
    val postedBy: String = "", // userId
    val postedByName: String = "",
    val postedByRole: String = "",
    val isVerified: Boolean = false,
    val postedAt: Long = 0L,
    val updatedAt: Long = 0L,
    val deleted: Boolean = false,
    val hasAttachment: Boolean = false,
    val attachmentUrl: String? = null,
    val attachmentStoragePath: String? = null,
    val collegeId: String = ""
)
