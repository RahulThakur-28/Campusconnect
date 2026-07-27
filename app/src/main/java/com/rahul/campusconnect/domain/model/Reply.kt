package com.rahul.campusconnect.domain.model

data class Reply(
    val replyId: String = "",
    val discussionId: String = "",
    val message: String = "",
    val createdBy: String = "",
    val createdByName: String = "",
    val createdByPhoto: String = "",
    val createdByRole: UserRole = UserRole.STUDENT,
    val isVerified: Boolean = false,
    val isOfficial: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val isDeleted: Boolean = false,
    val likedBy: List<String> = emptyList()
)
