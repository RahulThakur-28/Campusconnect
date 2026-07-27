package com.rahul.campusconnect.data.remote

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.domain.model.Discussion
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.domain.model.Reply
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DiscussionRemoteDataSourceImpl @Inject constructor(
    private val pathProvider: FirestorePathProvider
) : DiscussionRemoteDataSource {

    override fun getDiscussions(
        collegeId: String,
        moduleType: DiscussionParentType,
        moduleId: String
    ): Flow<List<Discussion>> {
        return pathProvider.discussions(collegeId)
            .whereEqualTo("moduleType", moduleType.name)
            .whereEqualTo("moduleId", moduleId)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { 
                    it.toObject(Discussion::class.java)?.copy(discussionId = it.id) 
                }.filter { !it.isDeleted }
            }
    }

    override suspend fun saveDiscussion(collegeId: String, discussion: Discussion): Result<Unit> = try {
        val ref = pathProvider.discussions(collegeId).document()
        val finalDiscussion = discussion.copy(
            discussionId = ref.id,
            collegeId = collegeId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        ref.set(finalDiscussion).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateDiscussion(
        collegeId: String,
        discussionId: String,
        updates: Map<String, Any>
    ): Result<Unit> = try {
        pathProvider.discussions(collegeId).document(discussionId).update(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getReplies(collegeId: String, discussionId: String): Flow<List<Reply>> {
        return pathProvider.discussions(collegeId)
            .document(discussionId)
            .collection("replies")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(Reply::class.java)?.copy(replyId = it.id) }
                    .filter { !it.isDeleted }
                    .sortedWith(compareByDescending<Reply> { it.isOfficial }.thenBy { it.createdAt })
            }
    }

    override suspend fun saveReply(collegeId: String, reply: Reply): Result<Unit> = try {
        val discussionRef = pathProvider.discussions(collegeId).document(reply.discussionId)
        val replyRef = discussionRef.collection("replies").document()
        
        val finalReply = reply.copy(
            replyId = replyRef.id,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        pathProvider.colleges().firestore.runTransaction { transaction ->
            transaction.set(replyRef, finalReply)
            transaction.update(discussionRef, "replyCount", FieldValue.increment(1))
            transaction.update(discussionRef, "updatedAt", System.currentTimeMillis())
        }.await()
        
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateReply(
        collegeId: String,
        discussionId: String,
        replyId: String,
        updates: Map<String, Any>
    ): Result<Unit> = try {
        pathProvider.discussions(collegeId)
            .document(discussionId)
            .collection("replies")
            .document(replyId)
            .update(updates)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun reportContent(collegeId: String, report: Map<String, Any>): Result<Unit> = try {
        pathProvider.reports(collegeId).add(report).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getDiscussionById(
        collegeId: String,
        discussionId: String
    ): Result<Discussion?> = try {
        val doc = pathProvider.discussions(collegeId).document(discussionId).get().await()
        Result.success(doc.toObject(Discussion::class.java)?.copy(discussionId = doc.id))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getDiscussionsByUser(
        collegeId: String,
        userId: String
    ): Result<List<Discussion>> = try {
        val snapshot = pathProvider.discussions(collegeId)
            .whereEqualTo("createdBy", userId)
            .whereEqualTo("isDeleted", false)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        val discussions = snapshot.documents.mapNotNull { it.toObject(Discussion::class.java)?.copy(discussionId = it.id) }
        Result.success(discussions)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
