package com.rahul.campusconnect.domain.model

data class Note(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val subject: String = "",
    val semester: String = "",
    val branch: String = "",
    val uploadedBy: String = "", // userId
    val uploadedByName: String = "",
    val uploadedByRole: String = "",
    val collegeId: String = "",
    val fileUrl: String = "",
    val storagePath: String = "",
    val thumbnailUrl: String? = null,
    val thumbnailStoragePath: String? = null,
    val fileType: String = "PDF",
    val fileExtension: String = "pdf",
    val fileSize: String = "",
    val downloadCount: Int = 0,
    val tags: List<String> = emptyList(),
    val isVerified: Boolean = false,
    val deleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
