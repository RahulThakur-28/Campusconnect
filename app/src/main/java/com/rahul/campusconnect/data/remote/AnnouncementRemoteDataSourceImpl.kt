package com.rahul.campusconnect.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.common.storage.StoragePathGenerator
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.Announcement
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AnnouncementRemoteDataSourceImpl @Inject constructor(
    private val pathProvider: FirestorePathProvider,
    private val storageManager: StorageManager
) : AnnouncementRemoteDataSource {

    override suspend fun getAnnouncements(collegeId: String): Result<List<Announcement>> {
        return try {
            Log.d("ANNOUNCEMENT_QUERY", "Fetching announcements for college: $collegeId")
            val snapshot = pathProvider.announcements(collegeId)
                .whereEqualTo(Constants.DELETED, false)
                .orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val announcements = snapshot.documents.mapNotNull {
                it.toObject(Announcement::class.java)?.copy(id = it.id)
            }
            Result.success(announcements)
        } catch (e: Exception) {
            Log.e("ANNOUNCEMENT_QUERY", "Error fetching announcements", e)
            Result.failure(e)
        }
    }

    override suspend fun getAnnouncementById(collegeId: String, announcementId: String): Result<Announcement?> {
        return try {
            val document = pathProvider.announcements(collegeId)
                .document(announcementId)
                .get()
                .await()

            val announcement = document.toObject(Announcement::class.java)?.copy(id = document.id)
            if (announcement?.deleted == true) Result.success(null)
            else Result.success(announcement)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createAnnouncement(collegeId: String, announcement: Announcement): Result<String> {
        return try {
            val document = pathProvider.announcements(collegeId).document(announcement.id.ifBlank { generateAnnouncementId() })
            val currentTime = System.currentTimeMillis()
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

    override suspend fun updateAnnouncement(collegeId: String, announcement: Announcement): Result<Unit> {
        return try {
            val updatedAnnouncement = announcement.copy(updatedAt = System.currentTimeMillis())
            pathProvider.announcements(collegeId)
                .document(announcement.id)
                .set(updatedAnnouncement)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAnnouncement(collegeId: String, announcementId: String): Result<Unit> {
        return try {
            pathProvider.announcements(collegeId)
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

    override suspend fun getMyAnnouncements(collegeId: String, userId: String): Result<List<Announcement>> {
        return try {
            val snapshot = pathProvider.announcements(collegeId)
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

    override fun generateAnnouncementId(): String = java.util.UUID.randomUUID().toString()

    override suspend fun uploadAnnouncementImage(collegeId: String, announcementId: String, imageUri: Uri): Result<Pair<String, String>> {
        return try {
            val path = StoragePathGenerator.announcementBanner(collegeId, announcementId)
            storageManager.uploadImage(
                bucket = StorageConstants.MEDIA_BUCKET,
                path = path,
                imageUri = imageUri
            ).map { url -> Pair(url, path) }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadAnnouncementAttachment(collegeId: String, announcementId: String, attachmentUri: Uri): Result<Pair<String, String>> {
        return try {
            val path = StoragePathGenerator.announcementAttachment(collegeId, announcementId, "pdf")
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
        return storageManager.deleteFile(bucket = StorageConstants.MEDIA_BUCKET, path = path)
    }
}
