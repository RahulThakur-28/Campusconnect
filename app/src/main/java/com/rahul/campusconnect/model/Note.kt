package com.rahul.campusconnect.model

data class Note(
    val id: String = "",
    val title: String = "",
    val subject: String = "",
    val semester: String = "",
    val uploadedBy: String = "",
    val thumbnailUrl: String = "",
    val pdfUrl: String = "",
    val downloads: Int = 0,
    val uploadedAt: Long = 0L,

    // Future Ready
    val fileSize: String = "",        // 2.4 MB
    val fileType: String = "PDF",     // PDF, PPT, DOC
    val isVerified: Boolean = false   // Verified Teacher Notes
)