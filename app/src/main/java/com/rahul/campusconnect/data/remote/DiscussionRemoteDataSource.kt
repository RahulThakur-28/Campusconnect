package com.rahul.campusconnect.data.remote

import com.rahul.campusconnect.domain.model.Discussion
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.domain.model.Reply
import kotlinx.coroutines.flow.Flow

interface DiscussionRemoteDataSource {
    fun getDiscussions(
        collegeId: String,
        moduleType: DiscussionParentType,
        moduleId: String
    ): Flow<List<Discussion>>

    suspend fun saveDiscussion(collegeId: String, discussion: Discussion): Result<Unit>
    
    suspend fun updateDiscussion(collegeId: String, discussionId: String, updates: Map<String, Any>): Result<Unit>

    fun getReplies(collegeId: String, discussionId: String): Flow<List<Reply>>

    suspend fun saveReply(collegeId: String, reply: Reply): Result<Unit>

    suspend fun updateReply(collegeId: String, discussionId: String, replyId: String, updates: Map<String, Any>): Result<Unit>
    
    suspend fun reportContent(collegeId: String, report: Map<String, Any>): Result<Unit>

    suspend fun getDiscussionById(collegeId: String, discussionId: String): Result<Discussion?>

    suspend fun getDiscussionsByUser(collegeId: String, userId: String): Result<List<Discussion>>
}
