package com.rahul.campusconnect.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.common.storage.StoragePathGenerator
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.Announcement
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AnnouncementRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageManager: StorageManager
) : AnnouncementRemoteDataSource {

    private suspend fun getCollegeId(): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User is not logged in.")

        val snapshot = firestore
            .collection(Constants.USERS)
            .document(uid)
            .get()
            .await()

        val collegeId = snapshot.getString("collegeId")
        if (collegeId.isNullOrBlank()) {
            throw IllegalStateException("College ID not found.")
        }
        return collegeId
    }

    private suspend fun announcementsCollection(): CollectionReference {
        return firestore.collection(Constants.COLLEGES)
            .document(getCollegeId())
            .collection(Constants.ANNOUNCEMENTS)
    }

    override suspend fun getAnnouncements(): Result<List<Announcement>> {

        return try {

            Log.d("ANNOUNCEMENT_QUERY", "Fetching announcements...")

            val snapshot = announcementsCollection()
                .whereEqualTo(Constants.DELETED, false)
                .orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(
                "ANNOUNCEMENT_QUERY",
                "Documents found = ${snapshot.size()}"
            )

            snapshot.documents.forEach {
                Log.d(
                    "ANNOUNCEMENT_QUERY",
                    it.data.toString()
                )
            }

            val announcements = snapshot.documents.mapNotNull {
                it.toObject(Announcement::class.java)?.copy(id = it.id)
            }

            Result.success(announcements)

        } catch (e: FirebaseFirestoreException) {

            Log.e("ANNOUNCEMENT_QUERY", "========================================")
            Log.e("ANNOUNCEMENT_QUERY", "Firestore Exception")
            Log.e("ANNOUNCEMENT_QUERY", "Code    : ${e.code}")
            Log.e("ANNOUNCEMENT_QUERY", "Message : ${e.message}")
            Log.e("ANNOUNCEMENT_QUERY", "Cause   : ${e.cause}")
            Log.e("ANNOUNCEMENT_QUERY", "========================================", e)

            Result.failure(e)

        } catch (e: Exception) {

            Log.e("ANNOUNCEMENT_QUERY", "General Exception", e)

            Result.failure(e)
        }
    }

    override suspend fun getAnnouncementById(announcementId: String): Result<Announcement?> {
        return try {
            val document = announcementsCollection()
                .document(announcementId)
                .get()
                .await()

            val announcement = document.toObject(Announcement::class.java)?.copy(id = document.id)
            
            if (announcement?.deleted == true) {
                Result.success(null)
            } else {
                Result.success(announcement)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createAnnouncement(announcement: Announcement): Result<String> {
        return try {
            val document = announcementsCollection().document(announcement.id.ifBlank { generateAnnouncementId() })
            val currentTime = System.currentTimeMillis()
            val collegeId = getCollegeId()

            val announcementWithId = announcement.copy(
                id = document.id,
                postedAt = currentTime,
                updatedAt = currentTime,
                deleted = false,
                collegeId = collegeId
            )
            document.set(announcementWithId).await()
            Result.success(document.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAnnouncement(announcement: Announcement): Result<Unit> {
        return try {
            val updatedAnnouncement = announcement.copy(
                updatedAt = System.currentTimeMillis()
            )
            announcementsCollection()
                .document(announcement.id)
                .set(updatedAnnouncement)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAnnouncement(announcementId: String): Result<Unit> {
        return try {
            announcementsCollection()
                .document(announcementId)
                .update(
                    mapOf(
                        Constants.DELETED to true,
                        Constants.UPDATED_AT to System.currentTimeMillis()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyAnnouncements(userId: String): Result<List<Announcement>> {
        return try {
            val snapshot = announcementsCollection()
                .whereEqualTo(Constants.DELETED, false)
                .whereEqualTo("postedBy", userId)
                .orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val announcements = snapshot.documents.mapNotNull {
                it.toObject(Announcement::class.java)?.copy(id = it.id)
            }
            Result.success(announcements)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun generateAnnouncementId(): String {
        // We can't suspend here if we want to use it in non-suspend context, but document() is not suspend.
        // However, announcementsCollection() IS suspend because of getCollegeId().
        // For simplicity, we can use a random UUID if collegeId is not needed for ID generation.
        return java.util.UUID.randomUUID().toString()
    }

    override suspend fun uploadAnnouncementImage(
        announcementId: String,
        imageUri: Uri
    ): Result<Pair<String, String>> {
        return try {
            val path = StoragePathGenerator.announcementBanner(getCollegeId(), announcementId)
            storageManager.uploadImage(
                bucket = StorageConstants.MEDIA_BUCKET,
                path = path,
                imageUri = imageUri
            ).map { url -> Pair(url, path) }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadAnnouncementAttachment(
        announcementId: String,
        attachmentUri: Uri
    ): Result<Pair<String, String>> {
        return try {
            // Need to extract extension from Uri or use a fixed one like pdf for now
            val path = StoragePathGenerator.announcementAttachment(getCollegeId(), announcementId, "pdf")
            storageManager.uploadPdf(
                bucket = StorageConstants.MEDIA_BUCKET,
                path = path,
                pdfUri = attachmentUri
            ).map { url -> Pair(url, path) }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFile(path: String): Result<Unit> {
        return storageManager.deleteFile(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = path
        )
    }
}
