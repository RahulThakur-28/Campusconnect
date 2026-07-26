package com.rahul.campusconnect.data.remote

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.domain.model.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationRemoteDataSourceImpl @Inject constructor(
    private val pathProvider: FirestorePathProvider
) : NotificationRemoteDataSource {

    override fun getNotifications(collegeId: String): Flow<List<Notification>> {
        return pathProvider.notifications(collegeId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(Notification::class.java)?.copy(id = it.id) }
            }
    }

    override suspend fun markAsRead(collegeId: String, notificationId: String): Result<Unit> = try {
        pathProvider.notifications(collegeId).document(notificationId).update("isRead", true).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun markAllAsRead(collegeId: String): Result<Unit> = try {
        val batch = pathProvider.notifications(collegeId).firestore.batch()
        val unread = pathProvider.notifications(collegeId).whereEqualTo("isRead", false).get().await()
        for (doc in unread.documents) {
            batch.update(doc.reference, "isRead", true)
        }
        batch.commit().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteNotification(collegeId: String, notificationId: String): Result<Unit> = try {
        pathProvider.notifications(collegeId).document(notificationId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
