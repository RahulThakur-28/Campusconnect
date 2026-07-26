package com.rahul.campusconnect.domain.model

data class Report(
    val id: String = "",
    val userId: String = "",
    val type: String = "", // BUG, INAPPROPRIATE_CONTENT, etc.
    val description: String = "",
    val referenceId: String? = null, // ID of the reported item
    val status: String = "PENDING", // PENDING, RESOLVED
    val collegeId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
