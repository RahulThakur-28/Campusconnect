package com.rahul.campusconnect.model

data class Note(
    val id: String = "",
    val title: String = "",
    val subject: String = "",
    val department: String = "",
    val semester: String = "",
    val uploadedBy: String = "",
    val thumbnailUrl: String = "",
    val pdfUrl: String = "",
    val downloads: Int = 0,
    val pages: Int = 0,
    val uploadedAt: Long = System.currentTimeMillis(),
    val fileSize: String = "",
    val description: String = "",
    val isVerified: Boolean = false
)
