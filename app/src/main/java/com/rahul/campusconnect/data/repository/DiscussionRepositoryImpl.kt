package com.rahul.campusconnect.data.repository

import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.DiscussionRemoteDataSource
import com.rahul.campusconnect.domain.model.Answer
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.domain.model.Question
import com.rahul.campusconnect.domain.model.Notification
import com.rahul.campusconnect.domain.model.NotificationType
import com.rahul.campusconnect.domain.repository.EventQARepository
import com.rahul.campusconnect.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class DiscussionRepositoryImpl @Inject constructor(
    private val remoteDataSource: DiscussionRemoteDataSource,
    private val sessionManager: SessionManager,
    private val notificationRepository: NotificationRepository
) : EventQARepository {

    private fun getCollegeId(): String? = sessionManager.getCollegeId()

    override fun getQuestions(parentId: String, parentType: DiscussionParentType): Flow<List<Question>> {
        val collegeId = getCollegeId() ?: return emptyFlow()
        return remoteDataSource.getQuestions(collegeId, parentId, parentType)
    }

    override fun getAnswers(questionId: String): Flow<List<Answer>> {
        val collegeId = getCollegeId() ?: return emptyFlow()
        return remoteDataSource.getAnswers(collegeId, questionId)
    }

    override suspend fun getQuestionById(questionId: String): Result<Question?> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.getQuestionById(collegeId, questionId)
    }

    override suspend fun askQuestion(question: Question): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.saveQuestion(collegeId, question)
    }

    override suspend fun answerQuestion(answer: Answer): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        val result = remoteDataSource.saveAnswer(collegeId, answer)
        
        result.onSuccess {
            // Send notification to question owner
            remoteDataSource.getQuestionById(collegeId, answer.questionId).onSuccess { question ->
                question?.let {
                    if (it.userId != answer.userId) { // Don't notify self
                        notificationRepository.sendNotification(
                            Notification(
                                userId = it.userId,
                                title = "New Reply to your Question",
                                message = "${answer.userName}: ${answer.content.take(50)}...",
                                type = NotificationType.DISCUSSION_REPLY,
                                relatedId = answer.questionId,
                                collegeId = collegeId
                            )
                        )
                    }
                }
            }
        }
        return result
    }

    override suspend fun likeQuestion(questionId: String): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.likeQuestion(collegeId, questionId)
    }

    override suspend fun likeAnswer(questionId: String, answerId: String): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.likeAnswer(collegeId, questionId, answerId)
    }

    override suspend fun getMyQuestions(userId: String): Result<List<Question>> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.getQuestionsByUser(collegeId, userId)
    }
}
