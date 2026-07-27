package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.common.storage.StoragePathGenerator
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.Notification
import com.rahul.campusconnect.domain.model.NotificationType
import com.rahul.campusconnect.domain.model.UserRole
import com.rahul.campusconnect.domain.model.VerificationRequest
import com.rahul.campusconnect.domain.repository.NotificationRepository
import com.rahul.campusconnect.domain.repository.VerificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VerificationRepositoryImpl @Inject constructor(
    private val pathProvider: FirestorePathProvider,
    private val storageManager: StorageManager,
    private val notificationRepository: NotificationRepository
) : VerificationRepository {

    override suspend fun submitVerificationRequest(
        request: VerificationRequest,
        documentUri: Uri
    ): Result<Unit> = try {
        val path = "verification/${request.collegeId}/${request.userId}.jpg"
        val uploadResult = storageManager.uploadImage(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = path,
            imageUri = documentUri
        )

        if (uploadResult.isFailure) {
            throw uploadResult.exceptionOrNull() ?: Exception("Upload failed")
        }

        val finalRequest = request.copy(
            documentUrl = uploadResult.getOrThrow(),
            documentStoragePath = path,
            status = "PENDING",
            submittedAt = System.currentTimeMillis()
        )

        pathProvider.verificationRequests(request.collegeId)
            .document(request.userId)
            .set(finalRequest)
            .await()

        pathProvider.users(request.collegeId)
            .document(request.userId)
            .update("verificationStatus", "PENDING")
            .await()

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getRequestsByStatus(collegeId: String, status: String): Flow<List<VerificationRequest>> {
        return pathProvider.verificationRequests(collegeId)
            .whereEqualTo("status", status)
            .orderBy("submittedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(VerificationRequest::class.java) }
    }

    override suspend fun approveRequest(request: VerificationRequest, adminId: String): Result<Unit> = try {
        val updates = mapOf(
            "role" to request.requestedRole.name,
            "verificationStatus" to "VERIFIED",
            "isVerified" to true,
            "verifiedBy" to adminId,
            "verifiedAt" to System.currentTimeMillis(),
            "verifiedCollegeId" to request.collegeId,
            "updatedAt" to System.currentTimeMillis()
        )

        pathProvider.colleges().firestore.runTransaction { transaction ->
            val userRef = pathProvider.users(request.collegeId).document(request.userId)
            val requestRef = pathProvider.verificationRequests(request.collegeId).document(request.userId)

            transaction.update(userRef, updates)
            transaction.update(requestRef, mapOf(
                "status" to "APPROVED",
                "reviewedAt" to System.currentTimeMillis(),
                "reviewedBy" to adminId
            ))
        }.await()

        Result.success(Unit).also {
            notificationRepository.sendNotification(
                Notification(
                    userId = request.userId,
                    title = "Verification Approved 🎉",
                    message = "Your account has been verified as ${request.requestedRole.displayName}.",
                    type = NotificationType.VERIFICATION_APPROVED,
                    collegeId = request.collegeId
                )
            )
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun rejectRequest(collegeId: String, userId: String, reason: String, adminId: String): Result<Unit> = try {
        pathProvider.colleges().firestore.runTransaction { transaction ->
            val userRef = pathProvider.users(collegeId).document(userId)
            val requestRef = pathProvider.verificationRequests(collegeId).document(userId)

            transaction.update(userRef, "verificationStatus", "REJECTED")
            transaction.update(requestRef, mapOf(
                "status" to "REJECTED",
                "rejectionReason" to reason,
                "reviewedAt" to System.currentTimeMillis(),
                "reviewedBy" to adminId
            ))
        }.await()
        Result.success(Unit).also {
            notificationRepository.sendNotification(
                Notification(
                    userId = userId,
                    title = "Verification Rejected",
                    message = "Reason: $reason. Please resubmit with correct documents.",
                    type = NotificationType.VERIFICATION_REJECTED,
                    collegeId = collegeId
                )
            )
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getRequestByUserId(userId: String, collegeId: String): Result<VerificationRequest?> = try {
        val doc = pathProvider.verificationRequests(collegeId).document(userId).get().await()
        Result.success(doc.toObject(VerificationRequest::class.java))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
