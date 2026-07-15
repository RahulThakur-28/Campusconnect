package com.rahul.campusconnect.data.repository

import com.rahul.campusconnect.domain.repository.EventQARepository
import com.rahul.campusconnect.model.DiscussionParentType
import com.rahul.campusconnect.presentation.discussion.viewmodel.Answer
import com.rahul.campusconnect.presentation.discussion.viewmodel.Question
import com.rahul.campusconnect.model.UserRole
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeEventQARepository @Inject constructor() : EventQARepository {
    
    private val fakeQuestions = listOf(
        Question(
            id = "1",
            parentId = "event1",
            parentType = DiscussionParentType.EVENT,
            userId = "user1",
            userName = "Amit Sharma",
            userRole = UserRole.STUDENT,
            content = "Will there be any participation certificate for this event?",
            timestamp = System.currentTimeMillis() - 3600000,
            answerCount = 1,
            likeCount = 5,
            hasOfficialAnswer = true
        ),
        Question(
            id = "2",
            parentId = "event1",
            parentType = DiscussionParentType.EVENT,
            userId = "user2",
            userName = "Priya Singh",
            userRole = UserRole.VERIFIED_STUDENT,
            content = "Can we bring our own laptops for the hackathon?",
            timestamp = System.currentTimeMillis() - 7200000,
            answerCount = 2,
            likeCount = 3,
            hasOfficialAnswer = false
        ),
        Question(
            id = "3",
            parentId = "placement1",
            parentType = DiscussionParentType.PLACEMENT,
            userId = "user3",
            userName = "Rahul Thakur",
            userRole = UserRole.STUDENT,
            content = "What is the expected CTC for this role?",
            timestamp = System.currentTimeMillis() - 1800000,
            answerCount = 0,
            likeCount = 2,
            hasOfficialAnswer = false
        )
    )

    private val fakeAnswers = listOf(
        Answer(
            id = "a1",
            questionId = "1",
            parentId = "event1",
            parentType = DiscussionParentType.EVENT,
            userId = "admin1",
            userName = "Prof. Rajesh",
            userRole = UserRole.VERIFIED_TEACHER,
            content = "Yes, all participants will receive e-certificates via email after the event.",
//            timestamp = System.currentTimeMillis() - 1800000,
            isOfficial = true,
            likeCount = 10
        )
    )

    override fun getQuestions(parentId: String, parentType: DiscussionParentType): Flow<List<Question>> = flow {
        delay(1000)
        emit(fakeQuestions.filter { it.parentId == parentId && it.parentType == parentType })
    }

    override fun getAnswers(questionId: String): Flow<List<Answer>> = flow {
        delay(500)
        emit(fakeAnswers.filter { it.questionId == questionId })
    }

    override suspend fun askQuestion(question: Question) {
        delay(500)
    }

    override suspend fun answerQuestion(answer: Answer) {
        delay(500)
    }

    override suspend fun likeQuestion(questionId: String) {
        delay(200)
    }

    override suspend fun likeAnswer(answerId: String) {
        delay(200)
    }
}
