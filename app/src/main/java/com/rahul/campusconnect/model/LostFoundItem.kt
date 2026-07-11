package com.rahul.campusconnect.model

data class LostFoundItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val location: String = "",
    val reportedBy: String = "",
    val reportedDate: String = "",
    val status: String = "",
    val contact: String = "",

    // Future Ready
    val isVerified: Boolean = false,
    val claimedBy: String = "",
    val createdAt: Long = 0L
)