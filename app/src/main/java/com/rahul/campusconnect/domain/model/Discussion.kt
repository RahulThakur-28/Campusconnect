package com.rahul.campusconnect.domain.model

data class Discussion(
    val discussionId: String = "",
    val collegeId: String = "",
    val moduleType: DiscussionParentType = DiscussionParentType.EVENT,
    val moduleId: String = "",
    val title: String = "",
    val question: String = "",
    val createdBy: String = "",
    val createdByName: String = "",
    val createdByPhoto: String = "",
    val createdByRole: UserRole = UserRole.STUDENT,
    val isVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val replyCount: Int = 0,
    val isDeleted: Boolean = false,
    val likedBy: List<String> = emptyList() // To handle one like per user
)
