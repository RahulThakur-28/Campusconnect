package com.rahul.campusconnect.presentation.discussion.viewmodel

import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.domain.model.UserRole

data class Answer(
    val id: String = "",
    val questionId: String = "",
    val parentId: String = "",
    val parentType: DiscussionParentType = DiscussionParentType.EVENT,
    val userId: String = "",
    val userName: String = "",
    val userRole: UserRole = UserRole.STUDENT,
    val profileImage: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val likeCount: Int = 0,
    val isOfficial: Boolean = false
)
