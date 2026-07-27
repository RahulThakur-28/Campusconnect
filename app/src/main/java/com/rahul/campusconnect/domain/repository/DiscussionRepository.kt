package com.rahul.campusconnect.domain.repository

import com.rahul.campusconnect.domain.model.Discussion
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.domain.model.Reply
import kotlinx.coroutines.flow.Flow

interface DiscussionRepository {
    fun getDiscussions(
        moduleType: DiscussionParentType,
        moduleId: String
    ): Flow<List<Discussion>>

    suspend fun askQuestion(discussion: Discussion): Result<Unit>
    
    suspend fun editQuestion(discussionId: String, title: String, question: String): Result<Unit>

    suspend fun deleteQuestion(discussionId: String): Result<Unit>

    suspend fun likeQuestion(discussionId: String, userId: String): Result<Unit>

    fun getReplies(discussionId: String): Flow<List<Reply>>

    suspend fun answerQuestion(reply: Reply): Result<Unit>

    suspend fun editReply(discussionId: String, replyId: String, message: String): Result<Unit>

    suspend fun deleteReply(discussionId: String, replyId: String): Result<Unit>

    suspend fun likeReply(discussionId: String, replyId: String, userId: String): Result<Unit>

    suspend fun report(id: String, type: String, reason: String): Result<Unit>
    
    suspend fun getDiscussionById(discussionId: String): Result<Discussion?>

    suspend fun getMyDiscussions(userId: String): Result<List<Discussion>>
}
