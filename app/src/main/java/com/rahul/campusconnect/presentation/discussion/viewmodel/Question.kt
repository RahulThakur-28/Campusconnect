package com.rahul.campusconnect.presentation.discussion.viewmodel

import com.rahul.campusconnect.model.DiscussionParentType
import com.rahul.campusconnect.model.UserRole

data class Question(
    val id: String = "",
    val parentId: String = "",
    val parentType: DiscussionParentType = DiscussionParentType.EVENT,
    val userId: String = "",
    val userName: String = "",
    val userRole: UserRole = UserRole.STUDENT,
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val answerCount: Int = 0,
    val likeCount: Int = 0,
    val hasOfficialAnswer: Boolean = false
)
