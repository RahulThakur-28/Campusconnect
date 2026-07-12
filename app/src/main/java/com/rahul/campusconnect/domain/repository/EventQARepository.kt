package com.rahul.campusconnect.domain.repository

import com.rahul.campusconnect.model.DiscussionParentType
import com.rahul.campusconnect.presentation.discussion.viewmodel.Answer
import com.rahul.campusconnect.presentation.discussion.viewmodel.Question
import kotlinx.coroutines.flow.Flow

interface EventQARepository {
    fun getQuestions(parentId: String, parentType: DiscussionParentType): Flow<List<Question>>
    fun getAnswers(questionId: String): Flow<List<Answer>>
    suspend fun askQuestion(question: Question)
    suspend fun answerQuestion(answer: Answer)
    suspend fun likeQuestion(questionId: String)
    suspend fun likeAnswer(answerId: String)
}
