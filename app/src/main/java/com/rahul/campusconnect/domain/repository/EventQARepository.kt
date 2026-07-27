package com.rahul.campusconnect.domain.repository

import com.rahul.campusconnect.domain.model.Answer
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.domain.model.Question
import kotlinx.coroutines.flow.Flow

interface EventQARepository {
    fun getQuestions(parentId: String, parentType: DiscussionParentType): Flow<List<Question>>
    fun getAnswers(questionId: String): Flow<List<Answer>>
    suspend fun getQuestionById(questionId: String): Result<Question?>
    suspend fun askQuestion(question: Question): Result<Unit>
    suspend fun answerQuestion(answer: Answer): Result<Unit>
    suspend fun likeQuestion(questionId: String): Result<Unit>
    suspend fun likeAnswer(questionId: String, answerId: String): Result<Unit>
    suspend fun getMyQuestions(userId: String): Result<List<Question>>
}
