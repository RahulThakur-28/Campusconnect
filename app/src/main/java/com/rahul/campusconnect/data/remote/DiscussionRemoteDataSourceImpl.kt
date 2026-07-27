package com.rahul.campusconnect.data.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.domain.model.Answer
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.domain.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DiscussionRemoteDataSourceImpl @Inject constructor(
    private val pathProvider: FirestorePathProvider
) : DiscussionRemoteDataSource {

    override fun getQuestions(collegeId: String, parentId: String, parentType: DiscussionParentType): Flow<List<Question>> {
        return pathProvider.discussions(collegeId)
            .whereEqualTo("parentId", parentId)
            .whereEqualTo("parentType", parentType.name)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(Question::class.java)?.copy(id = it.id) }
            }
    }

    override suspend fun getQuestionById(collegeId: String, questionId: String): Result<Question?> = try {
        val doc = pathProvider.discussions(collegeId).document(questionId).get().await()
        Result.success(doc.toObject(Question::class.java)?.copy(id = doc.id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getAnswers(collegeId: String, questionId: String): Flow<List<Answer>> {
        return pathProvider.discussions(collegeId)
            .document(questionId)
            .collection("answers")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(Answer::class.java)?.copy(id = it.id) }
            }
    }

    override suspend fun saveQuestion(collegeId: String, question: Question): Result<Unit> = try {
        val doc = pathProvider.discussions(collegeId).document()
        pathProvider.discussions(collegeId).document(doc.id).set(question.copy(id = doc.id, collegeId = collegeId)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun saveAnswer(collegeId: String, answer: Answer): Result<Unit> = try {
        val questionRef = pathProvider.discussions(collegeId).document(answer.questionId)
        val answerRef = questionRef.collection("answers").document()
        
        pathProvider.discussions(collegeId).firestore.runTransaction { transaction ->
            transaction.set(answerRef, answer.copy(id = answerRef.id, collegeId = collegeId))
            transaction.update(questionRef, "answerCount", FieldValue.increment(1))
        }.await()
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun likeQuestion(collegeId: String, questionId: String): Result<Unit> = try {
        pathProvider.discussions(collegeId).document(questionId).update("likeCount", FieldValue.increment(1)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun likeAnswer(collegeId: String, questionId: String, answerId: String): Result<Unit> = try {
        pathProvider.discussions(collegeId)
            .document(questionId)
            .collection("answers")
            .document(answerId)
            .update("likeCount", FieldValue.increment(1))
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getQuestionsByUser(collegeId: String, userId: String): Result<List<Question>> = try {
        val snapshot = pathProvider.discussions(collegeId)
            .whereEqualTo("userId", userId)
            .get()
            .await()
        val questions = snapshot.documents.mapNotNull { it.toObject(Question::class.java)?.copy(id = it.id) }
        Result.success(questions)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
