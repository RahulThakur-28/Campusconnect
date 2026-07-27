package com.rahul.campusconnect.data.remote

import com.rahul.campusconnect.domain.model.Answer
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.domain.model.Question
import kotlinx.coroutines.flow.Flow

interface DiscussionRemoteDataSource {
    fun getQuestions(collegeId: String, parentId: String, parentType: DiscussionParentType): Flow<List<Question>>
    fun getAnswers(collegeId: String, questionId: String): Flow<List<Answer>>
    suspend fun getQuestionById(collegeId: String, questionId: String): Result<Question?>
    suspend fun saveQuestion(collegeId: String, question: Question): Result<Unit>
    suspend fun saveAnswer(collegeId: String, answer: Answer): Result<Unit>
    suspend fun likeQuestion(collegeId: String, questionId: String): Result<Unit>
    suspend fun likeAnswer(collegeId: String, questionId: String, answerId: String): Result<Unit>
    suspend fun getQuestionsByUser(collegeId: String, userId: String): Result<List<Question>>
}
