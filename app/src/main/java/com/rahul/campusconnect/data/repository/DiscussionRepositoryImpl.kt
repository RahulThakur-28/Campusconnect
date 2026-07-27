package com.rahul.campusconnect.data.repository

import com.google.firebase.firestore.FieldValue
import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.DiscussionRemoteDataSource
import com.rahul.campusconnect.domain.model.*
import com.rahul.campusconnect.domain.repository.DiscussionRepository
import com.rahul.campusconnect.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class DiscussionRepositoryImpl @Inject constructor(
    private val remoteDataSource: DiscussionRemoteDataSource,
    private val sessionManager: SessionManager,
    private val notificationRepository: NotificationRepository
) : DiscussionRepository {

    private fun getCollegeId(): String? = sessionManager.getCollegeId()

    override fun getDiscussions(
        moduleType: DiscussionParentType,
        moduleId: String
    ): Flow<List<Discussion>> {
        val collegeId = getCollegeId() ?: return emptyFlow()
        return remoteDataSource.getDiscussions(collegeId, moduleType, moduleId)
    }

    override suspend fun askQuestion(discussion: Discussion): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.saveDiscussion(collegeId, discussion)
    }

    override suspend fun editQuestion(
        discussionId: String,
        title: String,
        question: String
    ): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.updateDiscussion(
            collegeId,
            discussionId,
            mapOf("title" to title, "question" to question, "updatedAt" to System.currentTimeMillis())
        )
    }

    override suspend fun deleteQuestion(discussionId: String): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.updateDiscussion(collegeId, discussionId, mapOf("isDeleted" to true))
    }

    override suspend fun likeQuestion(discussionId: String, userId: String): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.updateDiscussion(
            collegeId,
            discussionId,
            mapOf("likeCount" to FieldValue.increment(1), "likedBy" to FieldValue.arrayUnion(userId))
        )
    }

    override fun getReplies(discussionId: String): Flow<List<Reply>> {
        val collegeId = getCollegeId() ?: return emptyFlow()
        return remoteDataSource.getReplies(collegeId, discussionId)
    }

    override suspend fun answerQuestion(reply: Reply): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        val result = remoteDataSource.saveReply(collegeId, reply)
        
        result.onSuccess {
            // Send notification to question owner
            remoteDataSource.getDiscussionById(collegeId, reply.discussionId).onSuccess { discussion ->
                discussion?.let {
                    if (it.createdBy != reply.createdBy) {
                        notificationRepository.sendNotification(
                            Notification(
                                userId = it.createdBy,
                                title = "New Reply to your Question",
                                message = "${reply.createdByName}: ${reply.message.take(50)}...",
                                type = NotificationType.DISCUSSION_REPLY,
                                relatedId = reply.discussionId,
                                collegeId = collegeId
                            )
                        )
                    }
                }
            }
        }
        return result
    }

    override suspend fun editReply(
        discussionId: String,
        replyId: String,
        message: String
    ): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.updateReply(
            collegeId,
            discussionId,
            replyId,
            mapOf("message" to message, "updatedAt" to System.currentTimeMillis())
        )
    }

    override suspend fun deleteReply(discussionId: String, replyId: String): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.updateReply(collegeId, discussionId, replyId, mapOf("isDeleted" to true))
    }

    override suspend fun likeReply(discussionId: String, replyId: String, userId: String): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.updateReply(
            collegeId,
            discussionId,
            replyId,
            mapOf("likeCount" to FieldValue.increment(1), "likedBy" to FieldValue.arrayUnion(userId))
        )
    }

    override suspend fun report(id: String, type: String, reason: String): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        val userId = sessionManager.getUid() ?: return Result.failure(Exception("Not logged in"))
        
        val report = mapOf(
            "targetId" to id,
            "targetType" to type, // DISCUSSION or REPLY
            "reason" to reason,
            "reportedBy" to userId,
            "submittedAt" to System.currentTimeMillis()
        )
        return remoteDataSource.reportContent(collegeId, report)
    }

    override suspend fun getDiscussionById(discussionId: String): Result<Discussion?> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.getDiscussionById(collegeId, discussionId)
    }

    override suspend fun getMyDiscussions(userId: String): Result<List<Discussion>> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.getDiscussionsByUser(collegeId, userId)
    }
}
